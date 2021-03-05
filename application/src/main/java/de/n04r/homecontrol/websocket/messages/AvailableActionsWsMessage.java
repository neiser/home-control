package de.n04r.homecontrol.websocket.messages;

import de.n04r.homecontrol.model.AvailableAction;
import lombok.Value;

import java.util.List;

@Value
public class AvailableActionsWsMessage implements AbstractWsMessage {
    List<AvailableAction> actions;

}