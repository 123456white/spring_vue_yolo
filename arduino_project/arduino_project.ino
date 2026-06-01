#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

// 定义 WIFI 名称常量
const char* WIFINAME = "ESP";
// 定义 WIFI 密码常量
const char* WIFIPASSWORD = "12345678";

// 创建一个 WiFiUDP 对象
WiFiUDP udp;
// 定义远程服务器端口号
const int remotePort = 8282;
// 定义远程服务器 IP 地址
const char* remoteIP = "192.168.155.165";
// 这里可以替换为你要发送的实际变量
int variableToSend = 42; 

// 参数一为 WIFI 名称,参数二为 WIFI 密码,参数三为设定的最长等待时间
void connectWifi(const char* wifiName, const char* wifiPassword, uint8_t waitTime) {
    // 设置 ESP8266 为无线终端模式
    WiFi.mode(WIFI_STA);
    // 清除之前的 WIFI 配置缓存
    WiFi.disconnect();
    // 开始连接指定的 WIFI 网络
    WiFi.begin(wifiName, wifiPassword);
    uint8_t count = 0;
    // 当 WIFI 状态不是已连接状态时循环等待
    while (WiFi.status()!= WL_CONNECTED) {
        // 等待 1 秒
        delay(3000);
        // 打印连接 WIFI 的等待时间
        Serial.printf("connect WIFI...%ds\r\n", ++count);
        // 如果等待时间超过设定的最长等待时间则退出循环
        if (count >= waitTime) {
            Serial.println("connect WIFI fail");
            return;
        }
    }
    // 连接成功后打印连接的 WIFI 名称和本地 IP 地址
    Serial.printf("connect WIFI %s success,local IP is %s\r\n", WiFi.SSID().c_str(), WiFi.localIP().toString().c_str());
}



void setup() {
    // put your setup code here, to run once:
    // 初始化串口通信，波特率为 9600
    Serial.begin(9600);
    Serial.println();
    // 连接 WIFI 网络
    connectWifi(WIFINAME, WIFIPASSWORD, 15);
    // 启动 UDP 服务，监听本地端口 8282
    udp.begin(8282);
}

void loop() {
    // put your main code here, to run repeatedly:
    // 如果 WIFI 状态不是已连接状态
    if (WiFi.status()!= WL_CONNECTED) {
        // 打印提示信息并尝试重新连接 WIFI
        Serial.println("WIFI is break,try to connect...");
        connectWifi(WIFINAME, WIFIPASSWORD, 15);
    }

    // 发送 UDP 数据
    udp.beginPacket(remoteIP, remotePort);
    // 发送 "Hello from ESP8266 via UDP!" 字符串
    //udp.print("Hello from ESP8266 via UDP!");
    variableToSend = ++variableToSend;
    // 将变量转换为字符串并发送
    udp.print(String(variableToSend).c_str());
    // 延迟 1 秒
    delay(3000);
    udp.endPacket();

    // 检查是否有接收到的 UDP 数据
    int packetSize = udp.parsePacket();
    if (packetSize) {
        char buffer[255];
        // 读取接收到的数据并存入 buffer 数组中，最多读取 255 个字节
        int len = udp.read(buffer, 255);
        if (len > 0) {
            buffer[len] = 0;
            // 打印接收到的 UDP 数据包内容
            Serial.printf("Received UDP packet: %s\n", buffer);
        }
    }
}
