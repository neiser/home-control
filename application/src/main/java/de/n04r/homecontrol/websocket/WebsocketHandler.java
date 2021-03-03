package de.n04r.homecontrol.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.DevicesConfigurationProperties;
import de.n04r.homecontrol.shelly.ShellyController;
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
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ShellyController shellyController;
    private final DevicesConfigurationProperties devicesConfigurationProperties;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection established");
        Set<String> availableTags = devicesConfigurationProperties.stream()
                .flatMap(device -> device.getTags().stream())
                .collect(Collectors.toSet());
        String payload = objectMapper.writeValueAsString(new ConfigWsMessage(availableTags));
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        AbstractWsMessage abstractMessage = objectMapper.readValue(textMessage.getPayload(), AbstractWsMessage.class);
        if (abstractMessage instanceof ActionWsMessage) {
            ActionWsMessage message = (ActionWsMessage) abstractMessage;
            List<String> deviceHosts = devicesConfigurationProperties.stream()
                    .filter(device -> device.getTags().stream().anyMatch(deviceTag -> message.getTags().contains(deviceTag)))
                    .map(DevicesConfigurationProperties.Device::getHost)
                    .collect(Collectors.toList());
            if (message.getAction() == ActionWsMessage.Action.SHUTTER_DOWN) {
                shellyController.closeShutters(deviceHosts);
            } else if (message.getAction() == ActionWsMessage.Action.SHUTTER_UP) {
                shellyController.openShutters(deviceHosts);
            } else {
                log.debug("Not implemented: {}", message);
            }
        }
    }
}
