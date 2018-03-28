package com.example.christina.researchsuitedemo.location;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by jameskizer on 9/28/17.
 */

public class RSTransitionEvent implements Serializable {

    public enum Action {
        UNKNOWN, ENTER, EXIT
    }

    private final String regionIdentifier;
    private final Action action;
    private final UUID uuid;
    private final long timestamp;

    public RSTransitionEvent(String regionIdentifier, Action action, UUID uuid, long timestamp) {
        this.regionIdentifier = regionIdentifier;
        this.action = action;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public String getRegionIdentifier() {
        return regionIdentifier;
    }

    public Action getAction() {
        return action;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
