package com.inventory.asset.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetLifecycleService {

    // Define valid statuses
    public static final String STATUS_NEW = "new";
    public static final String STATUS_IN_SERVICE = "in_service";
    public static final String STATUS_MAINTENANCE = "maintenance";
    public static final String STATUS_REPAIR = "repair";
    public static final String STATUS_DECOMMISSIONED = "decommissioned";
    public static final String STATUS_DISPOSED = "disposed";

    private static final Map<String, List<String>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put(STATUS_NEW, Arrays.asList(STATUS_IN_SERVICE, STATUS_DISPOSED));
        ALLOWED_TRANSITIONS.put(STATUS_IN_SERVICE, Arrays.asList(STATUS_MAINTENANCE, STATUS_REPAIR, STATUS_DECOMMISSIONED));
        ALLOWED_TRANSITIONS.put(STATUS_MAINTENANCE, Arrays.asList(STATUS_IN_SERVICE, STATUS_REPAIR, STATUS_DECOMMISSIONED));
        ALLOWED_TRANSITIONS.put(STATUS_REPAIR, Arrays.asList(STATUS_IN_SERVICE, STATUS_MAINTENANCE, STATUS_DECOMMISSIONED));
        ALLOWED_TRANSITIONS.put(STATUS_DECOMMISSIONED, Arrays.asList(STATUS_DISPOSED, STATUS_IN_SERVICE));
        ALLOWED_TRANSITIONS.put(STATUS_DISPOSED, Collections.emptyList());
    }

    public List<String> getValidStatuses() {
        return Arrays.asList(
            STATUS_NEW,
            STATUS_IN_SERVICE,
            STATUS_MAINTENANCE,
            STATUS_REPAIR,
            STATUS_DECOMMISSIONED,
            STATUS_DISPOSED
        );
    }

    public List<String> getAllowedTransitions(String currentStatus) {
        return ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Collections.emptyList());
    }

    public boolean isValidTransition(String currentStatus, String newStatus) {
        if (currentStatus == null) {
            return getValidStatuses().contains(newStatus);
        }
        List<String> allowed = getAllowedTransitions(currentStatus);
        return allowed.contains(newStatus);
    }

    public void validateTransition(String currentStatus, String newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalArgumentException(
                String.format("Invalid status transition from '%s' to '%s'", currentStatus, newStatus)
            );
        }
    }
}

