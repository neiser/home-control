package de.n04r.homecontrol.websocket.messages;


import lombok.Value;

import java.util.Map;
import java.util.Set;

@Value
public class ActionWsMessage implements AbstractWsMessage {
    Action action;
    Set<String> tags;
    Map<String, Object> properties;

    public enum Action {
        ON,
        OFF,
        SHUTTER_DOWN,
        SHUTTER_UP,
        SHUTTER_SET
    }
}
