package inf112.skeleton.model.entities;

import inf112.skeleton.model.interfaces.IEntity;

/**
 * Abstract base class for entities in the game.
 * Provides common implementation for the Entity interface.
 */
public abstract class AbstractEntity implements IEntity {
    // Position and movement - core model data
    protected int x;
    protected int y;
    protected int speed;
    protected String direction;
    protected int width;
    protected int height;
    
    // Animation variables - needed to keep track of animation state
    protected int spriteCounter = 0;
    protected int spriteNum = 1;
    
    // Constant for animation
    private static final int MAX_SPRITE_COUNTER = 20;
    
    @Override
    public String getDirection() {
        return direction;
    }
    
    @Override
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public void setX(int x) {
        this.x = x;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    @Override
    public void setY(int y) {
        this.y = y;
    }
    
    @Override
    public int getSpeed() {
        return speed;
    }
    
    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    @Override
    public int getSpriteCounter() {
        return spriteCounter;
    }
    
    @Override
    public void setSpriteCounter(int spriteCounter) {
        this.spriteCounter = spriteCounter;
    }
    
    @Override
    public int getSpriteNum() {
        return spriteNum;
    }
    
    @Override
    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }
    
    @Override
    public int getAnimationState() {
        return spriteNum;
    }
    
    @Override
    public boolean isVisible() {
        return true;
    }
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public void setWidth(int width) {
        this.width = width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    @Override
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Updates the animation state.
     * Should be called in the update method of subclasses.
     */
    protected void updateAnimation() {
        spriteCounter++;
        if (spriteCounter > MAX_SPRITE_COUNTER) {
            spriteNum = spriteNum == 1 ? 2 : 1;
            spriteCounter = 0;
        }
    }
} 