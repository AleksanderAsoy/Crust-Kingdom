package inf112.skeleton.model.interfaces;

/**
 * Interface representing an entity in the game.
 * Used to create the player and NPCs.
 */
public interface IEntity {
    /**
     * Updates the entity's state.
     */
    void update();
    
    /**
     * Gets the current direction for the entity.
     */
    String getDirection();
    
    /**
     * Sets the direction for the entity.
     */
    void setDirection(String direction);
    
    /**
     * Gets the X-position of the entity.
     */
    int getX();
    
    /**
     * Sets the X-position of the entity.
     */
    void setX(int x);
    
    /**
     * Gets the Y-position of the entity.
     */
    int getY();
    
    /**
     * Sets the Y-position of the entity.
     */
    void setY(int y);
    
    /**
     * Gets the speed of the entity.
     */
    int getSpeed();
    
    /**
     * Sets the speed of the entity.
     */
    void setSpeed(int speed);
    
    /**
     * Gets the sprite counter for animation.
     */
    int getSpriteCounter();
    
    /**
     * Sets the sprite counter for animation.
     */
    void setSpriteCounter(int spriteCounter);
    
    /**
     * Gets the sprite number for animation.
     */
    int getSpriteNum();
    
    /**
     * Sets the sprite number for animation.
     */
    void setSpriteNum(int spriteNum);
    
    /**
     * Gets the current animation state (1 or 2).
     */
    int getAnimationState();
    
    /**
     * Checks if the entity is visible.
     */
    boolean isVisible();
    
    /**
     * Gets the width of the entity.
     */
    int getWidth();
    
    /**
     * Sets the width of the entity.
     */
    void setWidth(int width);
    
    /**
     * Gets the height of the entity.
     */
    int getHeight();
    
    /**
     * Sets the height of the entity.
     */
    void setHeight(int height);
}
