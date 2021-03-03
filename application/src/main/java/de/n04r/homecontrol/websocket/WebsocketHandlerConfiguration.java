package de.n04r.homecontrol.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketHandlerConfiguration implements WebSocketConfigurer {

    private final WebsocketHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/api")
                // if not allowed, the frontend won't connect in local dev mode without
                // giving any meaningful hint within the error message
                .setAllowedOrigins("*");
    }
}
