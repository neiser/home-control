package de.n04r.homecontrol.model;

import org.springframework.lang.Nullable;

import java.util.List;

public interface Scene {

    List<Command> getCommands();

    interface Command {
        Action getAction();

        @Nullable
        List<String> getTags();

        @Nullable
        List<String> getDevices();
    }
}
