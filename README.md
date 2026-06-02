# 玉米去雄情况检测系统

基于 **Spring Boot + Vue 2 + YOLOv8 + ESP8266 + OneNet** 的智能农业综合管理平台，实现玉米去雄图像的深度学习识别、多区域环境数据采集与远程设备控制。

---

## 项目结构

```
spring_vue_yolo/
├── Springboot_project/      # Java 后端服务 (Spring Boot 2.6)
├── vue_project/             # Vue 前端界面 (Vue 2 + Element UI)
├── yolo_web/                # YOLO 图像检测服务 (Python)
└── arduino_project/         # ESP8266 物联网数据采集终端
```

---

## 系统架构

```
┌──────────────────────────────────────────────────────────┐
│                      前端 (Vue 2)                         │
│  登录/注册 → 用户管理 → 去雄检测 → 环境监测 → 摄像头控制   │
└──────────┬──────────────────────┬────────────────────────┘
           │ HTTP REST API        │ OneNet MQTT/HTTP
           ▼                      ▼
┌──────────────────────┐  ┌──────────────────┐
│  Spring Boot (8181)  │  │   OneNet 云平台   │
│  - CRUD / Excel      │  │   设备属性下发     │
│  - YOLO 调用         │  └────────┬─────────┘
│  - UDP 通信          │           │ LoRa
└──┬────────┬──────────┘           ▼
   │        │              ┌──────────────────┐
   │        └──────────────│   ESP8266 终端   │
   │  ProcessBuilder       │  (UDP :8282)     │
   ▼                       │  环境数据采集     │
┌──────────────────┐       └──────────────────┘
│  PyTorch YOLOv8  │
│  (cv2 + PIL)     │
│  玉米去雄识别     │
└──────────────────┘
```

---

## 技术栈

| 模块 | 技术选型 |
|------|---------|
| **后端** | Java 17、Spring Boot 2.6.13、MyBatis-Plus 3.5.7、MySQL 8.0、Hutool 5.7 |
| **前端** | Vue 2.6、Element UI 2.4、Vue Router 3.5、Vuex 3.6、Axios 0.18 |
| **AI 检测** | Python 3.9+、Ultralytics YOLOv8、OpenCV、Pillow |
| **物联网** | ESP8266、WiFiUDP、LoRa 透传、OneNet 平台 |
| **通信** | HTTP REST、UDP (端口 8282)、LoRa、ProcessBuilder 进程调用 |

---

## 环境要求

| 依赖 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.6+ | Java 依赖管理 |
| Node.js | 16+ | 前端构建工具 |
| MySQL | 8.0+ | 关系型数据库 |
| Python | 3.9+ | YOLO 检测服务（建议使用 Anaconda） |
| Arduino IDE | 2.x | ESP8266 开发工具 |

### Python 环境配置

```bash
conda create -n yolo python=3.9
conda activate yolo
pip install opencv-python ultralytics pillow
```

---

## 快速开始

### 1. 数据库初始化

```sql
CREATE DATABASE agriculture DEFAULT CHARSET utf8mb4;

USE agriculture;

CREATE TABLE user (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(50)  NOT NULL,
    phone    VARCHAR(20),
    email    VARCHAR(50)
);
```

### 2. 后端启动

1. IDEA 打开 `Springboot_project/`
2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/agriculture
       username: root
       password: 你的密码
   ```
3. 确认 `UserController.java` 中 `pythonPath` 指向你的 conda 环境 Python：
   ```java
   String pythonPath = "D:\\Conda\\envs\\yolo\\python.exe";
   ```
4. 运行 `SpringbootProjectApplication.java`，服务端口 **8181**

### 3. 前端启动

```bash
cd vue_project
npm install
npm run serve
```

前端开发服务器默认端口 8080，已配置代理：
- `/user` → `http://localhost:8181`（后端接口）
- `/iot-api` → `https://iot-api.heclouds.com`（OneNet 平台）

### 4. YOLO 检测服务

无需单独启动。后端收到图片后通过 `ProcessBuilder` 自动调用 `serve.py`：

```
前端上传图片 → /user/uploadImage → 保存至 yolo_web/web/
  → /user/getPredictedImage → 调用 serve.py → YOLO 推理
  → 结果输出至 yolo_web/result/ → 返回前端展示
```

### 5. ESP8266 终端部署

1. Arduino IDE 安装 ESP8266 开发板（`https://arduino.esp8266.com/stable/package_esp8266com_index.json`）
2. 打开 `arduino_project/arduino_project.ino`
3. 修改配置：
   ```cpp
   const char* WIFINAME     = "你的WiFi名称";
   const char* WIFIPASSWORD = "你的WiFi密码";
   const char* remoteIP     = "192.168.x.x";  // 后端服务器局域网 IP
   ```
4. 编译上传到 ESP8266，终端通过 UDP 向 `8282` 端口周期性上报数据

---

## 功能模块

### 用户系统
| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | GET | 用户登录认证 |
| `/user` | GET | 查询全部用户 |
| `/user/page` | GET | 分页 + 模糊搜索 |
| `/user/{id}` | GET | 按 ID 查询 |
| `/user/saveorupdate` | POST | 新增/更新用户 |
| `/user/delete/{id}` | DELETE | 删除单个用户 |
| `/user/del/batch` | POST | 批量删除 |
| `/user/export` | GET | 导出 Excel |
| `/user/import` | POST | 从 Excel 导入 |

### 图像检测
| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/uploadImage` | POST | 上传待检测图片（玉米田间照片） |
| `/user/getPredictedImage` | GET | 获取 YOLO 检测标注结果 |

### UDP 通信
| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/messages` | GET | 获取 ESP8266 上报数据 |
| `/user/sendudpmessage` | GET | 向 ESP8266 发送下行指令（已注释，按需启用） |

### 前端页面

| 路由 | 页面 | 功能 |
|------|------|------|
| `/login` | 登录页 | 用户身份认证 |
| `/register` | 注册页 | 新用户注册 |
| `/home` | 首页 | 系统概览 |
| `/data-center` | 数据中心 | 玉米去雄检测 + 摄像头控制 + 双区域环境监测 |
| `/user` | 用户管理 | 用户 CRUD + Excel 导入导出 |

---

## OneNet 平台集成

数据中心页面通过 OneNet 物联网平台远程管理 LoRa 摄像头设备：

- **设备属性查询**：定时轮询摄像头 IP 地址
- **属性下发**：远程开关摄像服务（`CAM_key`）
- **设备信息**（`DataCenter.vue` 中配置）：
  ```js
  product_id: '5ge5BdeW12'
  device_name: 'LoRa_cam'
  ```

---

## 注意事项

| 事项 | 说明 |
|------|------|
| `best.pt` 模型文件 | YOLO 权重文件体积较大，未纳入版本管理，需自行训练或获取 |
| 数据库密码 | `application.yml` 中密码为占位符，部署时请替换 |
| Python 路径 | `UserController.java` 第 193 行需改为本机 conda 环境的 python.exe 绝对路径 |
| ESP8266 IP | `remoteIP` 需设置为后端服务器实际局域网 IP |
| OneNet Token | `DataCenter.vue` 中 token 含时间戳，过期后需重新生成 |
| CORS 跨域 | 后端已全局放通（`CorsConfiguration.java`），生产环境建议收紧 |

---

## License

本项目仅供学习与交流使用。
