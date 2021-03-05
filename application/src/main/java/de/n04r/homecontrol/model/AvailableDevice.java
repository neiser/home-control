package de.n04r.homecontrol.model;

import lombok.Value;

import java.util.List;

@Value
public class AvailableDevice {
    String name;
    List<AvailableAction> availableActions;
}
