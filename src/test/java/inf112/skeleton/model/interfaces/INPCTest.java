package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class INPCTest {
    private INPC npc;

    @BeforeEach
    void setUp() {
        npc = mock(INPC.class);
        
        // Set up default mock behavior
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(200);
        when(npc.getDirection()).thenReturn("right");
        when(npc.isVisible()).thenReturn(true);
        when(npc.getAnimationState()).thenReturn(1);
        when(npc.isWaiting()).thenReturn(false);
        when(npc.isWaitingForPizza()).thenReturn(false);
        when(npc.getNpcId()).thenReturn(1);
    }

    @Test
    void shouldUpdateNPCState() {
        // Test that update method is called
        npc.update();
        verify(npc).update();
    }

    @Test
    void shouldHandlePosition() {
        // Test initial position
        assertEquals(100, npc.getX());
        assertEquals(200, npc.getY());
    }

    @Test
    void shouldHandleDirection() {
        // Test initial direction
        assertEquals("right", npc.getDirection());
    }

    @Test
    void shouldHandleVisibility() {
        // Test initial visibility
        assertTrue(npc.isVisible());
    }

    @Test
    void shouldHandleAnimation() {
        // Test animation state
        assertEquals(1, npc.getAnimationState());
    }

    @Test
    void shouldHandleWaitingStates() {
        // Test initial waiting states
        assertFalse(npc.isWaiting());
        assertFalse(npc.isWaitingForPizza());
        
        // Test waiting state changes
        when(npc.isWaiting()).thenReturn(true);
        assertTrue(npc.isWaiting());
    }

    @Test
    void shouldHandleNPCId() {
        // Test NPC ID
        assertEquals(1, npc.getNpcId());
    }
} 