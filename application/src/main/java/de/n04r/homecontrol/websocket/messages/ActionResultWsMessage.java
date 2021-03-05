package de.n04r.homecontrol.websocket.messages;

import lombok.Value;

@Value
public class ActionResultWsMessage implements AbstractWsMessage {
    long successful;
    long failed;
}
