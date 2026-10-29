package inf112.skeleton.model.managers;

import java.io.Serial;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import inf112.skeleton.model.interfaces.managers.ISoundManager;

/**
 * Handles the game's sound effects and music by preloading clips.
 * This includes:
 * - Loading sound files into Clips at startup
 * - Playing preloaded sounds
 * - Looping background music
 */
public class SoundManager implements ISoundManager {
    private static final Logger logger = Logger.getLogger(SoundManager.class.getName());
    @Serial
    private static final long serialVersionUID = 1L;
    private final transient Clip[] preloadedClips; // Array to hold preloaded clips
    private final transient URL[] soundURLs;      // Array to hold sound file URLs
    private boolean soundEnabled;
    private float volume = 1.0f; // Default volume
    private transient Clip currentLoopingClip = null; // Track the looping clip (music)

    /**
     * Constructor for Sound. Initializes arrays and loads sounds.
     */
    public SoundManager() {
        soundURLs = new URL[30];      // Initialize URL array
        preloadedClips = new Clip[30]; // Initialize Clip array
        soundEnabled = true;
        loadSoundURLs();
        preloadAllClips();
    }

    /**
     * Sets the sound enabled state. Stops all sounds if disabled.
     * @param enabled the new sound enabled state
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds(); // Stop everything if sound is disabled
        }
    }

    /**
     * Checks if the sound is enabled.
     * @return true if the sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Gets the current volume level.
     * @return the volume (0.0f to 1.0f)
     */
    public float getVolume() {
        return volume;
    }

    /**
     * Sets the volume for all clips.
     * @param volume the new volume (0.0f to 1.0f)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume)); // Clamp volume
        applyVolumeToAllClips();
    }

    /**
     * Loads the URLs for all sound files.
     */
    private void loadSoundURLs() {
        try {
            soundURLs[0] = getClass().getResource("/sounds/menu.wav");
            soundURLs[1] = getClass().getResource("/sounds/gameplay.wav");
            soundURLs[2] = getClass().getResource("/sounds/coin.wav");
            soundURLs[3] = getClass().getResource("/sounds/powerup.wav");
            soundURLs[4] = getClass().getResource("/sounds/fanfare.wav");
            soundURLs[5] = getClass().getResource("/sounds/storeunlock.wav");
            soundURLs[6] = getClass().getResource("/sounds/PizzaDone.wav");
            // Add other sound URLs here if needed
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load sound URLs. Sound system might be partially functional.", e);
            // Don't disable sound entirely, maybe only some sounds failed
        }
    }

    /**
     * Preloads all sounds specified in soundURLs into Clip objects.
     */
    private void preloadAllClips() {
        for (int i = 0; i < soundURLs.length; i++) {
            if (soundURLs[i] != null) {
                try {
                    AudioInputStream ais = AudioSystem.getAudioInputStream(soundURLs[i]);
                    preloadedClips[i] = AudioSystem.getClip();
                    preloadedClips[i].open(ais);
                    // Add listener to close stream when clip stops/ends (good practice)
                    preloadedClips[i].addLineListener(event -> {
                        // No action needed on stop - clip will be reused
                    });
                    logger.log(Level.INFO, "Successfully preloaded sound: {0}", i);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to preload sound file {0}: {1}", new Object[]{i, soundURLs[i]});
                    preloadedClips[i] = null; // Ensure failed loads are null
                }
            }
        }
        applyVolumeToAllClips(); // Apply initial volume after loading
    }
    
    /**
     * Applies the current volume setting to all preloaded clips.
     */
    private void applyVolumeToAllClips() {
         for (Clip clip : preloadedClips) {
            if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                 try {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // Convert volume (0.0-1.0) to dB gain
                    float dB = volume == 0.0f ? -80.0f : (float) (Math.log10(volume) * 20.0);
                    // Clamp dB to the control's limits
                    dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                    gainControl.setValue(dB);
                 } catch (IllegalArgumentException e) {
                    // This might happen if the clip is closed or in an invalid state
                    logger.log(Level.WARNING, "Could not set volume for a clip - it might be closed.", e);
                 } catch (Exception e) {
                    // Catch other potential exceptions
                    logger.log(Level.WARNING, "Unexpected error setting volume for a clip.", e);
                 }
            }
        }       
    }


    /**
     * Plays the preloaded sound effect at the given index from the beginning.
     * @param i the index of the sound file
     */
    public void play(int i) {
        if (!soundEnabled || i < 0 || i >= preloadedClips.length || preloadedClips[i] == null) {
             if (soundEnabled && (i < 0 || i >= preloadedClips.length || preloadedClips[i] == null)) {
                 logger.log(Level.WARNING, "Attempted to play invalid or non-preloaded sound index: {0}", i);
             }
            return;
        }
        
        Clip clip = preloadedClips[i];
        
        // Stop the clip if it's already running (to restart it)
        if (clip.isRunning()) {
            clip.stop();
        }
        // Rewind to the beginning
        clip.setFramePosition(0); 
        // Start playing
        clip.start();
        logger.log(Level.FINE, "Playing sound: {0}", i);
    }

    /**
     * Plays the preloaded sound at the given index and loops it continuously.
     * Stops any previously looping sound.
     * @param i the index of the sound file
     */
    public void loop(int i) {
        if (!soundEnabled || i < 0 || i >= preloadedClips.length || preloadedClips[i] == null) {
            if (soundEnabled && (i < 0 || i >= preloadedClips.length || preloadedClips[i] == null)) {
                 logger.log(Level.WARNING, "Attempted to loop invalid or non-preloaded sound index: {0}", i);
            }
            return;
        }

        stopLoop(); // Stop any currently looping music

        currentLoopingClip = preloadedClips[i];
        currentLoopingClip.setFramePosition(0); // Start from beginning
        currentLoopingClip.loop(Clip.LOOP_CONTINUOUSLY);
        logger.log(Level.INFO, "Looping sound: {0}", i);
    }
    
    /**
     * Stops the currently looping sound (if any).
     */
    public void stopLoop() {
        if (currentLoopingClip != null && currentLoopingClip.isRunning()) {
            currentLoopingClip.stop();
            logger.log(Level.INFO, "Stopped looping sound.");
        }
        currentLoopingClip = null;
    }

    /**
     * Stops all currently running sound clips (effects and loops).
     */
    public void stopAllSounds() {
         for (Clip clip : preloadedClips) {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        }
        currentLoopingClip = null; // Ensure looping clip reference is cleared
        logger.log(Level.INFO, "Stopped all sounds.");
    }
} 