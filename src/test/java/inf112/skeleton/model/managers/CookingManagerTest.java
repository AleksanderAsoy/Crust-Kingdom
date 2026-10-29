package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test suite for the CookingManager class.
 * Tests the cooking process, minigame functionality, and related features.
 */
class CookingManagerTest {
    private CookingManager cookingManager;
    private long currentTime;
    
    @Mock
    private KarmaManager karmaManager;
    
    @Mock
    private SoundManager sound;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentTime = 0; // Start at 0 for predictable testing
        cookingManager = new CookingManager() {
            @Override
            protected long getCurrentTimeMillis() {
                return currentTime;
            }
        };
        cookingManager.setKarmaManager(karmaManager);
        cookingManager.setSound(sound);
    }

    /**
     * Advances the simulated time by the specified milliseconds.
     * 
     * @param milliseconds the time to advance in milliseconds
     */
    private void advanceTime(long milliseconds) {
        currentTime += milliseconds;
        cookingManager.update();
    }

    @Test
    void shouldStartCooking() {
        // Act
        cookingManager.startCooking();

        // Assert
        assertTrue(cookingManager.isCooking(), "Should be cooking after startCooking");
        assertFalse(cookingManager.isReadyToTakeOut(), "Should not be ready to take out when starting");
        assertEquals(currentTime, cookingManager.getStartTime(), "Start time should be set to current time");
    }

    @Test
    void shouldNotStartCookingIfAlreadyCooking() {
        // Arrange
        cookingManager.startCooking();
        long initialStartTime = cookingManager.getStartTime();
        
        // Act
        advanceTime(500); // Advance time
        cookingManager.startCooking();
        
        // Assert
        assertTrue(cookingManager.isCooking(), "Should still be cooking");
        assertEquals(initialStartTime, cookingManager.getStartTime(), "Start time should not change");
    }

    @Test
    void shouldCompletePizzaAfterCookTime() {
        // Arrange
        cookingManager.startCooking();
        
        // Act
        advanceTime(cookingManager.getCookTime());
        
        // Assert
        assertFalse(cookingManager.isCooking(), "Should not be cooking after completion");
        assertTrue(cookingManager.isReadyToTakeOut(), "Should be ready to take out after completion");
        verify(sound).play(6); // Verify sound is played
    }

    @Test
    void shouldNotCompletePizzaBeforeCookTime() {
        // Arrange
        cookingManager.startCooking();
        
        // Act
        advanceTime(cookingManager.getCookTime() / 2);
        
        // Assert
        assertTrue(cookingManager.isCooking(), "Should still be cooking before completion");
        assertFalse(cookingManager.isReadyToTakeOut(), "Should not be ready to take out before completion");
    }

    @Test
    void shouldTakeOutPizza() {
        // Arrange
        cookingManager.startCooking();
        advanceTime(cookingManager.getCookTime());
        assertTrue(cookingManager.isReadyToTakeOut(), "Pizza should be ready to take out");
        
        // Act
        cookingManager.takeOutPizza();
        
        // Assert
        assertFalse(cookingManager.isReadyToTakeOut(), "Should not be ready to take out after taking out");
        assertEquals(0, cookingManager.getStartTime(), "Start time should be reset");
    }

    @Test
    void shouldResetCorrectly() {
        // Arrange
        cookingManager.startCooking();
        advanceTime(cookingManager.getCookTime());
        assertTrue(cookingManager.isReadyToTakeOut(), "Pizza should be ready to take out");
        
        // Act
        cookingManager.reset();
        
        // Assert
        assertFalse(cookingManager.isCooking(), "Should not be cooking after reset");
        assertFalse(cookingManager.isReadyToTakeOut(), "Should not be ready to take out after reset");
        assertEquals(0, cookingManager.getStartTime(), "Start time should be reset");
        assertFalse(cookingManager.isBakingMinigameActive(), "Minigame should not be active after reset");
    }

    @Test
    void shouldHandleCookingSpeedMultiplier() {
        // Arrange
        double multiplier = 2.0;
        cookingManager.setCookingSpeedMultiplier(multiplier);
        
        // Act
        cookingManager.startCooking();
        advanceTime(cookingManager.getCookTime() / 2); // Half the time with 2x speed
        
        // Assert
        assertTrue(cookingManager.isReadyToTakeOut(), "Should be ready to take out with faster cooking speed");
    }

    @Test
    void shouldCalculateCookingProgress() throws Exception {
        // Test initial state
        assertEquals(0.0, cookingManager.getCookingProgress(), 0.01, "Progress should be 0 when not cooking");
        
        // Test cooking in progress
        cookingManager.startCooking();
        advanceTime(cookingManager.getCookTime() / 2);
        assertEquals(0.5, cookingManager.getCookingProgress(), 0.01, "Progress should be 0.5 at halfway");
        
        // Test completed state
        // Use reflection to directly set the state we want to test
        java.lang.reflect.Field isCookingField = CookingManager.class.getDeclaredField("isCooking");
        isCookingField.setAccessible(true);
        isCookingField.set(cookingManager, true);
        
        java.lang.reflect.Field isReadyToTakeOutField = CookingManager.class.getDeclaredField("isReadyToTakeOut");
        isReadyToTakeOutField.setAccessible(true);
        isReadyToTakeOutField.set(cookingManager, true);
        
        assertEquals(1.0, cookingManager.getCookingProgress(), 0.01, "Progress should be 1.0 when ready to take out");
    }

    @Test
    void shouldReturnZeroProgressWhenNotCooking() {
        // Assert
        assertEquals(0.0, cookingManager.getCookingProgress(), "Progress should be 0 when not cooking");
    }

    @Test
    void shouldInitiateBakingMinigame() {
        // Act
        cookingManager.initiateBakingMinigame();
        
        // Assert
        assertTrue(cookingManager.isBakingMinigameActive(), "Minigame should be active after initiation");
        assertEquals(currentTime, cookingManager.getMinigameStartTime(), "Minigame start time should be set");
    }

    @Test
    void shouldNotInitiateMinigameIfAlreadyCooking() {
        // Arrange
        cookingManager.startCooking();
        
        // Act
        cookingManager.initiateBakingMinigame();
        
        // Assert
        assertFalse(cookingManager.isBakingMinigameActive(), "Minigame should not be active if already cooking");
    }

    @Test
    void shouldNotInitiateMinigameIfAlreadyActive() {
        // Arrange
        cookingManager.initiateBakingMinigame();
        long initialStartTime = cookingManager.getMinigameStartTime();
        
        // Act
        advanceTime(500);
        cookingManager.initiateBakingMinigame();
        
        // Assert
        assertTrue(cookingManager.isBakingMinigameActive(), "Minigame should still be active");
        assertEquals(initialStartTime, cookingManager.getMinigameStartTime(), "Minigame start time should not change");
    }

    @Test
    void shouldCalculateOscillatingMinigameProgress() {
        // Arrange
        cookingManager.initiateBakingMinigame();
        long period = CookingManager.MINIGAME_OSCILLATION_PERIOD;
        
        // Assert progress at different points in the cycle
        assertEquals(0.0, cookingManager.getMinigameProgress(), 0.01, "Progress at start");
        
        advanceTime(period / 4); // 1/4 way (moving right)
        assertEquals(0.5, cookingManager.getMinigameProgress(), 0.01, "Progress at 1/4 period");
        
        advanceTime(period / 4); // 1/2 way (peak right)
        assertEquals(1.0, cookingManager.getMinigameProgress(), 0.01, "Progress at 1/2 period");
        
        advanceTime(period / 4); // 3/4 way (moving left)
        assertEquals(0.5, cookingManager.getMinigameProgress(), 0.01, "Progress at 3/4 period");
        
        advanceTime(period / 4); // Full cycle (back at start)
        assertEquals(0.0, cookingManager.getMinigameProgress(), 0.01, "Progress at full period");
    }

    @Test
    void shouldReturnZeroProgressWhenMinigameNotActive() {
        // Assert
        assertEquals(0.0, cookingManager.getMinigameProgress(), "Progress should be 0 when minigame not active");
    }

    @Test
    void shouldAttemptMinigameHitSuccess() {
        // Arrange
        cookingManager.initiateBakingMinigame();
        double targetProgress = (cookingManager.getMinigameSuccessStart() + cookingManager.getMinigameSuccessEnd()) / 2.0;
        long timeToAdvance = 0;
        if (targetProgress <= 1.0) { 
            timeToAdvance = (long) ((targetProgress / 2.0) * CookingManager.MINIGAME_OSCILLATION_PERIOD);
        }
        advanceTime(timeToAdvance); 
        assertTrue(cookingManager.getMinigameProgress() >= cookingManager.getMinigameSuccessStart() &&
                   cookingManager.getMinigameProgress() <= cookingManager.getMinigameSuccessEnd(), 
                   "Test setup failed: progress not in success zone");

        // Act
        cookingManager.attemptMinigameHit();

        // Assert
        assertFalse(cookingManager.isBakingMinigameActive(), "Minigame should deactivate after hit");
        assertTrue(cookingManager.isCooking(), "Actual cooking should start after successful hit");
        assertNotEquals(0, cookingManager.getStartTime(), "Cooking start time should be set");
        assertEquals(1.5, cookingManager.getCookingSpeedMultiplier(), "Cooking speed should be increased for success");
        verify(karmaManager).increaseKarmaSlightly();
        verify(karmaManager, never()).decreaseKarmaSlightly();
    }

    @Test
    void shouldAttemptMinigameHitFailure() {
        // Arrange
        cookingManager.initiateBakingMinigame();
        long timeToAdvance = 1; // Advance minimal time - progress should be near 0.0
        advanceTime(timeToAdvance);
        // Check that progress is indeed outside the success zone
        assertTrue(cookingManager.getMinigameProgress() < cookingManager.getMinigameSuccessStart() ||
                   cookingManager.getMinigameProgress() > cookingManager.getMinigameSuccessEnd(), 
                   "Test setup failed: progress is unexpectedly in success zone");

        // Act
        cookingManager.attemptMinigameHit();

        // Assert
        assertFalse(cookingManager.isBakingMinigameActive(), "Minigame should deactivate after hit");
        assertTrue(cookingManager.isCooking(), "Actual cooking should start even after failed hit");
        assertNotEquals(0, cookingManager.getStartTime(), "Cooking start time should be set");
        assertEquals(0.75, cookingManager.getCookingSpeedMultiplier(), "Cooking speed should be decreased for failure");
        verify(karmaManager).decreaseKarmaSlightly();
        verify(karmaManager, never()).increaseKarmaSlightly();
    }

    @Test
    void shouldNotAttemptHitWhenMinigameNotActive() {
        // Arrange - Minigame is not active
        assertFalse(cookingManager.isBakingMinigameActive());
        
        // Act
        cookingManager.attemptMinigameHit();
        
        // Assert
        assertFalse(cookingManager.isCooking(), "Cooking should not start");
        verify(karmaManager, never()).increaseKarmaSlightly();
        verify(karmaManager, never()).decreaseKarmaSlightly();
    }

    @Test
    void shouldRandomizeZoneWithinBounds() {
        // Arrange
        double initialStart = cookingManager.getMinigameSuccessStart();
        double initialEnd = cookingManager.getMinigameSuccessEnd();
        boolean changed = false;

        // Act & Assert - Run multiple times to increase chance of seeing randomization
        for (int i = 0; i < 10; i++) { 
            cookingManager.initiateBakingMinigame();
            double currentStart = cookingManager.getMinigameSuccessStart();
            double currentEnd = cookingManager.getMinigameSuccessEnd();

            // Check bounds
            assertTrue(currentStart >= 0.0, "Start bound check");
            assertTrue(currentEnd <= 1.0, "End bound check");
            assertTrue(currentStart < currentEnd, "Start < End check");

            // Check width
            double width = currentEnd - currentStart;
            assertTrue(width >= CookingManager.MINIGAME_MIN_ZONE_WIDTH - 0.001, "Min width check");
            assertTrue(width <= CookingManager.MINIGAME_MAX_ZONE_WIDTH + 0.001, "Max width check");

            if (Math.abs(currentStart - initialStart) > 0.01 || Math.abs(currentEnd - initialEnd) > 0.01) {
                changed = true;
            }
        }
        assertTrue(changed, "Minigame zone should likely have changed values after 10 randomization's");
    }
} 