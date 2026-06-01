# -*- coding: utf-8 -*-
import sys
import os
import cv2
from ultralytics import YOLO
from PIL import Image

def yolo_web(test_str):
    model = YOLO("D:/project/spring_vue_yolo/yolo_web/best.pt")
    # 对测试图像进行预测
    predictions = model(f"D:/project/spring_vue_yolo/yolo_web/web/{test_str}")
    # 读取原始图像
    img = cv2.imread(f"D:/project/spring_vue_yolo/yolo_web/web/{test_str}", cv2.IMREAD_UNCHANGED)
    # 将BGR图像转换为RGB图像，以便PIL正确显示
    # img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    # 获取预测结果的绘制图像
    result_img = predictions[0].plot()
    # 将RGB图像转换为BGR图像，以便cv2.imshow正确显示（如果需要使用cv2.imshow）
    result_img_bgr = cv2.cvtColor(result_img, cv2.COLOR_RGB2BGR)
    # 使用PIL保存图像
    result_img_pil = Image.fromarray(result_img_bgr)
    save_path_result = os.path.join('D:/project/spring_vue_yolo/yolo_web/result', f"result_{test_str}")
    result_img_pil.save(save_path_result)
    return f"result_{test_str}"

if __name__ == '__main__':
    sys.argv = ['', 'image_0004.jpg']
    print(sys.argv[1])  # 获取传来的uniFileName内容，即图片名称
    result = yolo_web(sys.argv[1])
    print(result)
    # 退出程序
    sys.exit(0)
