package de.n04r.homecontrol.websocket.messages;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor(onConstructor_ = @JsonCreator)
public class TagsSelectedWsMessage implements AbstractWsMessage {
    Set<String> tags;
}
