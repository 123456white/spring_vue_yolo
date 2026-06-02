#include "esp_camera.h"
#include <Arduino.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <Ticker.h>
#include <ArduinoJson.h>
#define CAMERA_MODEL_AI_THINKER  // Has PSRAM
#include "camera_pins.h"
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------1.变量定义与函数声明*start----------------------------------------
const char *ssid = "5G";
const char *password = "123456789";
const char *mqtt_server = "mqtts.heclouds.com";  //onenet 的 IP地址 mqtts.heclouds.com 183.230.40.96
const int port = 1883;                           //端口号

#define mqtt_pubid "5ge5BdeW12"  //产品ID
#define mqtt_devid "LoRa_cam"    //设备名称
#define mqtt_password "version=2018-10-31&res=products%2F5ge5BdeW12%2Fdevices%2FLoRa_cam&et=1923796800&method=md5&sign=Pp%2FOcIi5OlobEB1oBkkD7w%3D%3D"
//设备上传数据的post主题
#define ONENET_TOPIC_PROP_POST "$sys/" mqtt_pubid "/" mqtt_devid "/thing/property/post"
//接收下发属性设置主题
#define ONENET_TOPIC_PROP_SET "$sys/" mqtt_pubid "/" mqtt_devid "/thing/property/set"
//这是post上传数据使用的模板
#define ONENET_POST_BODY_FORMAT "{\"id\": \"%d\",\"params\": %s}"
int postMsgId = 0;  //记录已经post了多少条

WiFiClient espClient;            //创建一个WIFI连接客户端
PubSubClient client(espClient);  // 创建一个PubSub客户端, 传入创建的WIFI客户端
Ticker tim1;                     //定时器,用来循环上传数据

bool cam_key_value = false;  //获取图像按钮是否打开

void startCameraServer();
void stopCameraServer();
void setupLedFlash(int pin);
void connectWifi();                                              //连网
void clientReconnect();                                          //MQTT重连
void callback(char *topic, byte *payload, unsigned int length);  //回调函数
void sendIP();                                                   //发送IP
//-------------------------------------------1.变量定义与函数声明*end----------------------------------------

