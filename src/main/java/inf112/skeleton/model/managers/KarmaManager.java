package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;

/**
 * Manages the player's karma in the game.
 * Karma affects various aspects of gameplay and is influenced by player actions.
 */
public class KarmaManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final float KARMA_DECREASE_RATE = 0.5f; // Decrease by 0.5 per missed order
    private static final float KARMA_INCREASE_RATE = 0.2f; // Increase by 0.2 per accepted order
    private static final float MAX_KARMA = 10.0f;
    private static final float MIN_KARMA = 0.0f;
    private static final float KARMA_MINIGAME_ADJUSTMENT = 0.1f; // Small adjustment for minigame success/failure
    
    private float currentKarma;
    
    /**
     * Creates a new KarmaManager with neutral karma (5.0).
     */
    public KarmaManager() {
        this.currentKarma = 5.0f; // Start with neutral karma
    }
    
    /**
     * Decreases the player's karma by the defined rate.
     * Karma cannot go below the minimum value.
     */
    public void decreaseKarma() {
        currentKarma = Math.max(MIN_KARMA, currentKarma - KARMA_DECREASE_RATE);
    }
    
    /**
     * Increases the player's karma by the defined rate.
     * Karma cannot exceed the maximum value.
     */
    public void increaseKarma() {
        currentKarma = Math.min(MAX_KARMA, currentKarma + KARMA_INCREASE_RATE);
    }
    
    /**
     * Increases the player's karma by a smaller amount (for minigame success).
     * Karma cannot exceed the maximum value.
     */
    public void increaseKarmaSlightly() {
        currentKarma = Math.min(MAX_KARMA, currentKarma + KARMA_MINIGAME_ADJUSTMENT);
    }
    
    /**
     * Decreases the player's karma by a smaller amount (for minigame failure).
     * Karma cannot go below the minimum value.
     */
    public void decreaseKarmaSlightly() {
        currentKarma = Math.max(MIN_KARMA, currentKarma - KARMA_MINIGAME_ADJUSTMENT);
    }
    
    /**
     * Gets the current karma value.
     * 
     * @return The current karma value
     */
    public float getCurrentKarma() {
        return currentKarma;
    }
    
    /**
     * Resets the karma to the neutral value (5.0).
     */
    public void reset() {
        currentKarma = 5.0f;
    }
} 