package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;
import java.util.logging.Logger;

import inf112.skeleton.model.interfaces.managers.ICookingManager;
import inf112.skeleton.model.interfaces.managers.ISoundManager;

/**
 * Manages the cooking process for pizzas, including the cooking minigame.
 */
public class CookingManager implements Serializable, ICookingManager {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CookingManager.class.getName());

    private boolean isCooking;
    private long startTime;
    private static final long COOK_TIME = 2_000; // 2 seconds
    private boolean isReadyToTakeOut;
    private double cookingSpeedMultiplier = 1.0;
    private ISoundManager sound;

    // Minigame related fields
    private boolean isBakingMinigameActive = false;
    private long minigameStartTime;
    public static final long MINIGAME_OSCILLATION_PERIOD = 2000; // Time for one full back-and-forth cycle (ms)
    private double minigameSuccessStart = 0.7; // Default value, will be randomized
    private double minigameSuccessEnd = 0.9;   // Default value, will be randomized
    public static final double MINIGAME_MIN_ZONE_WIDTH = 0.15; // Minimum width of the success zone (e.g., 15%)
    public static final double MINIGAME_MAX_ZONE_WIDTH = 0.30; // Maximum width of the success zone (e.g., 30%)
    private KarmaManager karmaManager;
    private final transient Random random = new Random();

    public CookingManager() {
        this.isCooking = false;
        this.isReadyToTakeOut = false;
    }

    /**
     * Starts cooking a pizza.
     */
    public void startCooking() {
        if (!isCooking) {
            this.isCooking = true;
            this.isReadyToTakeOut = false;
            this.startTime = getCurrentTimeMillis();
            LOGGER.info("Started cooking pizza.");
        } else {
            LOGGER.info("Already cooking a pizza.");
        }
    }

    /**
     * Updates the cooking state.
     */
    public void update() {
        // Update normal cooking state if not in minigame
        if (!isBakingMinigameActive) {
            updateCookingState();
        }
    }

    /**
     * Updates the status of the pizza being cooked.
     */
    private void updateCookingState() {
        if (isCooking) {
            long elapsedTime = getCurrentTimeMillis() - startTime;
            if (elapsedTime >= (COOK_TIME / cookingSpeedMultiplier)) {
                completePizza();
            }
        }
    }

    /**
     * Completes the pizza and marks it as ready to take out.
     */
    public void completePizza() {
        isCooking = false;
        isReadyToTakeOut = true;
        if (sound != null) {
            sound.play(6); // Play PizzaDone sound
        }
    }

    /**
     * Takes out the pizza.
     * Note: This method is only called when a pizza is ready to take out,
     * as enforced by the game's UI and interaction logic.
     */
    public void takeOutPizza() {
        isCooking = false;
        isReadyToTakeOut = false;
        startTime = 0;
    }

    /**
     * Checks if the pizza is ready to take out.
     * 
     * @return true if the pizza is ready to take out, false otherwise
     */
    public boolean isReadyToTakeOut() {
        return isReadyToTakeOut;
    }

    /**
     * Checks if a pizza is currently cooking.
     * 
     * @return true if a pizza is cooking, false otherwise
     */
    public boolean isCooking() {
        return isCooking;
    }

    /**
     * Gets the start time of the current cooking process.
     * 
     * @return the start time in milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the base cooking time.
     * 
     * @return the cooking time in milliseconds
     */
    public long getCookTime() {
        return COOK_TIME;
    }

    /**
     * Gets the current time in milliseconds.
     * 
     * @return the current time in milliseconds
     */
    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Resets the cooking manager state.
     */
    public void reset() {
        isCooking = false;
        isReadyToTakeOut = false;
        startTime = 0;
        isBakingMinigameActive = false;
        minigameStartTime = 0;
        randomizeMinigameZone();
    }

    /**
     * Sets the cooking speed multiplier.
     * 
     * @param multiplier the new cooking speed multiplier
     */
    public void setCookingSpeedMultiplier(double multiplier) {
        this.cookingSpeedMultiplier = multiplier;
    }

    /**
     * Gets the cooking speed multiplier.
     * 
     * @return the cooking speed multiplier
     */
    public double getCookingSpeedMultiplier() {
        return cookingSpeedMultiplier;
    }

    /**
     * Sets the sound manager.
     * 
     * @param sound the sound manager
     */
    public void setSound(ISoundManager sound) {
        this.sound = sound;
    }

    /**
     * Gets the cooking progress.
     * 
     * @return the cooking progress as a value between 0.0 and 1.0
     */
    public double getCookingProgress() {
        if (!isCooking) {
            return 0.0;
        }
        if (isReadyToTakeOut) {
            return 1.0;
        }
        long currentTime = getCurrentTimeMillis();
        long elapsedTime = currentTime - startTime;
        double adjustedCookTime = COOK_TIME / cookingSpeedMultiplier;
        double progress = elapsedTime / adjustedCookTime;
        return Math.min(1.0, Math.max(0.0, progress));
    }

    /**
     * Sets the karma manager.
     * 
     * @param karmaManager the karma manager
     */
    public void setKarmaManager(KarmaManager karmaManager) {
        this.karmaManager = karmaManager;
    }

    /**
     * Initiates the baking minigame.
     */
    public void initiateBakingMinigame() {
        if (!isCooking && !isBakingMinigameActive) {
            isBakingMinigameActive = true;
            minigameStartTime = getCurrentTimeMillis();
            randomizeMinigameZone();
            LOGGER.info("Baking minigame initiated");
        } else {
            LOGGER.info("Cannot initiate minigame: already cooking or minigame active");
        }
    }

    /**
     * Checks if the baking minigame is active.
     * 
     * @return true if the minigame is active, false otherwise
     */
    public boolean isBakingMinigameActive() {
        return isBakingMinigameActive;
    }

    /**
     * Gets the minigame progress.
     * 
     * @return the minigame progress as a value between 0.0 and 1.0
     */
    public double getMinigameProgress() {
        if (!isBakingMinigameActive) {
            return 0.0;
        }
        
        long elapsedTime = getCurrentTimeMillis() - minigameStartTime;
        double cycleProgress = (elapsedTime % MINIGAME_OSCILLATION_PERIOD) / (double) MINIGAME_OSCILLATION_PERIOD;
        
        // Oscillate between 0.0 and 1.0
        if (cycleProgress < 0.5) {
            return cycleProgress * 2.0; // 0.0 to 1.0
        } else {
            return (1.0 - cycleProgress) * 2.0; // 1.0 to 0.0
        }
    }

    /**
     * Attempts a hit in the minigame.
     */
    public void attemptMinigameHit() {
        if (!isBakingMinigameActive) {
            LOGGER.warning("Cannot attempt minigame hit: minigame not active");
            return;
        }
        
        double currentProgress = getMinigameProgress();
        boolean success = currentProgress >= minigameSuccessStart && currentProgress <= minigameSuccessEnd;
        
        // Update karma based on success
        if (karmaManager != null) {
            if (success) {
                karmaManager.increaseKarmaSlightly(); // Reward for successful minigame
            } else {
                karmaManager.decreaseKarmaSlightly(); // Small penalty for failure
            }
        }
        
        // Start actual cooking regardless of success/failure
        startActualCooking(success);
    }

    /**
     * Starts the actual cooking process after the minigame.
     * 
     * @param success whether the minigame was successful
     */
    private void startActualCooking(boolean success) {
        isBakingMinigameActive = false;
        
        // Apply cooking speed multiplier based on minigame success
        if (success) {
            setCookingSpeedMultiplier(1.5); // Faster cooking for success
        } else {
            setCookingSpeedMultiplier(0.75); // Slower cooking for failure
        }
        
        // Start the actual cooking process
        startCooking();
    }

    /**
     * Randomizes the success zone for the minigame.
     */
    private void randomizeMinigameZone() {
        // Calculate a random width for the success zone
        double zoneWidth = MINIGAME_MIN_ZONE_WIDTH + 
                          random.nextDouble() * (MINIGAME_MAX_ZONE_WIDTH - MINIGAME_MIN_ZONE_WIDTH);
        
        // Calculate a random start position that allows for the full width
        double maxStart = 1.0 - zoneWidth;
        minigameSuccessStart = random.nextDouble() * maxStart;
        minigameSuccessEnd = minigameSuccessStart + zoneWidth;
    }

    /**
     * Gets the start of the success zone for the minigame.
     * 
     * @return the start of the success zone
     */
    public double getMinigameSuccessStart() {
        return minigameSuccessStart;
    }

    /**
     * Gets the end of the success zone for the minigame.
     * 
     * @return the end of the success zone
     */
    public double getMinigameSuccessEnd() {
        return minigameSuccessEnd;
    }

    /**
     * Gets the start time of the minigame.
     * 
     * @return the start time in milliseconds
     */
    public long getMinigameStartTime() {
        return minigameStartTime;
    }
} 