package inf112.skeleton.model.interfaces.managers;

import inf112.skeleton.model.managers.KarmaManager;

/**
 * Interface defining the contract for managing pizza cooking operations.
 * Handles cooking state, progress, timing, and cooking minigame.
 */
public interface ICookingManager {
    /**
     * Starts cooking a pizza from the queue.
     */
    void startCooking();

    /**
     * Updates the cooking manager state.
     */
    void update();

    /**
     * Checks if a pizza is currently cooking.
     * 
     * @return true if a pizza is cooking, false otherwise
     */
    boolean isCooking();

    /**
     * Checks if a pizza is ready to be taken out.
     * 
     * @return true if a pizza is ready, false otherwise
     */
    boolean isReadyToTakeOut();

    /**
     * Takes out the pizza.
     */
    void takeOutPizza();

    /**
     * Gets the current cooking progress.
     * 
     * @return cooking progress as a value between 0.0 and 1.0
     */
    double getCookingProgress();

    /**
     * Gets the cooking speed multiplier.
     * 
     * @return the cooking speed multiplier
     */
    double getCookingSpeedMultiplier();

    /**
     * Sets the cooking speed multiplier.
     * 
     * @param multiplier the new cooking speed multiplier
     */
    void setCookingSpeedMultiplier(double multiplier);

    /**
     * Gets the base cooking time in milliseconds.
     * 
     * @return the base cooking time
     */
    long getCookTime();

    /**
     * Gets the start time of the current cooking process.
     * 
     * @return the start time in milliseconds
     */
    long getStartTime();

    /**
     * Sets the sound manager reference.
     * 
     * @param sound the sound manager to set
     */
    void setSound(ISoundManager sound);

    /**
     * Sets the karma manager reference.
     * 
     * @param karmaManager the karma manager to set
     */
    void setKarmaManager(KarmaManager karmaManager);

    /**
     * Initiates the baking minigame.
     */
    void initiateBakingMinigame();

    /**
     * Checks if the baking minigame is active.
     * 
     * @return true if the minigame is active, false otherwise
     */
    boolean isBakingMinigameActive();

    /**
     * Gets the minigame progress.
     * 
     * @return the minigame progress as a value between 0.0 and 1.0
     */
    double getMinigameProgress();

    /**
     * Attempts a hit in the minigame.
     */
    void attemptMinigameHit();

    /**
     * Gets the start of the success zone for the minigame.
     * 
     * @return the start of the success zone
     */
    double getMinigameSuccessStart();

    /**
     * Gets the end of the success zone for the minigame.
     * 
     * @return the end of the success zone
     */
    double getMinigameSuccessEnd();

    /**
     * Gets the start time of the minigame.
     * 
     * @return the start time in milliseconds
     */
    long getMinigameStartTime();

    /**
     * Resets the cooking manager state.
     */
    void reset();
} 