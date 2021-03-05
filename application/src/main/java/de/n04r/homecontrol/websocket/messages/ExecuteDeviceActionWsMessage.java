package de.n04r.homecontrol.websocket.messages;


import com.fasterxml.jackson.annotation.JsonCreator;
import de.n04r.homecontrol.model.Action;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class ExecuteDeviceActionWsMessage implements AbstractWsMessage {
    Action action;
    String device;
}
