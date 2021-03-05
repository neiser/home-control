package de.n04r.homecontrol.websocket.messages;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = AvailableTagsWsMessage.class, name = "available-tags"),
        @Type(value = AvailableActionsWsMessage.class, name = "available-actions"),
        @Type(value = TagsSelectedWsMessage.class, name = "tags-selected"),
        @Type(value = ExecuteBatchActionWsMessage.class, name = "execute-batch-action"),
        @Type(value = ActionResultWsMessage.class, name = "action-result"),
        @Type(value = AvailableScenesWsMessage.class, name = "available-scenes"),
        @Type(value = ActivateSceneWsMessage.class, name = "activate-scene"),
        @Type(value = AvailableDevicesWsMessage.class, name = "available-devices"),
        @Type(value = ExecuteDeviceActionWsMessage.class, name = "execute-device-action"),
})
public interface AbstractWsMessage {

}
