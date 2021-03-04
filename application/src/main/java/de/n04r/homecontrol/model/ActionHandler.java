package de.n04r.homecontrol.model;

import de.n04r.homecontrol.shelly.ShellyDeviceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionHandler {
    private final ShellyDeviceHandler shellyDeviceHandler;

    public List<String> getAvailableActions() {
        return Arrays.stream(Action.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public void executeAction(List<Device> devices, Action action) {
        List<Device> devicesSupportingAction = devices.stream()
                .filter(device -> device.getType().getSupportedActions().contains(action))
                .collect(Collectors.toList());
        if (action == Action.SHUTTER_DOWN) {
            devicesSupportingAction.forEach(shellyDeviceHandler::closeShutter);
        } else if (action == Action.SHUTTER_UP) {
            devicesSupportingAction.forEach(shellyDeviceHandler::openShutter);
        } else if (action == Action.SHUTTER_STOP) {
            devicesSupportingAction.forEach(shellyDeviceHandler::stopShutter);
        } else {
            log.debug("Not implemented: {}", action);
        }
    }
}
