package de.n04r.homecontrol.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.model.Action;
import de.n04r.homecontrol.model.ActionHandler;
import de.n04r.homecontrol.model.ActionResult;
import de.n04r.homecontrol.model.Device;
import de.n04r.homecontrol.model.TagHandler;
import de.n04r.homecontrol.websocket.messages.AbstractWsMessage;
import de.n04r.homecontrol.websocket.messages.ActionResultWsMessage;
import de.n04r.homecontrol.websocket.messages.ActionWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableActionsWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableTagsWsMessage;
import de.n04r.homecontrol.websocket.messages.TagsSelectedWsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final TagHandler tagHandler;
    private final ActionHandler actionHandler;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection established, sending available tags");
        sendMessage(session, new AvailableTagsWsMessage(tagHandler.getAvailableTags()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("Connection closed: {}", status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        AbstractWsMessage abstractMessage = objectMapper.readValue(textMessage.getPayload(), AbstractWsMessage.class);
        if (abstractMessage instanceof ActionWsMessage) {
            ActionWsMessage message = (ActionWsMessage) abstractMessage;
            List<Device> devices = tagHandler.findDevices(message.getTags());
            ActionResult actionResult = actionHandler.executeAction(devices, message.getAction());
            sendMessage(session, new ActionResultWsMessage(actionResult.getSuccessful(), actionResult.getFailed()));

        } else if (abstractMessage instanceof TagsSelectedWsMessage) {
            TagsSelectedWsMessage message = (TagsSelectedWsMessage) abstractMessage;
            List<Action> availableActions = tagHandler.findAvailableActions(message.getTags());
            log.debug("Sending available actions {} for tags {}", availableActions, message.getTags());
            sendMessage(session, new AvailableActionsWsMessage(availableActions.stream()
                    .map(action -> new AvailableActionsWsMessage.ActionWithDisplayName(action.name(), action.getDisplayName()))
                    .collect(Collectors.toList())));
        }
    }

    private void sendMessage(WebSocketSession session, AbstractWsMessage message) throws IOException {
        String payload = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(payload));
    }
}
