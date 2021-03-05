package de.n04r.homecontrol.model;

import de.n04r.homecontrol.config.DevicesConfigurationProperties;
import de.n04r.homecontrol.config.ScenesConfigurationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SceneHandler {
    private static final ActionResult ZERO_ACTION_RESULT = new ActionResult(0, 0);

    private final TagHandler tagHandler;
    private final ActionHandler actionHandler;
    private final ScenesConfigurationProperties scenesConfigurationProperties;
    private final DevicesConfigurationProperties devicesConfigurationProperties;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public List<String> getAvailableScenes() {
        return scenesConfigurationProperties.stream()
                .map(ScenesConfigurationProperties.SceneConfigItem::getName)
                .collect(Collectors.toList());
    }

    public ActionResult activateScene(String sceneName) {
        return scenesConfigurationProperties.stream()
                .filter(scene -> scene.getName().equals(sceneName))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Found more than one scene with name " + sceneName);
                })
                .flatMap(scene -> {
                    List<Future<ActionResult>> tasks = scene.getCommands().stream()
                            .map(this::runSceneCommand)
                            .collect(Collectors.toList());
                    return tasks.stream()
                            .map(task -> {
                                try {
                                    return task.get(1, TimeUnit.SECONDS);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    throw new IllegalStateException("Interrupted while waiting for task", e);
                                } catch (ExecutionException | TimeoutException e) {
                                    log.warn("Cannot complete task", e);
                                    return ZERO_ACTION_RESULT;
                                }
                            })
                            .reduce(ActionResult::plus);
                })
                .orElse(ZERO_ACTION_RESULT);
    }

    private Future<ActionResult> runSceneCommand(Scene.Command command) {
        return executorService.submit(() -> {
            List<Device> allDevices = new ArrayList<>();
            if (command.getTags() != null) {
                allDevices.addAll(tagHandler.findDevices(command.getTags()));
            }
            if (command.getDevices() != null) {
                allDevices.addAll(findDevicesByNames(command.getDevices()));
            }
            return actionHandler.executeAction(allDevices, command.getAction());
        });
    }

    private List<Device> findDevicesByNames(List<String> deviceNames) {
        return devicesConfigurationProperties.stream()
                .filter(d -> deviceNames.contains(d.getName()))
                .collect(Collectors.toList());
    }
}
