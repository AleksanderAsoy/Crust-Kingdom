package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.movement.NPCMovementStrategy;
import inf112.skeleton.model.orders.Order;

/**
 * Test suite for the NPC class.
 * Tests behavior, movement, and interactions for NPCs.
 */

class NPCTest {
    
    @Mock
    private OrderManager orderManager;
    
    @Mock
    private GameLogic gameLogic;

    private NPC npc;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create NPC with starting position
        int startX = 500;
        int startY = 500;
        npc = new NPC(orderManager, startX, startY, gameLogic);
    }
    
    @Test
    void shouldCreateNPC() {
        // Verify that NPC is created with correct properties
        assertNotNull(npc, "NPC should be created");
        assertNotNull(npc.getMovementStrategy(), "NPC should have a movement strategy");
        assertTrue(npc.getNpcId() >= 0, "NPC ID should be a non-negative integer");
    }
    
    @Test
    void shouldUpdateAnimationWhenWaiting() {
        // Arrange
        npc.setWaiting(true);
        npc.setDirection("waiting_at_counter");
        int initialSpriteNum = npc.spriteNum;
        
        // Act - run multiple updates to change sprite
        for (int i = 0; i < 25; i++) {
            npc.updateNpcAnimation();
        }
        
        // Assert - check that sprite has NOT changed when NPC is waiting at counter
        assertEquals(initialSpriteNum, npc.spriteNum, "Sprite number should not change when waiting at counter");
    }
    
    @Test
    void shouldToggleVisibility() {
        // Default state may be visible in actual implementation
        boolean initialVisibility = npc.isVisible();
        
        // Set to opposite of current state
        npc.setVisible(!initialVisibility);
        
        // Check that the state has changed
        assertNotEquals(initialVisibility, npc.isVisible(), "NPC visibility should change");
        
        // Set back to original state
        npc.setVisible(initialVisibility);
        
        // Check that we are back to the original state
        assertEquals(initialVisibility, npc.isVisible(), "NPC visibility should be restorable");
    }
    
    @Test
    void shouldHandleOrderAssociation() {
        // Arrange
        Order order = new Order("Pepperoni");
        
        // Act
        npc.setOrder(order);
        
        // Assert
        assertSame(order, npc.getOrder(), "NPC should store the order correctly");
        assertEquals("Pepperoni", npc.getOrder().getType(), "Order type should match what was set");
    }
    
    @Test
    void shouldUpdateNPCViaMovementStrategy() {
        // Arrange
        NPCMovementStrategy mockStrategy = mock(NPCMovementStrategy.class);
        
        // Use reflection to set mockStrategy
        try {
            java.lang.reflect.Field field = NPC.class.getDeclaredField("movementStrategy");
            field.setAccessible(true);
            field.set(npc, mockStrategy);
            
            // Set lastUpdateTime to 0 to ensure update happens
            java.lang.reflect.Field lastUpdateField = NPC.class.getDeclaredField("lastUpdateTime");
            lastUpdateField.setAccessible(true);
            lastUpdateField.set(npc, 0L);
        } catch (Exception e) {
            fail("Failed to set mock movement strategy: " + e.getMessage());
        }
        
        // Act
        npc.update();
        
        // Assert
        verify(mockStrategy).update();
    }
    
    @Test
    void shouldToggleWaitingState() {
        // Arrange
        assertFalse(npc.isWaiting());
        
        // Act
        npc.setWaiting(true);
        
        // Assert
        assertTrue(npc.isWaiting(), "NPC should be in waiting state after setWaiting(true)");
    }
} 

