package de.n04r.homecontrol.model;

import de.n04r.homecontrol.shelly.ShellyDeviceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionHandler {
    private final ShellyDeviceHandler shellyDeviceHandler;

    public void executeAction(List<Device> devices, Action action) {
        List<Device> devicesSupportingAction = devices.stream()
                .filter(device -> device.getType().getSupportedActions().contains(action))
                .collect(Collectors.toList());

        log.debug("Executing {} on {} devices", action, devicesSupportingAction.size());

        switch (action) {
            case SWITCH_ON:
                devicesSupportingAction.forEach(shellyDeviceHandler::turnRelayOn);
                break;
            case SWITCH_OFF:
                devicesSupportingAction.forEach(shellyDeviceHandler::turnRelayOff);
                break;
            case SHUTTER_UP:
                devicesSupportingAction.forEach(shellyDeviceHandler::openShutter);
                break;
            case SHUTTER_DOWN:
                devicesSupportingAction.forEach(shellyDeviceHandler::closeShutter);
                break;
            case SHUTTER_STOP:
                devicesSupportingAction.forEach(shellyDeviceHandler::stopShutter);
                break;
            default:
                log.debug("Action not implemented: {}", action);
                break;
        }
    }
}
