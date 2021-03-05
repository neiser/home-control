package de.n04r.homecontrol.websocket.messages;


import com.fasterxml.jackson.annotation.JsonCreator;
import de.n04r.homecontrol.model.Action;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class ExecuteActionWsMessage implements AbstractWsMessage {
    Action action;
    Set<String> tags;
    Map<String, Object> properties;
}
