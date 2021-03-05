package de.n04r.homecontrol.websocket.messages;

import lombok.Value;

import java.util.List;

@Value
public class AvailableTagsWsMessage implements AbstractWsMessage {
    List<String> tags;
}