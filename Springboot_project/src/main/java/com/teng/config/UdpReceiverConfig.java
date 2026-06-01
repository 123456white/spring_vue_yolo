package com.teng.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Configuration
@EnableIntegration
public class UdpReceiverConfig {

    // 创建一个名为 udpInputChannel 的消息通道
    @Bean
    public MessageChannel udpInputChannel() {
        // 返回一个直接消息通道实例
        return new DirectChannel();
    }

    // 创建 UDP 接收适配器的 Bean
    @Bean
    public UnicastReceivingChannelAdapter udpAdapter() {
        // 创建一个 UnicastReceivingChannelAdapter 实例，监听端口 8282
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(8282);
        // 设置接收适配器的输出通道为 udpInputChannel
        adapter.setOutputChannel(udpInputChannel());
        // 返回 UDP 接收适配器实例
        return adapter;
    }

    // 处理接收到的消息的方法，使用 @ServiceActivator 注解将其注册为消息接收处理方法
    @ServiceActivator(inputChannel = "udpInputChannel")
    public void handleMessage(@Payload byte[] payload, @Header("ip_address") String ipAddress) {
        // 打印接收到的 UDP 消息的来源 IP 地址和消息内容
        System.out.println("Received UDP message from " + ipAddress + " with content: " + new String(payload));
    }
}
