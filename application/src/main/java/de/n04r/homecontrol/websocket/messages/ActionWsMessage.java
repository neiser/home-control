package de.n04r.homecontrol.websocket.messages;


import de.n04r.homecontrol.model.Action;
import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
public class ActionWsMessage implements AbstractWsMessage {
    Action action;
    Set<String> tags;
    Map<String, Object> properties;
}
