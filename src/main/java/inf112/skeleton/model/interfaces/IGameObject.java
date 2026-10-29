package inf112.skeleton.model.interfaces;

import java.awt.image.BufferedImage;

/**
 * Interface for game objects in the game world.
 * Defines common behavior for all game objects.
 */
public interface IGameObject {
    /**
     * Gets the image of the game object.
     * @return the image
     */
    BufferedImage getImage();
    
    /**
     * Gets the X-position of the game object.
     * @return the X-position
     */
    int getX();
    
    /**
     * Gets the Y-position of the game object.
     * @return the Y-position
     */
    int getY();
    
    /**
     * Checks if the game object is solid (collidable).
     * @return true if solid, false otherwise
     */
    boolean isSolid();
    
    /**
     * Checks if the game object is interactable.
     * @return true if interactable, false otherwise
     */
    boolean isInteractable();
    
    /**
     * Gets the type of the game object.
     * @return the type
     */
    String getType();
} 