////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------2.初始化-start-------------------------------------------------
void setup() {
  Serial.begin(115200);
  Serial.setDebugOutput(true);
  Serial.println();

  camera_config_t config;
  config.ledc_channel = LEDC_CHANNEL_0;
  config.ledc_timer = LEDC_TIMER_0;
  config.pin_d0 = Y2_GPIO_NUM;
  config.pin_d1 = Y3_GPIO_NUM;
  config.pin_d2 = Y4_GPIO_NUM;
  config.pin_d3 = Y5_GPIO_NUM;
  config.pin_d4 = Y6_GPIO_NUM;
  config.pin_d5 = Y7_GPIO_NUM;
  config.pin_d6 = Y8_GPIO_NUM;
  config.pin_d7 = Y9_GPIO_NUM;
  config.pin_xclk = XCLK_GPIO_NUM;
  config.pin_pclk = PCLK_GPIO_NUM;
  config.pin_vsync = VSYNC_GPIO_NUM;
  config.pin_href = HREF_GPIO_NUM;
  config.pin_sccb_sda = SIOD_GPIO_NUM;
  config.pin_sccb_scl = SIOC_GPIO_NUM;
  config.pin_pwdn = PWDN_GPIO_NUM;
  config.pin_reset = RESET_GPIO_NUM;
  config.xclk_freq_hz = 10000000;
  config.frame_size = FRAMESIZE_UXGA;
  config.pixel_format = PIXFORMAT_JPEG; // for streaming
  //config.pixel_format = PIXFORMAT_RGB565; // for face detection/recognition
  config.grab_mode = CAMERA_GRAB_WHEN_EMPTY;
  config.fb_location = CAMERA_FB_IN_PSRAM;
  config.jpeg_quality = 12;
  config.fb_count = 1;
  
  // if PSRAM IC present, init with UXGA resolution and higher JPEG quality
  //                      for larger pre-allocated frame buffer.
  if(config.pixel_format == PIXFORMAT_JPEG){
    if(psramFound()){
      config.jpeg_quality = 10;
      config.fb_count = 2;
      config.grab_mode = CAMERA_GRAB_LATEST;
    } else {
      // Limit the frame size when PSRAM is not available
      config.frame_size = FRAMESIZE_SVGA;
      config.fb_location = CAMERA_FB_IN_DRAM;
    }
  } else {
    // Best option for face detection/recognition
    config.frame_size = FRAMESIZE_240X240;
#if CONFIG_IDF_TARGET_ESP32S3
    config.fb_count = 2;
#endif
  }

#if defined(CAMERA_MODEL_ESP_EYE)
  pinMode(13, INPUT_PULLUP);
  pinMode(14, INPUT_PULLUP);
#endif

  // camera init
  esp_err_t err = esp_camera_init(&config);
  if (err != ESP_OK) {
    Serial.printf("Camera init failed with error 0x%x", err);
    return;
  }

  sensor_t * s = esp_camera_sensor_get();
  // initial sensors are flipped vertically and colors are a bit saturated
  if (s->id.PID == OV3660_PID) {
    s->set_vflip(s, 1); // flip it back
    s->set_brightness(s, 1); // up the brightness just a bit
    s->set_saturation(s, -2); // lower the saturation
  }
  // drop down frame size for higher initial frame rate
  if(config.pixel_format == PIXFORMAT_JPEG){
    s->set_framesize(s, FRAMESIZE_QVGA);
  }

#if defined(CAMERA_MODEL_M5STACK_WIDE) || defined(CAMERA_MODEL_M5STACK_ESP32CAM)
  s->set_vflip(s, 1);
  s->set_hmirror(s, 1);
#endif

#if defined(CAMERA_MODEL_ESP32S3_EYE)
  s->set_vflip(s, 1);
#endif

// Setup LED FLash if LED pin is defined in camera_pins.h
#if defined(LED_GPIO_NUM)
  setupLedFlash(LED_GPIO_NUM);
#endif

  //WIFI连接
  connectWifi();

  client.setServer(mqtt_server, port);  //设置客户端连接的服务器,连接Onenet服务器, 使用1883端口
  delay(2000);
  Serial.println("set Onenet Server Init!");
  client.connect(mqtt_devid, mqtt_pubid, mqtt_password);  //客户端连接到指定的产品的指定设备.同时输入鉴权信息
  delay(2000);
  Serial.println("Onenet device connect Init!");
  if (client.connected()) {
    Serial.println("OneNet is connected!");  //判断以下是不是连好了.
  } else {
    while (!client.connected()) {
      client.connect(mqtt_devid, mqtt_pubid, mqtt_password);
    }
    Serial.println("OneNet is connected!");
  }
  
  client.subscribe(ONENET_TOPIC_PROP_SET);
  client.setCallback(callback);
  tim1.attach(20, sendIP);  //定时每20秒调用一次发送数据函数
}
//-------------------------------------------2.初始化-end-----------------------------------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------3.循环处理-start-------------------------------------------------
void loop() {
  // 如果 WIFI 状态不是已连接状态
  if (WiFi.status() != WL_CONNECTED) {
    // 打印提示信息并尝试重新连接 WIFI
    Serial.println("WIFI is break,try to connect...");
    connectWifi();
  }
  if (!client.connected())  //如果客户端没连接ONENET, 重新连接
  {
    clientReconnect();
    delay(100);
  }
  client.loop();  //客户端循环检测
  delay(100);
}
//-------------------------------------------3.循环处理-end---------------------------------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------4.Wifi连接-start-------------------------------------------------
void connectWifi() {
  // 设置 ESP 为无线终端模式
  WiFi.mode(WIFI_STA);
  // 清除之前的 WIFI 配置缓存
  WiFi.disconnect();
  Serial.print("connect WIFI");
  // 开始连接指定的 WIFI 网络
  WiFi.begin(ssid, password);
  uint8_t count = 0;
  // 当 WIFI 状态不是已连接状态时循环等待
  while (!WiFi.isConnected()) {
    // 等待 1 秒
    delay(3000);
    Serial.print(".");
    // 如果等待时间超过设定的最长等待时间则退出循环
  }
  // 连接成功后打印连接的 WIFI 名称和本地 IP 地址
  Serial.printf("connect WIFI %s success\r\n", WiFi.SSID().c_str());
  Serial.printf("http://%s\r\n", WiFi.localIP().toString().c_str());
  delay(1000);
}
//-------------------------------------------4.Wifi连接-end---------------------------------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------5.MQTT重连-start-------------------------------------------------
//重连函数, 如果客户端断线,可以通过此函数重连
void clientReconnect() {
  while (!client.connected())  //再重连客户端
  {
    Serial.println("reconnect MQTT...");
    if (client.connect(mqtt_devid, mqtt_pubid, mqtt_password)) {
      Serial.println("MQTT connected!");
    } else {
      Serial.println("failed");
      Serial.println(client.state());
      Serial.println("try again in 5 sec");
      delay(5000);
    }
  }
}
//-------------------------------------------5.MQTT重连-end---------------------------------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------6.回调函数-start-------------------------------------------------
void callback(char *topic, byte *payload, unsigned int length) {
  Serial.println("Onenet message rev:");
  //Serial.println(topic);
  char message[length + 1];
  for (size_t i = 0; i < length; i++) {
    message[i] = (char)payload[i];
  }
  message[length] = '\0';

  StaticJsonDocument<200> doc;
  DeserializationError error = deserializeJson(doc, message);
  if (error) {
    Serial.println("Failed to parse JSON");
    return;
  }
  // 检查是否存在"params"和"CAM_key"
  if (doc.containsKey("params") && doc["params"].containsKey("CAM_key")) {
    cam_key_value = doc["params"]["CAM_key"];
    Serial.print("CAM_key value: ");
    Serial.println(cam_key_value);
    if (cam_key_value) {
      startCameraServer();
      Serial.print("Camera Ready!\r\n");
    } else if (!cam_key_value) {
      
      stopCameraServer();
      Serial.print("Camera close!\r\n");
      // Serial.println("Rebooting ESP32...");
      // ESP.restart();
    }
  }
}
//-------------------------------------------6.回调函数-end---------------------------------------------------

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
//-------------------------------------------7.数据发送-start-------------------------------------------------
//向主题发送IP数据
void sendIP() {
  if (client.connected()) {
    //先拼接出json字符串
    char param[82];
    char jsonBuf[178];
    sprintf(param, "{\"CAM_IP\": {\"value\": \"%s\"}}", WiFi.localIP().toString().c_str());  //我们把要上传的数据写在param里
    postMsgId += 1;
    sprintf(jsonBuf, ONENET_POST_BODY_FORMAT, postMsgId, param);
    //再从mqtt客户端中发布post消息
    if (client.publish(ONENET_TOPIC_PROP_POST, jsonBuf)) {
      Serial.print("Post message to Onenet: \n");
      Serial.println(jsonBuf);
    } else {
      Serial.println("Publish message to cloud failed!");
    }
  }
}
//-------------------------------------------7.数据发送-end---------------------------------------------------