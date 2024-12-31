package com.shopee.shopeecareer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry
//                                            registry) {
//        registry.addMapping("/ws/**").allowedOrigins("http://localhost:3000"); // Frontend client URL
//    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Đăng ký prefix cho các endpoint mà server sẽ gửi tới client
//        registry.enableSimpleBroker("/topic");  // Prefix cho các thông báo
//        registry.setApplicationDestinationPrefixes("/app");  // Prefix cho các endpoint từ client gửi lên
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic", "/user");
        registry.setUserDestinationPrefix("/user");
    }

//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Đăng ký endpoint WebSocket, nơi client sẽ kết nối đến
//        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();  // Endpoint WebSocket
////        registry.setAllowedOriginPatterns("*");
//    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint WebSocket, nơi client sẽ kết nối đến
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }
}
