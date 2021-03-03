package de.n04r.homecontrol.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.websocket.messages.AbstractWsMessage;
import de.n04r.homecontrol.websocket.messages.ConfigWsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection established");
        String payload = objectMapper.writeValueAsString(new ConfigWsMessage(List.of("tag1", "tag2")));
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        AbstractWsMessage message = objectMapper.readValue(textMessage.getPayload(), AbstractWsMessage.class);
        log.debug("Received {}", message);
    }
}
