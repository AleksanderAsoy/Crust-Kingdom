package inf112.skeleton.model.entities;

import java.awt.image.BufferedImage;

import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.interfaces.INPC;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.movement.NPCMovementStrategy;
import inf112.skeleton.model.orders.Order;
import inf112.skeleton.view.sprites.NPCSprite;

/**
 * Represents an NPC (Non-Player Character) in the game.
 * Handles AI behavior, movement, and interactions.
 */
public class NPC extends AbstractEntity implements INPC {

    private final NPCMovementStrategy movementStrategy;
    private final int npcId;
    private static int npcCounter = 0;
    private boolean visible;
    private boolean waiting;
    private NPCSprite sprite;
    private Order currentOrder;
    private long lastUpdateTime;
    private static final long UPDATE_INTERVAL = 16; // ~60 FPS
    private final String skin;
    private final boolean isVIP;

    /**
     * Creates a new NPC with default skin at the specified position.
     * 
     * @param orderManager The order manager for handling orders
     * @param startX The initial X position
     * @param startY The initial Y position
     * @param gameLogic The game logic instance
     */
    public NPC(IOrderManager orderManager, int startX, int startY, GameLogic gameLogic) {
        this(orderManager, startX, startY, "Npc", gameLogic); // Default skin
    }

    /**
     * Creates a new NPC with the specified skin at the specified position.
     * 
     * @param orderManager The order manager for handling orders
     * @param startX The initial X position
     * @param startY The initial Y position
     * @param skin The skin to use for the NPC
     * @param gameLogic The game logic instance
     */
    public NPC(IOrderManager orderManager, int startX, int startY, String skin, GameLogic gameLogic) {
        this.npcId = npcCounter++;
        this.skin = skin;
        this.isVIP = skin.equals("NpcVIP");
        this.sprite = new NPCSprite(this);
        this.movementStrategy = new NPCMovementStrategy(this, orderManager, startX, startY, gameLogic);
        this.lastUpdateTime = System.currentTimeMillis();
        this.setDirection("down"); // Initialize with default direction
        this.width = 48; // NPCs are 48x48 pixels
        this.height = 48;
    }

    /**
     * Updates the NPC's state, including movement and animation.
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= UPDATE_INTERVAL) {
            movementStrategy.update();
            updateNpcAnimation();
            lastUpdateTime = currentTime;
        }
    }

    /**
     * Updates the NPC's animation based on its current state.
     */
    public void updateNpcAnimation() {
        if ((!waiting || !getDirection().equals("waiting_at_counter")) && isMoving()) {
            updateAnimation();
        }
    }

    private boolean isMoving() {
        return !getDirection().equals("waiting_at_counter") && 
               (getX() != movementStrategy.getStartX() || getY() != movementStrategy.getStartY());
    }

    /**
     * Checks if the NPC is waiting.
     * 
     * @return true if the NPC is waiting, false otherwise
     */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * Sets whether the NPC is waiting.
     * 
     * @param waiting true if the NPC should wait, false otherwise
     */
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /**
     * Gets the NPC's unique identifier.
     * 
     * @return The NPC's ID
     */
    @Override
    public int getNpcId() {
        return npcId;
    }

    /**
     * Checks if the NPC is visible.
     * 
     * @return true if the NPC is visible, false otherwise
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether the NPC is visible.
     * 
     * @param visible true if the NPC should be visible, false otherwise
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets the current sprite for the NPC.
     * 
     * @return The current sprite
     */
    public BufferedImage getSprite() {
        return sprite.getCurrentSprite();
    }

    /**
     * Sets the current order for the NPC.
     * 
     * @param order The order to set
     */
    public void setOrder(Order order) {
        this.currentOrder = order;
    }
    
    /**
     * Gets the current order for the NPC.
     * 
     * @return The current order
     */
    public Order getOrder() {
        return this.currentOrder;
    }

    /**
     * Cleans up resources used by the NPC.
     */
    public void cleanup() {
        // Clear all references
        this.currentOrder = null;
        this.sprite = null;
        this.visible = false;
        this.waiting = false;
        this.lastUpdateTime = 0;
        this.setDirection("down"); // Reset to default direction
        // Note: movementStrategy is final and cannot be nulled
    }

    /**
     * Gets the movement strategy for the NPC.
     * 
     * @return The movement strategy
     */
    public NPCMovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    /**
     * Gets the skin of the NPC.
     * 
     * @return The skin name
     */
    public String getSkin() {
        return skin;
    }

    /**
     * Checks if the NPC is a VIP.
     * 
     * @return true if the NPC is a VIP, false otherwise
     */
    public boolean isVIP() {
        return isVIP;
    }

    @Override
    public boolean isWaitingForPizza() {
        return waiting && currentOrder != null;
    }
}
