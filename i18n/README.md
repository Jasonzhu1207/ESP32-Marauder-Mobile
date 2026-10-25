# 简体中文汉化

本目录只保存汉化映射，**不修改上游源码**，因此同步上游不会产生冲突。

- `zh-CN.json`：原文 → 译文映射。键是 Kotlin 字符串字面量的原始内容（保留 `\"` 等转义，`${...}` 按源码原样写）。
- `apply_zh_cn.py`：在构建前把映射精确替换进 `app/src/main/java`。只替换整段字面量，不会误伤命令或协议字符串。

## 本地检查

```sh
python3 i18n/apply_zh_cn.py --dry-run   # 只统计，不写文件
python3 i18n/apply_zh_cn.py             # 直接改写工作区（请勿提交）
```

输出中 `N/M key(s) matched` 若 N < M，说明上游改了某条原文，需要更新对应键。

## 构建

在 GitHub Actions 中手动运行 **Build 简体中文版 APK**（`workflow_dispatch`）：

1. 默认先把上游 `0xsys/ESP32-Marauder-Mobile` 的 `main` 合并到构建环境中（不写入本仓库）。
2. 应用映射（只在 CI 工作区内生效）。
3. 构建 Release APK，上传为 artifact，并打 tag `zh-cn-v<版本>-r<运行号>` 发布 Release。

### 签名

若要让每次构建使用同一个签名（才能覆盖升级），在仓库 Secrets 中设置：

- `KEYSTORE_BASE64`：`base64 -w0 your.jks`
- `KEYSTORE_PASSWORD`、`KEY_ALIAS`、`KEY_PASSWORD`

未设置时使用临时密钥，并会给出警告。

## 功能说明（“!”图标）

功能卡片上的小叹号打开的用途说明，写在 `app/src/main/java/com/marauder/mobile/data/MenuHelp.kt` 中，**源码即中文**，因此英文上游版也会显示这些中文说明。`apply_zh_cn.py` 只按映射替换英文原文，不会改动这些中文文本。新增说明时按命令字符串（或 `submenu:<id>`）作为键添加即可。

## 维护原则

- 只翻译界面文字（菜单标题与说明、按钮、标签、对话框、提示）。
- 品牌与技术术语保留英文：WiFi、AP、STA、SSID、BSSID、PMKID、Deauth、Beacon、Probe、Karma、Marauder、ESP32、板卡名等。
- 串口命令（如 `scanall`、`sniffprobe`、`evilportal -c start`）、固件协议与日志保持英文，不翻译。控制台输出一般保持英文，但同时出现在提示条中的少数消息（如 "Disconnected"）已翻译。
