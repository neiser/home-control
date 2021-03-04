package de.n04r.homecontrol.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.DevicesConfigurationProperties;
import de.n04r.homecontrol.model.ActionHandler;
import de.n04r.homecontrol.model.Device;
import de.n04r.homecontrol.model.TagHandler;
import de.n04r.homecontrol.shelly.ShellyDeviceHandler;
import de.n04r.homecontrol.websocket.messages.AbstractWsMessage;
import de.n04r.homecontrol.websocket.messages.ActionWsMessage;
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
    private final ShellyDeviceHandler shellyDeviceHandler;
    private final TagHandler tagHandler;
    private final ActionHandler actionHandler;
    private final DevicesConfigurationProperties devicesConfigurationProperties;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection established, sending available tags and actions");
        String payload = objectMapper.writeValueAsString(new ConfigWsMessage(
                tagHandler.getAvailableTags(),
                actionHandler.getAvailableActions()
        ));
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        AbstractWsMessage abstractMessage = objectMapper.readValue(textMessage.getPayload(), AbstractWsMessage.class);
        if (abstractMessage instanceof ActionWsMessage) {
            ActionWsMessage message = (ActionWsMessage) abstractMessage;
            List<Device> devices = tagHandler.findDevices(message.getTags());
            actionHandler.executeAction(devices, message.getAction());
        }
    }
}
