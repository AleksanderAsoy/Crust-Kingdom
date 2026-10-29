package inf112.skeleton.model.interfaces.managers;

import java.io.Serializable;

/**
 * Interface for managing sound in the game.
 */
public interface ISoundManager extends Serializable {
    /**
     * Plays a sound effect.
     * 
     * @param soundIndex the index of the sound to play
     */
    void play(int soundIndex);

    /**
     * Loops a sound effect.
     * 
     * @param soundIndex the index of the sound to loop
     */
    void loop(int soundIndex);

    /**
     * Stops the currently looping sound.
     */
    void stopLoop();

    /**
     * Checks if sound is enabled.
     * 
     * @return true if sound is enabled, false otherwise
     */
    boolean isSoundEnabled();

    /**
     * Sets whether sound is enabled.
     * 
     * @param enabled true to enable sound, false to disable
     */
    void setSoundEnabled(boolean enabled);

    /**
     * Gets the current sound volume.
     * 
     * @return the current sound volume
     */
    float getVolume();

    /**
     * Sets the sound volume.
     * 
     * @param volume the new sound volume
     */
    void setVolume(float volume);
} 