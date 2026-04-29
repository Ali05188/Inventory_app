package com.inventory.asset.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AssetLifecycleServiceTest {

    @Autowired
    private AssetLifecycleService lifecycleService;

    @Test
    void getValidStatuses_ReturnsAllStatuses() {
        List<String> statuses = lifecycleService.getValidStatuses();

        assertNotNull(statuses);
        assertEquals(6, statuses.size());
        assertTrue(statuses.contains("new"));
        assertTrue(statuses.contains("in_service"));
        assertTrue(statuses.contains("maintenance"));
        assertTrue(statuses.contains("repair"));
        assertTrue(statuses.contains("decommissioned"));
        assertTrue(statuses.contains("disposed"));
    }

    @Test
    void getAllowedTransitions_FromNew_ReturnsCorrectTransitions() {
        List<String> transitions = lifecycleService.getAllowedTransitions("new");

        assertNotNull(transitions);
        assertEquals(2, transitions.size());
        assertTrue(transitions.contains("in_service"));
        assertTrue(transitions.contains("disposed"));
    }

    @Test
    void getAllowedTransitions_FromInService_ReturnsCorrectTransitions() {
        List<String> transitions = lifecycleService.getAllowedTransitions("in_service");

        assertNotNull(transitions);
        assertEquals(3, transitions.size());
        assertTrue(transitions.contains("maintenance"));
        assertTrue(transitions.contains("repair"));
        assertTrue(transitions.contains("decommissioned"));
    }

    @Test
    void getAllowedTransitions_FromDisposed_ReturnsEmpty() {
        List<String> transitions = lifecycleService.getAllowedTransitions("disposed");

        assertNotNull(transitions);
        assertTrue(transitions.isEmpty());
    }

    @Test
    void isValidTransition_ValidTransition_ReturnsTrue() {
        assertTrue(lifecycleService.isValidTransition("new", "in_service"));
        assertTrue(lifecycleService.isValidTransition("in_service", "maintenance"));
        assertTrue(lifecycleService.isValidTransition("maintenance", "repair"));
    }

    @Test
    void isValidTransition_InvalidTransition_ReturnsFalse() {
        assertFalse(lifecycleService.isValidTransition("new", "maintenance"));
        assertFalse(lifecycleService.isValidTransition("disposed", "in_service"));
        assertFalse(lifecycleService.isValidTransition("in_service", "new"));
    }

    @Test
    void isValidTransition_FromNull_AllowsValidStatuses() {
        assertTrue(lifecycleService.isValidTransition(null, "new"));
        assertTrue(lifecycleService.isValidTransition(null, "in_service"));
    }

    @Test
    void validateTransition_ValidTransition_NoException() {
        assertDoesNotThrow(() -> lifecycleService.validateTransition("new", "in_service"));
    }

    @Test
    void validateTransition_InvalidTransition_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> lifecycleService.validateTransition("new", "maintenance")
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
    }
}

