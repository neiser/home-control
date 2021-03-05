package de.n04r.homecontrol.model;

import de.n04r.homecontrol.config.DevicesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DeviceHandler {
    private final DevicesConfigurationProperties devicesConfigurationProperties;

    public List<AvailableDevice> getAvailableDevices() {
        return devicesConfigurationProperties.stream()
                .map(device -> new AvailableDevice(device.getName(), mapDeviceActions(device.getType().getSupportedActions())))
                .collect(Collectors.toList());
    }

    public List<AvailableAction> mapDeviceActions(Set<Action> supportedActions) {
        // keep order of Action enum
        return Arrays.stream(Action.values())
                .filter(supportedActions::contains)
                .map(action -> new AvailableAction(action.name(), action.getDisplayName()))
                .collect(Collectors.toList());
    }

    public List<Device> findDevicesByNames(List<String> deviceNames) {
        return devicesConfigurationProperties.stream()
                .filter(d -> deviceNames.contains(d.getName()))
                .collect(Collectors.toList());
    }
}
