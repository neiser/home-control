package de.n04r.homecontrol.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.n04r.homecontrol.model.ActionHandler;
import de.n04r.homecontrol.model.ActionResult;
import de.n04r.homecontrol.model.AvailableAction;
import de.n04r.homecontrol.model.Device;
import de.n04r.homecontrol.model.DeviceHandler;
import de.n04r.homecontrol.model.SceneHandler;
import de.n04r.homecontrol.model.TagHandler;
import de.n04r.homecontrol.websocket.messages.AbstractWsMessage;
import de.n04r.homecontrol.websocket.messages.ActionResultWsMessage;
import de.n04r.homecontrol.websocket.messages.ActivateSceneWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableActionsWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableDevicesWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableScenesWsMessage;
import de.n04r.homecontrol.websocket.messages.AvailableTagsWsMessage;
import de.n04r.homecontrol.websocket.messages.ExecuteBatchActionWsMessage;
import de.n04r.homecontrol.websocket.messages.ExecuteDeviceActionWsMessage;
import de.n04r.homecontrol.websocket.messages.TagsSelectedWsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class WebsocketHandler extends TextWebSocketHandler {

    private final TagHandler tagHandler;
    private final ActionHandler actionHandler;
    private final SceneHandler sceneHandler;
    private final DeviceHandler deviceHandler;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection established");
        sendMessage(session, new AvailableTagsWsMessage(tagHandler.getAvailableTags()));
        sendMessage(session, new AvailableScenesWsMessage(sceneHandler.getAvailableScenes()));
        sendMessage(session, new AvailableDevicesWsMessage(deviceHandler.getAvailableDevices()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.debug("Connection closed: {}", status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        AbstractWsMessage abstractMessage = objectMapper.readValue(textMessage.getPayload(), AbstractWsMessage.class);
        if (abstractMessage instanceof ExecuteBatchActionWsMessage) {
            ExecuteBatchActionWsMessage message = (ExecuteBatchActionWsMessage) abstractMessage;
            List<Device> devices = tagHandler.findDevices(message.getTags());
            ActionResult actionResult = actionHandler.executeAction(devices, message.getAction());
            sendActionResult(session, actionResult);

        } else if (abstractMessage instanceof TagsSelectedWsMessage) {
            TagsSelectedWsMessage message = (TagsSelectedWsMessage) abstractMessage;
            List<AvailableAction> availableActions = tagHandler.findAvailableActions(message.getTags());
            sendMessage(session, new AvailableActionsWsMessage(availableActions));
        } else if (abstractMessage instanceof ActivateSceneWsMessage) {
            ActivateSceneWsMessage message = (ActivateSceneWsMessage) abstractMessage;
            ActionResult actionResult = sceneHandler.activateScene(message.getScene());
            sendActionResult(session, actionResult);
        } else if (abstractMessage instanceof ExecuteDeviceActionWsMessage) {
            ExecuteDeviceActionWsMessage message = (ExecuteDeviceActionWsMessage) abstractMessage;
            List<Device> devices = deviceHandler.findDevicesByNames(Collections.singletonList(message.getDevice()));
            ActionResult actionResult = actionHandler.executeAction(devices, message.getAction());
            sendActionResult(session, actionResult);
        }
    }

    private void sendActionResult(WebSocketSession session, ActionResult actionResult) throws IOException {
        sendMessage(session, new ActionResultWsMessage(actionResult.getSuccessful(), actionResult.getFailed()));
    }

    private void sendMessage(WebSocketSession session, AbstractWsMessage message) throws IOException {
        log.debug("Sending message {}", message);
        String payload = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(payload));
    }
}
