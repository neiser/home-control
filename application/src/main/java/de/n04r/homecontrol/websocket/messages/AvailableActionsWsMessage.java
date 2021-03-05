package de.n04r.homecontrol.websocket.messages;

import lombok.Value;

import java.util.List;

@Value
public class AvailableActionsWsMessage implements AbstractWsMessage {
    List<ActionWithDisplayName> actions;

    @Value
    public static class ActionWithDisplayName {
        String id;
        String displayName;
    }
}