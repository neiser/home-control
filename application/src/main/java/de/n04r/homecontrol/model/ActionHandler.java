package de.n04r.homecontrol.model;

import de.n04r.homecontrol.shelly.ShellyDeviceHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActionHandler {
    private final ShellyDeviceHandler shellyDeviceHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ActionResult executeAction(List<Device> devices, Action action) {
        List<Device> devicesSupportingAction = devices.stream()
                .filter(device -> device.getType().getSupportedActions().contains(action))
                .distinct() // make sure we don't run actions twice on the same device
                .collect(Collectors.toList());

        log.debug("Executing {} on {} devices", action, devicesSupportingAction.size());

        switch (action) {
            case SWITCH_ON:
                return executeActionInParallel(devicesSupportingAction, shellyDeviceHandler::turnRelayOn);
            case SWITCH_OFF:
                return executeActionInParallel(devicesSupportingAction, shellyDeviceHandler::turnRelayOff);
            case SHUTTER_UP:
                return executeActionInParallel(devicesSupportingAction, shellyDeviceHandler::openShutter);
            case SHUTTER_DOWN:
                return executeActionInParallel(devicesSupportingAction, shellyDeviceHandler::closeShutter);
            case SHUTTER_STOP:
                return executeActionInParallel(devicesSupportingAction, shellyDeviceHandler::stopShutter);
            default:
                throw new IllegalStateException("Action not implemented " + action);
        }
    }

    private ActionResult executeActionInParallel(List<Device> devicesSupportingAction, Consumer<Device> action) {
        List<Future<?>> tasks = devicesSupportingAction.stream()
                .map(device -> executorService.submit(() -> action.accept(device)))
                .collect(Collectors.toList());

        long successfulTasks = tasks.stream().filter(task -> {
            try {
                task.get(1, TimeUnit.SECONDS);
                return true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for task", e);
            } catch (ExecutionException | TimeoutException e) {
                log.debug("Task did not complete successfully", e);
                return false;
            }
        }).count();

        return new ActionResult(successfulTasks, tasks.size() - successfulTasks);
    }
}
