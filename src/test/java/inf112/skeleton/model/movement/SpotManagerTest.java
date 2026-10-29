package inf112.skeleton.model.movement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.managers.SpotManager;

/**
 * Test suite for the SpotManager class.
 * Tests spot allocation, reservation, and coordinate management.
 */
class SpotManagerTest {
    private SpotManager spotManager;

    @BeforeEach
    void setUp() {
        SpotManager.resetInstance();
        spotManager = SpotManager.getInstance();
    }

    @Test
    void shouldInitializeWithAllSpotsUnoccupied() {
        // Try to reserve all spots
        for (int i = 0; i < 3; i++) {
            int spotIndex = spotManager.findAndReserveSpot();
            assertNotEquals(-1, spotIndex, "Should be able to reserve spot " + i);
        }
        
        // Next reservation should fail
        assertEquals(-1, spotManager.findAndReserveSpot(), "Should not be able to reserve more spots");
    }

    @Test
    void shouldReleaseSpotCorrectly() {
        // Reserve a spot
        int spotIndex = spotManager.findAndReserveSpot();
        assertNotEquals(-1, spotIndex, "Should be able to reserve a spot");
        
        // Release the spot
        spotManager.releaseSpot(spotIndex);
        
        // Should be able to reserve the spot again
        int newSpotIndex = spotManager.findAndReserveSpot();
        assertNotEquals(-1, newSpotIndex, "Should be able to reserve the spot after release");
    }

    @Test
    void shouldHandleInvalidSpotRelease() {
        // Try to release an invalid spot
        assertDoesNotThrow(() -> spotManager.releaseSpot(-1), "Should handle negative spot index");
        assertDoesNotThrow(() -> spotManager.releaseSpot(10), "Should handle out of bounds spot index");
    }

    @Test
    void shouldReturnCorrectSpotCoordinates() {
        // Check coordinates for each spot
        int[][] expectedCoordinates = {
            {5 * 48, 3 * 48}, // Customer spot 1
            {5 * 48, 4 * 48}, // Customer spot 2
            {5 * 48, 5 * 48}  // Customer spot 3
        };

        for (int i = 0; i < expectedCoordinates.length; i++) {
            int[] coords = spotManager.getSpotCoordinates(i);
            assertArrayEquals(expectedCoordinates[i], coords, 
                "Coordinates for spot " + i + " should match expected values");
        }
    }

    @Test
    void shouldHandleInvalidSpotCoordinates() {
        // Try to get coordinates for invalid spots
        int[] coords = spotManager.getSpotCoordinates(-1);
        assertEquals(0, coords.length, "Should return empty array for negative spot index");
        
        coords = spotManager.getSpotCoordinates(10);
        assertEquals(0, coords.length, "Should return empty array for out of bounds spot index");
    }

    @Test
    void shouldResetAllSpots() {
        // Reserve all spots
        for (int i = 0; i < 3; i++) {
            spotManager.findAndReserveSpot();
        }
        
        // Reset spots
        spotManager.reset();
        
        // Should be able to reserve all spots again
        for (int i = 0; i < 3; i++) {
            int spotIndex = spotManager.findAndReserveSpot();
            assertNotEquals(-1, spotIndex, "Should be able to reserve spot " + i + " after reset");
        }
    }

    @Test
    void shouldResetInstanceCorrectly() {
        // Get first instance
        SpotManager firstInstance = SpotManager.getInstance();
        
        // Reset instance
        SpotManager.resetInstance();
        
        // Get new instance
        SpotManager secondInstance = SpotManager.getInstance();
        
        assertNotSame(firstInstance, secondInstance, "Should create new instance after reset");
    }

    @Test
    void shouldHandleConcurrentSpotReservation() {
        // Reserve spots in sequence
        int spot1 = spotManager.findAndReserveSpot();
        int spot2 = spotManager.findAndReserveSpot();
        int spot3 = spotManager.findAndReserveSpot();
        
        // Verify all spots are different
        assertNotEquals(spot1, spot2, "Spots should be different");
        assertNotEquals(spot2, spot3, "Spots should be different");
        assertNotEquals(spot1, spot3, "Spots should be different");
        
        // Verify all spots are valid
        assertTrue(spot1 >= 0 && spot1 < 3, "Spot 1 should be valid");
        assertTrue(spot2 >= 0 && spot2 < 3, "Spot 2 should be valid");
        assertTrue(spot3 >= 0 && spot3 < 3, "Spot 3 should be valid");
    }
} 