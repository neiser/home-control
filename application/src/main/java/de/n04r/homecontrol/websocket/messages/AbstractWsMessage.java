package de.n04r.homecontrol.websocket.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = AvailableTagsWsMessage.class, name = "available-tags"),
        @Type(value = AvailableActionsWsMessage.class, name = "available-actions"),
        @Type(value = TagsSelectedWsMessage.class, name = "tags-selected"),
        @Type(value = ActionWsMessage.class, name = "action"),
        @Type(value = ActionResultWsMessage.class, name = "action-result"),
})
public interface AbstractWsMessage {

}
