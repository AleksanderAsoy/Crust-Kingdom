package inf112.skeleton.model.interfaces;

import java.awt.image.BufferedImage;

/**
 * Interface for sprite handling.
 * This interface defines the contract for how sprites should be handled.
 */
public interface ISprite {
    /**
     * Gets the current sprite based on direction and animation state.
     */
    BufferedImage getCurrentSprite();
    
    /**
     * Gets the direction the unit is moving in.
     */
    String getDirection();
    
    /**
     * Gets the X-position of the unit.
     */
    int getX();
    
    /**
     * Gets the Y-position of the unit.
     */
    int getY();
    
    /**
     * Checks if the unit is visible.
     */
    boolean isVisible();
} 