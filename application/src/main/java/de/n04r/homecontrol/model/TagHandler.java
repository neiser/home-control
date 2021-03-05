package de.n04r.homecontrol.model;

import de.n04r.homecontrol.DevicesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TagHandler {

    private final DevicesConfigurationProperties devicesConfigurationProperties;

    public List<String> getAvailableTags() {
        return devicesConfigurationProperties.stream()
                .flatMap(device -> device.getTags().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Action> findAvailableActions(Collection<String> tags) {
        Set<Action> allowedActions = findDevices(tags).stream()
                .map(Device::getType)
                .flatMap(deviceType -> deviceType.getSupportedActions().stream())
                .collect(Collectors.toSet());
        return Arrays.stream(Action.values())
                .filter(allowedActions::contains)
                .collect(Collectors.toList());
    }

    public List<Device> findDevices(Collection<String> tags) {
        return devicesConfigurationProperties.stream()
                .filter(device -> device.getTags().stream().anyMatch(tags::contains))
                .collect(Collectors.toList());
    }
}
