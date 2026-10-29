package inf112.skeleton.model.managers;

import java.util.Arrays;

/**
 * Handles the allocation of spots at the counter for NPCs.
 * Ensures that each spot can only be used by one NPC at a time.
 */
public class SpotManager {
    private static SpotManager instance;
    private static final int[][] COUNTER_SPOTS = {
        {5 * 48, 3 * 48}, // Customer spot 1
        {5 * 48, 4 * 48}, // Customer spot 2
        {5 * 48, 5 * 48}  // Customer spot 3
    };
    private final boolean[] spotOccupied;
    
    private SpotManager() {
        this.spotOccupied = new boolean[COUNTER_SPOTS.length];
    }
    
    public static SpotManager getInstance() {
        if (instance == null) {
            instance = new SpotManager();
        }
        return instance;
    }
    
    /**
     * Finds and reserves an available spot.
     * @return The index of the reserved spot, or -1 if all are occupied
     */
    public synchronized int findAndReserveSpot() {
        for (int i = 0; i < spotOccupied.length; i++) {
            if (!spotOccupied[i]) {
                spotOccupied[i] = true;
                return i;
            }
        }
        return -1; // All spots are occupied
    }
    
    /**
     * Releases a previously reserved spot.
     * @param spotIndex The index of the spot to be released
     */
    public synchronized void releaseSpot(int spotIndex) {
        if (spotIndex >= 0 && spotIndex < spotOccupied.length) {
            spotOccupied[spotIndex] = false;
        }
    }
    
    /**
     * Gets the coordinates for a specific spot.
     * @param spotIndex The index of the spot
     * @return An array with [x, y] coordinates, or an empty array if the index is invalid
     */
    public int[] getSpotCoordinates(int spotIndex) {
        if (spotIndex >= 0 && spotIndex < COUNTER_SPOTS.length) {
            return COUNTER_SPOTS[spotIndex];
        }
        return new int[0]; // Return an empty array instead of null
    }

    /**
     * Resets all spots to unoccupied state
     */
    public void reset() {
        Arrays.fill(spotOccupied, false);
    }

    /**
     * Resets the instance to its initial state
     */
    public static void resetInstance() {
        instance = null;
    }
} 