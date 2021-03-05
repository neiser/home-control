package de.n04r.homecontrol.websocket.messages;

import lombok.Value;

import java.util.List;

@Value
public class AvailableScenesWsMessage implements AbstractWsMessage {
    List<String> scenes;
}