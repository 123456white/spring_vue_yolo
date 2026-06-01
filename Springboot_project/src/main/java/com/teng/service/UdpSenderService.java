package com.teng.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
@Slf4j
@Service
public class UdpSenderService {

    public void sendUdpMessage(String message) {
        try {
            // 创建DatagramSocket对象
            DatagramSocket socket = new DatagramSocket();

            // 设置目标IP地址和端口号即ESP8266返回的IP地址
            InetAddress address = InetAddress.getByName("192.168.155.212");
            int port = 8282;

            // 将消息转换为字节数组
            byte[] buffer = message.getBytes();

            // 创建DatagramPacket对象
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

            // 发送数据包
            socket.send(packet);

            log.info("UDP消息发送成功！消息内容: {}", message);

            // 关闭socket
            socket.close();
        } catch (IOException e) {
            log.error("发送UDP消息时出现错误：", e);
        }
    }
}
