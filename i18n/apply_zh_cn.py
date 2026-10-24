#!/usr/bin/env python3
"""Apply the zh-CN mapping to Kotlin string literals before building.

Only whole string literals whose raw content exactly equals a key in
i18n/zh-CN.json are replaced. Comments, char literals and anything that is
not an exact match are left untouched, so protocol commands and identifiers
are never changed. Upstream sources stay unmodified in git; this runs in CI
(or locally) on the working tree only.

Usage:
    python3 i18n/apply_zh_cn.py            # apply to app/src/main/java
    python3 i18n/apply_zh_cn.py --dry-run  # report only, write nothing
"""

import argparse
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DEFAULT_MAP = ROOT / "i18n" / "zh-CN.json"
DEFAULT_SRC = ROOT / "app" / "src" / "main" / "java"

def is_valid_body(body):
    """True if body parses as exactly one Kotlin string literal's content."""
    wrapped = '"' + body + '"'
    lits = []
    end = scan_string(wrapped, 1, lits)
    return end == len(wrapped) and lits == [(1, len(wrapped) - 1)]


def scan_code(src, i, in_template=False):
    """Scan Kotlin code starting at i.

    Returns (literals, end) where literals is a list of
    (content_start, content_end) spans of top-level string literals.
    When in_template is True, scanning stops after the matching '}'.
    """
    literals = []
    depth = 0
    n = len(src)
    while i < n:
        c = src[i]
        if src.startswith("//", i):
            j = src.find("\n", i)
            i = n if j == -1 else j
            continue
        if src.startswith("/*", i):
            j = src.find("*/", i + 2)
            i = n if j == -1 else j + 2
            continue
        if src.startswith('"""', i):
            j = src.find('"""', i + 3)
            i = n if j == -1 else j + 3
            continue
        if c == '"':
            start = i + 1
            i = scan_string(src, start, literals)
            continue
        if c == "'":
            # Char literal: 'x' or '\x' or '\u0000'
            if i + 1 < n and src[i + 1] == "\\":
                j = src.find("'", i + 3)
                i = n if j == -1 else j + 1
            else:
                i += 3
            continue
        if in_template:
            if c == "{":
                depth += 1
            elif c == "}":
                if depth == 0:
                    return literals, i + 1
                depth -= 1
        i += 1
    return literals, i


def scan_string(src, i, literals):
    """Scan a string body starting at i (just after the opening quote).

    Appends (start, end) for this literal to literals and returns the index
    after the closing quote. Nested literals inside ${...} are skipped.
    """
    start = i
    n = len(src)
    while i < n:
        c = src[i]
        if c == "\\":
            i += 2
            continue
        if c == '"':
            literals.append((start, i))
            return i + 1
        if c == "$" and i + 1 < n and src[i + 1] == "{":
            _, i = scan_code(src, i + 2, in_template=True)
            continue
        if c == "\n":
            # Unterminated literal (should not happen in valid Kotlin)
            return i
        i += 1
    return n


def load_mapping(path):
    with open(path, encoding="utf-8") as f:
        mapping = json.load(f)
    bad = [k for k, v in mapping.items() if not is_valid_body(v)]
    if bad:
        for k in bad:
            print(f"ERROR: invalid Kotlin string body in value for key: {k!r}")
        sys.exit(1)
    return mapping


def process_file(path, mapping, hits, apply):
    src = path.read_text(encoding="utf-8")
    literals, _ = scan_code(src, 0)
    pieces = []
    last = 0
    changed = False
    for start, end in literals:
        raw = src[start:end]
        if raw in mapping:
            hits[raw] = hits.get(raw, 0) + 1
            pieces.append(src[last:start])
            pieces.append(mapping[raw])
            last = end
            changed = True
    if changed and apply:
        pieces.append(src[last:])
        path.write_text("".join(pieces), encoding="utf-8")
    return sum(1 for s, e in literals if src[s:e] in mapping)


def main():
    parser = argparse.ArgumentParser(description=__doc__.split("\n")[0])
    parser.add_argument("--map", type=Path, default=DEFAULT_MAP)
    parser.add_argument("--src", type=Path, default=DEFAULT_SRC)
    parser.add_argument("--dry-run", action="store_true")
    args = parser.parse_args()

    mapping = load_mapping(args.map)
    files = sorted(args.src.rglob("*.kt"))
    if not files:
        print(f"ERROR: no .kt files under {args.src}")
        sys.exit(1)

    hits = {}
    total = 0
    for f in files:
        total += process_file(f, mapping, hits, apply=not args.dry_run)

    unmatched = [k for k in mapping if k not in hits]
    mode = "dry-run" if args.dry_run else "applied"
    print(f"zh-CN {mode}: {total} literal(s) across {len(files)} file(s), "
          f"{len(hits)}/{len(mapping)} key(s) matched")
    for k in unmatched:
        print(f"WARNING: no match for key (upstream may have changed): {k!r}")


if __name__ == "__main__":
    main()
