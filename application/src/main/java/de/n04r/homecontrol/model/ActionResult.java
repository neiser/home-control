package de.n04r.homecontrol.model;

import lombok.Value;

@Value
public class ActionResult {
    long successful;
    long failed;

    public ActionResult plus(ActionResult other) {
        return new ActionResult(other.getSuccessful() + successful, other.getFailed() + failed);
    }
}
