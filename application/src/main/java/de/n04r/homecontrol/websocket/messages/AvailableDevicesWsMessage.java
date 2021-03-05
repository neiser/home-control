package de.n04r.homecontrol.websocket.messages;

import de.n04r.homecontrol.model.AvailableDevice;
import lombok.Value;

import java.util.List;

@Value
public class AvailableDevicesWsMessage implements AbstractWsMessage {
    List<AvailableDevice> devices;
}