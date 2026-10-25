package com.marauder.mobile.data

data class MenuHelpText(val purpose: String, val usage: String = "")

/** Looked up by the raw firmware command (or submenu id), so titles can differ between screens. */
object MenuHelp {

    fun forItem(item: MenuItem): MenuHelpText? = when (val action = item.action) {
        is MenuAction.Send -> helpByKey[action.command]
        is MenuAction.OpenAnalyzer -> helpByKey[action.command]
        is MenuAction.Submenu -> helpByKey["submenu:" + action.id]
        MenuAction.OpenEvilPortal -> helpByKey["evilportal:phone"]
        else -> null
    }

    private val helpByKey: Map<String, MenuHelpText> = mapOf(
        // ---------------- WiFi / Sniffers ----------------
        "sniffprobe" to MenuHelpText(
            "监听周围设备发出的「探测请求」（Probe Request）。手机等设备找以前连过的 WiFi 时，会广播它记住的网络名（SSID），这里就是把这些名字抓下来。",
            "点击开始监听，需要停止时点顶部的停止按钮。",
        ),
        "sniffbeacon" to MenuHelpText(
            "监听 WiFi 热点周期性广播的「信标帧」（Beacon），收集附近 AP 的名称、信道等信息，用来了解周围有哪些 WiFi。",
            "点击开始监听，需要停止时点顶部的停止按钮。",
        ),
        "sniffdeauth" to MenuHelpText(
            "监听「解除认证」数据包（Deauthentication）。这类包可能来自断线攻击，也可能是正常断连，抓到后仅供参考。",
            "点击开始监听，需要停止时点顶部的停止按钮。",
        ),
        "sniffpmkid" to MenuHelpText(
            "抓取 WPA 握手包（EAPOL）和 PMKID，保存为 PCAP 文件到 SD 卡，之后可拿到电脑上离线分析。",
            "需要插入 SD 卡。仅用于检测你自己有权测试的网络的密码强度。",
        ),
        "sniffraw" to MenuHelpText(
            "抓取所有原始 802.11 无线帧，保存为 PCAP 文件到 SD 卡，可用 Wireshark 等工具打开分析。",
            "需要插入 SD 卡。点击开始，需要停止时点顶部的停止按钮。",
        ),
        "analyzer -t wifi" to MenuHelpText(
            "以实时曲线图显示各 WiFi 信道的分析数据（Channel Analyzer），用来观察各信道的活动变化。",
            "进入页面即开始显示实时图表。",
        ),
        "analyzer -t chan" to MenuHelpText(
            "查看各 WiFi 信道的活跃程度（固件中名为 Channel Activity），以条形显示。",
            "进入页面即开始显示实时数据。",
        ),
        "sniffpwn" to MenuHelpText(
            "检测附近是否有 Pwnagotchi（一种自动化 WiFi 抓包的小设备）的特征信号。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "sniffpinescan" to MenuHelpText(
            "检测附近是否有 WiFi Pineapple 这类渗透测试设备的特征，这类设备常用来伪造热点。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "sniffmultissid" to MenuHelpText(
            "检测是否有同一台设备同时广播多个不同的热点名（SSID），这种情况常见于伪造热点的设备。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "scanall" to MenuHelpText(
            "主动扫描周围的 WiFi 热点（AP）和连接在它们上面的客户端设备（STA）。结果分别出现在「Access Points」和「Stations」列表里。",
            "很多攻击和 MAC 克隆功能都要先扫描，用来获得目标列表。",
        ),
        "sigmon" to MenuHelpText(
            "追踪单个目标的信号强度：持续显示一个已发现设备的信号强度（RSSI），用于在现场判断它的大致方位。通常信号越强，说明离你越近。",
            "先扫描并选择目标类型，再从列表中选择要追踪的目标。",
        ),
        "mactrack" to MenuHelpText(
            "持续记录周围 WiFi 设备的 MAC 地址（设备的唯一硬件编号），并标记出那些一直跟随你移动的设备（Following）。",
            "点击开始记录，需要停止时点顶部的停止按钮。",
        ),
        "sniffsae" to MenuHelpText(
            "捕获 WPA3 网络握手过程中的 SAE Commit 帧，用于分析 WPA3 的握手过程。",
            "如果已选中 AP，只监听它们所在的信道；未选中时会轮流扫描所有信道。",
        ),

        // ---------------- WiFi / Scanners ----------------
        "pingscan" to MenuHelpText(
            "向当前连接的 WiFi 网段逐个发送 ping 请求，找出在线的主机，并把它们加入 IP 列表（IP List）。",
            "需要先连接到一个 WiFi 网络（可在 General → Join Saved WiFi 连接已保存的网络）。",
        ),
        "arpscan -f" to MenuHelpText(
            "用 ARP 请求探测同一局域网中在线的设备。",
            "建议先连接到目标 WiFi 网络再使用。",
        ),
        "portscan -a -t 0" to MenuHelpText(
            "对 IP 列表中序号为 0 的那台主机做全端口扫描，检查它开放了哪些端口。",
            "需要先用 Ping Scan 生成 IP 列表。列表为空或没有序号 0 时不会执行。",
        ),
        "portscan -s ssh" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 SSH 端口（22）。SSH 是常见的远程登录服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s telnet" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 Telnet 端口（23）。Telnet 是较旧的明文远程登录服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s smtp" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 SMTP 端口（25），即邮件发送服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s dns" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 DNS 端口（53），即域名解析服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s http" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 HTTP 端口（80），即普通网页服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s https" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 HTTPS 端口（443），即加密网页服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),
        "portscan -s rdp" to MenuHelpText(
            "在本地网络中逐个检查主机是否开放 RDP 端口（3389），即 Windows 远程桌面服务。",
            "需要先连接到 WiFi 网络，程序会自动扫描本地网段。",
        ),

        // ---------------- WiFi / Attacks ----------------
        "attack -t beacon -l" to MenuHelpText(
            "按 SSID 列表广播假热点的信标帧（Beacon），让周围设备的 WiFi 列表里出现列表中的假名字。",
            "需要先准备 SSID 列表（可在 General → SSIDs 查看当前列表）。点顶部的停止按钮结束。",
        ),
        "attack -t beacon -r" to MenuHelpText(
            "随机生成大量假热点名称并广播，用来塞满周围设备的 WiFi 列表。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "attack -t funny" to MenuHelpText(
            "广播固件内置的一组趣味假热点名称。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "attack -t rickroll" to MenuHelpText(
            "广播一组歌词风格的假热点名称（例如 Never gonna give you up），属于恶搞性质。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "attack -t beacon -a" to MenuHelpText(
            "复制已选中的 AP，并以假热点的形式大量广播这些名称。",
            "需要先扫描（Scan AP/STA）并选中目标 AP。",
        ),
        "attack -t probe" to MenuHelpText(
            "向已选中的 AP 大量发送探测请求（Probe Request）数据包。",
            "需要先扫描并选中目标 AP。点顶部的停止按钮结束。",
        ),
        "attack -t deauth" to MenuHelpText(
            "向已选中的 AP 反复发送「解除认证」帧（Deauth），把连接在该 AP 上的设备强制踢下线。",
            "需要先扫描并选中目标 AP。点顶部的停止按钮结束。",
        ),
        "attack -t deauth -c" to MenuHelpText(
            "与 Deauth Flood 类似，但只针对你选中的具体客户端设备（Station），而不是整个 AP。",
            "需要先扫描并选中目标客户端设备。点顶部的停止按钮结束。",
        ),
        "evilportal:phone" to MenuHelpText(
            "在手机上选一个 HTML 登录页，上传到设备（不需要 SD 卡），然后冒充一个已扫描到的 AP，建立假热点和假登录页。运行后可以在应用中看到被输入的账号密码。",
            "先进入「Scan APs first」扫描并选择要冒充的目标 AP，再运行本项并选择网页文件。",
        ),
        "karma" to MenuHelpText(
            "伪造一个热点，名字取自设备之前在探测请求中发出的 SSID（即它记得的旧 WiFi 名），用来测试设备是否会自动连上假热点。",
            "固件要求带序号：karma -p <序号>，序号指固件内部记录的探测请求 SSID 列表，可先用「Probe Request Sniff」抓取探测请求。" +
                "注意：本应用的 Karma 按钮发送的是不带序号的 karma，固件会直接忽略，可能没有任何反应。可以在控制台里手动输入带序号的命令。",
        ),
        "attack -t badmsg" to MenuHelpText(
            "向所选 AP 的客户端发送伪造的 WPA 四次握手第 1 条消息（Msg 1），干扰其连接过程。",
            "需要先扫描并选中目标 AP。作用于所选 AP 的所有客户端。",
        ),
        "attack -t badmsg -c" to MenuHelpText(
            "与 Bad Msg 相同，但只作用于你选中的客户端设备。",
            "需要先扫描并选中目标客户端设备。",
        ),
        "attack -t sleep" to MenuHelpText(
            "伪造带有省电（sleep）标志的关联请求（Association Request），干扰目标客户端与 AP 之间的正常通信。",
            "需要先扫描并选中目标 AP。作用于所选 AP 的所有客户端。",
        ),
        "attack -t sleep -c" to MenuHelpText(
            "与 Assoc Sleep 相同，但只作用于你选中的客户端设备。",
            "需要先扫描并选中目标客户端设备。",
        ),
        "attack -t sae" to MenuHelpText(
            "针对 WPA3 网络的资源消耗攻击：向目标 AP 发送大量构造的 SAE Commit 帧，迫使 AP 反复进行高开销计算。",
            "建议先扫描并选中目标 AP。点顶部的停止按钮结束。",
        ),
        "attack -t csa" to MenuHelpText(
            "伪造信道切换通告（Channel Switch Announcement），让所选 AP 下的设备切换到另一个信道，从而干扰通信。",
            "需要先扫描并选中目标 AP。",
        ),
        "attack -t quiet" to MenuHelpText(
            "伪造静默通告（Quiet），让所选 AP 下的设备在一段时间内停止发送数据。",
            "需要先扫描并选中目标 AP。",
        ),
        "submenu:evil_portal" to MenuHelpText(
            "Evil Portal（假门户）：让连上来的设备看到一个伪造的热点和登录网页，用来测试用户是否会在假页面上输入账号密码。",
            "通常先在「Scan APs first」中选择目标 AP，再使用「Portal (phone HTML)」从手机上传网页，或使用「Start (SD index.html)」启动 SD 卡中的页面。",
        ),
        "evilportal -c start -w index.html" to MenuHelpText(
            "用 SD 卡中名为 index.html 的网页作为假登录页，启动 Evil Portal。",
            "需要先把 index.html 放入 SD 卡。",
        ),

        // ---------------- Bluetooth ----------------
        "sniffbt -t flipper" to MenuHelpText(
            "检测附近 Flipper Zero 设备发出的蓝牙广播。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "sniffbt -t airtag" to MenuHelpText(
            "抓取附近 AirTag 的蓝牙广播信息，之后可用于「Spoof Airtag」。",
            "先用本项抓取 AirTag，再使用 Spoof Airtag。",
        ),
        "analyzer -t bt" to MenuHelpText(
            "以实时曲线图显示附近蓝牙（BLE）的活动情况（BT Analyzer）。",
            "进入页面即开始显示实时图表。",
        ),
        "sniffbt -t flock" to MenuHelpText(
            "检测附近可能属于 Flock Safety 监控摄像头的蓝牙信号。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "sniffbt -t meta" to MenuHelpText(
            "检测附近可能属于 Meta 设备（如 Ray-Ban 智能眼镜）的蓝牙信号。",
            "点击开始检测，需要停止时点顶部的停止按钮。",
        ),
        "sniffskim" to MenuHelpText(
            "扫描附近的蓝牙设备，尝试识别可能的信用卡盗刷器（CC Skimmer）。识别结果仅供参考，需人工核实。",
            "点击开始扫描，需要停止时点顶部的停止按钮。",
        ),
        "blespam -t sourapple" to MenuHelpText(
            "发送伪造的 Apple 蓝牙广播。据项目 Wiki 描述，它可能导致 iOS 17 的 iPhone 崩溃。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t applejuice" to MenuHelpText(
            "向附近的 Apple 设备发送伪造的蓝牙配对广播，可能使其频繁弹出配对提示。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t windows" to MenuHelpText(
            "发送随机 MAC 的 Swift Pair 蓝牙广播，可能使附近的 Windows 电脑弹出配对通知。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t samsung" to MenuHelpText(
            "伪造三星设备的配对广播。据项目 Wiki 描述，受影响的三星设备在运行期间可能几乎无法正常使用。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t google" to MenuHelpText(
            "发送伪造的 Google 设备蓝牙广播，可能使附近的 Android 手机弹出配对提示。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t flipper" to MenuHelpText(
            "随机生成 Flipper Zero 风格的蓝牙广播（随机 MAC、设备名和 UUID），伪造附近有 Flipper Zero 的假象。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "blespam -t all" to MenuHelpText(
            "运行固件中所有蓝牙 spam 类型（如 Sour Apple、Samsung、Swift Pair 等）。",
            "点击开始，点顶部的停止按钮结束。",
        ),
        "spoofat -t 0" to MenuHelpText(
            "用「Airtag Sniff」抓到的 AirTag 数据，模拟该 AirTag 发出蓝牙广播。",
            "必须先用 Airtag Sniff 抓到 AirTag。默认使用列表中序号为 0 的那个。",
        ),

        // ---------------- GPS ----------------
        "wardrive" to MenuHelpText(
            "用手机的 GPS 定位，同时持续扫描周围的 WiFi，把热点名称、MAC、加密方式和位置记录成 WiGLE 格式的文件，可用于上传到 WiGLE 网站。",
            "文件保存在手机上，扩展名为 .log。上传到 WiGLE 前请把扩展名改为 .csv。",
        ),
        "gpsdata" to MenuHelpText(
            "显示 GPS 模块当前的定位数据（如经纬度）。",
            "需要接入 GPS 模块。",
        ),
        "nmea" to MenuHelpText(
            "显示 GPS 模块输出的原始 NMEA 数据（GPS 的标准文本格式），用于调试 GPS。",
            "需要接入 GPS 模块。点顶部的停止按钮结束。",
        ),
        "gpstracker -c start" to MenuHelpText(
            "用 GPS 模块持续记录移动轨迹，保存为 SD 卡上的 .gpx 文件，可在地图软件中查看。",
            "需要接入 GPS 模块并插入 SD 卡。结束时使用「Stop Tracker」。",
        ),
        "submenu:gps_poi" to MenuHelpText(
            "在 GPS 定位下记录兴趣点（POI），即标记你希望以后再回来的地点。",
            "先点 Start POI 开始，在需要记录的地点点 Mark POI 标记，结束时点 End POI。",
        ),

        // ---------------- Device ----------------
        "brightness -c" to MenuHelpText(
            "依次切换屏幕亮度的几个档位。",
            "每点一次切换到下一档。",
        ),
        "submenu:device_files" to MenuHelpText(
            "把扫描到的 AP 列表或 SSID 列表保存到 SD 卡，或从 SD 卡读回，方便下次直接使用，不必重新扫描。",
            "需要插入 SD 卡。",
        ),
        "update -s" to MenuHelpText(
            "从 SD 卡根目录读取固件文件 update.bin，并用它升级设备固件。",
            "先把固件的 .bin 文件改名为 update.bin 并放到 SD 卡根目录，再点击本项。升级过程中不要断电或断开连接。",
        ),

        // ---------------- General ----------------
        "join -s" to MenuHelpText(
            "使用设备上已保存的 WiFi 信息，自动连接到之前连过的网络。",
            "需要先通过 Join 功能连接过一次网络，设备才会保存它。",
        ),
        "stopscan -f" to MenuHelpText(
            "停止当前所有扫描和攻击，并断开 WiFi 连接。",
            "无需额外设置，直接点击即可。",
        ),
        "submenu:wifi_macs" to MenuHelpText(
            "修改或随机化设备的 MAC 地址（网卡的唯一硬件编号）。测试时可用来伪装成某台设备。",
            "随机生成直接点击；克隆需要先用 Scan AP/STA 扫描出目标。",
        ),
        "randapmac" to MenuHelpText(
            "随机生成一个新的 AP（热点）MAC 地址，并设置给设备。",
            "直接点击即可。",
        ),
        "randstamac" to MenuHelpText(
            "随机生成一个新的 STA（客户端）MAC 地址，并设置给设备。",
            "直接点击即可。",
        ),
        "cloneapmac -a 0" to MenuHelpText(
            "把设备的 AP（热点）MAC 设为扫描结果中序号为 0 的 AP 的 MAC，相当于复制它。",
            "需要先用 Scan AP/STA 扫描。扫描结果为空时不会生效。",
        ),
        "clonestamac -s 0" to MenuHelpText(
            "把设备的 STA（客户端）MAC 设为扫描结果中序号为 0 的客户端的 MAC。",
            "需要先用 Scan AP/STA 扫描。扫描结果为空时不会生效。",
        ),
    )
}
