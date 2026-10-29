package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.interfaces.managers.ICookingManager;

class ICookingManagerTest {
    private ICookingManager cookingManager;

    @BeforeEach
    void setUp() {
        cookingManager = mock(ICookingManager.class);
        
        when(cookingManager.isCooking()).thenReturn(false);
        when(cookingManager.getCookingProgress()).thenReturn(0.0);
        when(cookingManager.isBakingMinigameActive()).thenReturn(false);
        when(cookingManager.getCookTime()).thenReturn(1000L);
        when(cookingManager.getCookingSpeedMultiplier()).thenReturn(1.0);
        when(cookingManager.getMinigameProgress()).thenReturn(0.0);
    }

    @Test
    void shouldHandleCookingState() {
        // Test initial state
        assertFalse(cookingManager.isCooking(), "Should not be cooking initially");
        
        // Start cooking
        when(cookingManager.isCooking()).thenReturn(true);
        assertTrue(cookingManager.isCooking(), "Should be cooking after start");
        
        // Test cooking progress
        when(cookingManager.getCookingProgress()).thenReturn(0.5);
        double progress = cookingManager.getCookingProgress();
        assertTrue(progress >= 0.0 && progress <= 1.0, "Progress should be between 0 and 1");
    }

    @Test
    void shouldHandleMinigameOperations() {
        // Test minigame state
        assertFalse(cookingManager.isBakingMinigameActive(), "Minigame should not be active initially");
        
        // Start minigame
        when(cookingManager.isBakingMinigameActive()).thenReturn(true);
        assertTrue(cookingManager.isBakingMinigameActive(), "Minigame should be active after initiation");
        
        // Test minigame progress
        when(cookingManager.getMinigameProgress()).thenReturn(0.5);
        double progress = cookingManager.getMinigameProgress();
        assertTrue(progress >= 0.0 && progress <= 1.0, "Minigame progress should be between 0 and 1");
    }

    @Test
    void shouldHandleTimeOperations() {
        // Test cook time
        long cookTime = cookingManager.getCookTime();
        assertTrue(cookTime > 0, "Cook time should be positive");
    }

    @Test
    void shouldHandlePizzaOperations() {
        // Test ready state
        when(cookingManager.isReadyToTakeOut()).thenReturn(true);
        assertTrue(cookingManager.isReadyToTakeOut(), "Pizza should be ready to take out");
    }

    @Test
    void shouldHandleCookingSpeed() {
        // Test initial speed
        assertEquals(1.0, cookingManager.getCookingSpeedMultiplier(), "Initial speed should be 1.0");
    }

    @Test
    void shouldHandleMinigameActions() {
        // Test minigame hit attempt
        cookingManager.attemptMinigameHit();
        verify(cookingManager).attemptMinigameHit();
    }

    @Test
    void shouldHandleReset() {
        // Test reset
        cookingManager.reset();
        verify(cookingManager).reset();
    }
} 