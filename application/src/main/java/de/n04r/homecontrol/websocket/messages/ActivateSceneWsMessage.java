package de.n04r.homecontrol.websocket.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class ActivateSceneWsMessage implements AbstractWsMessage {
    String scene;
}