package de.n04r.homecontrol.config;

import de.n04r.homecontrol.model.Action;
import de.n04r.homecontrol.model.Scene;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "scenes")
public class ScenesConfigurationProperties extends ArrayList<ScenesConfigurationProperties.SceneConfigItem> {

    @Value
    @RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
    public static class SceneConfigItem implements Scene {
        String name;
        List<SceneCommandConfigItem> commands;

        @Override
        public List<Command> getCommands() {
            // I find the lack of power in Java Generics disturbing...
            return new ArrayList<>(commands);
        }
    }

    @Value
    @RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
    public static class SceneCommandConfigItem implements Scene.Command {
        Action action;
        @Nullable
        List<String> tags;
        @Nullable
        List<String> devices;
    }
}
