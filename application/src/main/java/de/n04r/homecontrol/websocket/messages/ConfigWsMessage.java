package de.n04r.homecontrol.websocket.messages;

import lombok.Value;

import java.util.Set;

@Value
public class ConfigWsMessage implements AbstractWsMessage {
    Set<String> tags;
}