package de.n04r.homecontrol.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Action {
    SWITCH_ON("On"),
    SWITCH_OFF("Off"),
    SHUTTER_DOWN("Down"),
    SHUTTER_UP("Up"),
    SHUTTER_STOP("Stop");

    private final String displayName;
}
