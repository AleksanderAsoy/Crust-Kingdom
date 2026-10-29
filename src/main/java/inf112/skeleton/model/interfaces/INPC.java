package inf112.skeleton.model.interfaces;

/**
 * Interface that defines the behavior of an NPC (Non-Player Character).
 * This separates the NPC logic from the implementation.
 */
public interface INPC {
    /**
     * Updates the NPC's state.
     */
    void update();
    
    /**
     * Gets the NPC's X-position.
     */
    int getX();
    
    /**
     * Gets the NPC's Y-position.
     */
    int getY();
    
    /**
     * Gets the NPC's direction.
     */
    String getDirection();
    
    /**
     * Checks if the NPC is visible.
     */
    boolean isVisible();
    
    /**
     * Gets the NPC's animation state.
     */
    int getAnimationState();
    
    /**
     * Checks if the NPC is waiting.
     */
    boolean isWaiting();
    
    /**
     * Checks if the NPC is waiting for a pizza.
     * 
     * @return true if the NPC is waiting for a pizza, false otherwise
     */
    boolean isWaitingForPizza();
    
    /**
     * Gets the NPC's unique ID.
     */
    int getNpcId();
} 