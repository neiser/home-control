package de.n04r.homecontrol.websocket.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = ConfigWsMessage.class, name = "config"),
        @Type(value = ActionWsMessage.class, name = "action")
})
public interface AbstractWsMessage {

}
