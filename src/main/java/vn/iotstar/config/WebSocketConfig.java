package vn.iotstar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Đăng ký các endpoint mà WebSocket client có thể kết nối đến
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Kết nối đến endpoint "/ws", hỗ trợ SockJS
        registry.addEndpoint("/ws").withSockJS();
    }

    // Cấu hình message broker để gửi và nhận các tin nhắn
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Đặt prefix cho các destination từ client gửi đến
        registry.setApplicationDestinationPrefixes("/app");

        // Cấu hình một message broker đơn giản để xử lý các tin nhắn có prefix "/topic"
        // Broker này có thể là một broker in-memory hoặc tích hợp với broker ngoài như ActiveMQ
        registry.enableSimpleBroker("/topic");
        
        // Nếu bạn có kế hoạch sử dụng các tính năng như gửi tin nhắn đến người dùng cụ thể (ví dụ: "/user/queue"), 
        // bạn có thể cấu hình thêm một điểm đến riêng biệt cho user:
        registry.setUserDestinationPrefix("/user");
    }
}
