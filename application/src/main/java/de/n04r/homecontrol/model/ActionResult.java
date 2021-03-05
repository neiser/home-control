package de.n04r.homecontrol.model;

import lombok.Value;

@Value
public class ActionResult {
    long successful;
    long failed;
}
