package de.n04r.homecontrol.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static de.n04r.homecontrol.model.Action.SHUTTER_DOWN;
import static de.n04r.homecontrol.model.Action.SHUTTER_STOP;
import static de.n04r.homecontrol.model.Action.SHUTTER_UP;
import static de.n04r.homecontrol.model.Action.SWITCH_OFF;
import static de.n04r.homecontrol.model.Action.SWITCH_ON;

@Getter
public enum DeviceType {
    SHELLY1PM(SWITCH_ON, SWITCH_OFF),
    SHELLY25_SHUTTER(SHUTTER_UP, SHUTTER_DOWN, SHUTTER_STOP);

    private final Set<Action> supportedActions;

    DeviceType(Action... supportedActions) {
        this.supportedActions = EnumSet.noneOf(Action.class);
        this.supportedActions.addAll(Arrays.asList(supportedActions));
    }
}
