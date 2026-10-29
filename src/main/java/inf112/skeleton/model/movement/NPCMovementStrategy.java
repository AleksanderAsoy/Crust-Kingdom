package inf112.skeleton.model.movement;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SpotManager;
import inf112.skeleton.model.orders.Order;


/**
 * This class implements the movement strategy for NPCs (Non-Player Characters).
 * It handles the logic for NPCs entering the pizza shop, placing orders, waiting for pizza, and leaving.
 */
public class NPCMovementStrategy {

    public enum NPCState {
        ENTER, PLACE_ORDER, WAIT_FOR_PIZZA, LEAVE
    }

    private final NPC npc;
    private final IOrderManager orderManager;
    private final int startX;
    private final int startY;
    private NPCState currentState;
    private boolean hasReachedCounter = false;
    private int assignedSpot = -1; // Each NPC will have a spot
    private final SpotManager spotManager;
    /** Used in handlePlaceOrder() for handling robber NPCs stealing money */
    private final GameLogic gameLogic;

    /**
     * Constructor for NPCMovementStrategy.
     * @param npc The NPC to manage.
     * @param orderManager The order manager to handle orders.
     * @param startX The starting X-coordinate.
     * @param startY The starting Y-coordinate.
     * @param gameLogic The game logic to handle game state.
     */
    public NPCMovementStrategy(NPC npc, IOrderManager orderManager, int startX, int startY, GameLogic gameLogic) {
        this.npc = npc;
        this.orderManager = orderManager;
        this.startX = startX;
        this.startY = startY;
        this.spotManager = SpotManager.getInstance();
        this.gameLogic = gameLogic;

        this.currentState = NPCState.ENTER; // Initialize state here
        resetNPC();
    }

    /**
     * Gets the starting X-coordinate.
     * @return The starting X-coordinate.
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Gets the starting Y-coordinate.
     * @return The starting Y-coordinate.
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Resets the NPC to its initial state.
     */
    public void resetNPC() {
        npc.setX(startX);
        npc.setY(startY);
        npc.setSpeed(2);
        npc.setVisible(true);
        currentState = NPCState.ENTER;
        hasReachedCounter = false;
        
        // Releases the spot if it has one
        if (assignedSpot != -1) {
            spotManager.releaseSpot(assignedSpot);
            assignedSpot = -1;
        }
    }

    /** 
     * Updates the NPC's state based on the current state.
    */
    public void update() {
        switch (currentState) {
            case ENTER:
                handleEnter();
                break;
            case PLACE_ORDER:
                handlePlaceOrder();
                break;
            case WAIT_FOR_PIZZA:
                handleWaitForPizza();
                break;
            case LEAVE:
                handleLeave();
                break;
            default:
        }
    }
    

    private void handleEnter() {
        if (assignedSpot == -1) {
            assignedSpot = spotManager.findAndReserveSpot();
        }
    
        if (assignedSpot != -1) {
            int[] spotCoords = spotManager.getSpotCoordinates(assignedSpot);
            if (spotCoords != null) {
                moveTo(spotCoords[0], spotCoords[1], NPCState.PLACE_ORDER);
            }
        }
    }
    

    private void handlePlaceOrder() {
        if (npc.getOrder() == null && !orderManager.isOrderQueueFull()) {
            if ("NpcRobber".equals(npc.getSkin())) {
                handleRobber();
            } else {
                // Regular NPC places order
                orderManager.createOrder();
                Order newOrder = orderManager.getOrders().get(orderManager.getOrders().size() - 1);
                npc.setOrder(newOrder);
                npc.setWaiting(true);
                npc.setDirection("waiting_at_counter");
                PizzaManager.getInstance(orderManager).assignOrderToSpot(newOrder, assignedSpot);
                
                // Add order paper notification at the pizza spot
                int[] pizzaSpot = PizzaManager.getInstance(orderManager).getPizzaSpot(assignedSpot);
                orderManager.addOrderNotification(pizzaSpot[0], pizzaSpot[1], newOrder.getOrderId());
                
                currentState = NPCState.WAIT_FOR_PIZZA;
            }
        }
    }

    /**
     * Handles robber NPC behavior - steals 50% of player's current money
     */
    public void handleRobber() {
        int currentMoney = gameLogic.getMoney();
        int stolenAmount = currentMoney / 2;
        gameLogic.spendMoney(stolenAmount);
        npc.setWaiting(false);
        npc.setDirection("down");
        currentState = NPCState.LEAVE;
        spotManager.releaseSpot(assignedSpot);
        assignedSpot = -1;
    }

    private void handleWaitForPizza() {
        PizzaManager pizzaManager = PizzaManager.getInstance(orderManager);
    
        for (PizzaOnCounter pizza : pizzaManager.getPizzasOnCounter()) {
            if (pizza.getOrderId() == npc.getOrder().getOrderId()) {
                // Use the pizza's remaining time to determine when to leave
                if (pizza.isFinished()) {
                    npc.setWaiting(false);
                    npc.setDirection("down");
                    currentState = NPCState.LEAVE;
    
                    // Add money for the order, handling VIP NPCs
                    pizzaManager.addMoneyForOrder(npc);
                    
                    pizzaManager.removePizzaByOrderId(npc.getOrder().getOrderId());
                    spotManager.releaseSpot(assignedSpot);
                    assignedSpot = -1;
                } else {
                    // Keep waiting while the pizza is still on the counter
                    npc.setWaiting(true);
                    npc.setDirection("waiting_at_counter");
                }
    
                // Since we found the pizza, we can break out of the loop
                break;
            }
        }
    }
    
    /**
     * Rejects the NPC's order.
     */
    public void rejectOrder() {
        if (npc.getOrder() != null) {
            // Release the spot immediately
            if (assignedSpot != -1) {
                spotManager.releaseSpot(assignedSpot);
                assignedSpot = -1;
            }
            
            // Clear the order and update NPC state
            npc.setOrder(null);
            npc.setWaiting(false);
            npc.setDirection("down"); // Set direction to go down (towards exit)
            
            currentState = NPCState.LEAVE;
        }
    }
    
    private void handleLeave() {
        moveTo(startX, startY, null);
    
        if (npc.getX() == startX && npc.getY() == startY) {
            // Release spot before making NPC invisible
            if (assignedSpot != -1) {
                spotManager.releaseSpot(assignedSpot);
                assignedSpot = -1;
            }
            
            // Clear any remaining references
            npc.setOrder(null);
            npc.setWaiting(false);
            npc.setVisible(false);
        }
    }
    

    private void moveTo(int targetX, int targetY, NPCState nextState) {
        // Check if NPC is moving
        boolean isMoving = (Math.abs(npc.getX() - targetX) >= npc.getSpeed() || 
                           Math.abs(npc.getY() - targetY) >= npc.getSpeed());
        
        if (isMoving) {
            // NPC is moving, so it is not waiting
            npc.setWaiting(false);
            
            if (npc.getX() < targetX) {
                npc.setX(Math.min(npc.getX() + npc.getSpeed(), targetX));
                npc.setDirection("right");
            }
            if (npc.getX() > targetX) {
                npc.setX(Math.max(npc.getX() - npc.getSpeed(), targetX));
                npc.setDirection("left");
            }
            if (npc.getY() < targetY) {
                npc.setY(Math.min(npc.getY() + npc.getSpeed(), targetY));
                npc.setDirection("down");
            }
            if (npc.getY() > targetY) {
                npc.setY(Math.max(npc.getY() - npc.getSpeed(), targetY));
                npc.setDirection("up");
            }
        } else if (npc.getX() == targetX && npc.getY() == targetY && !hasReachedCounter) {
            hasReachedCounter = true;
            
            if (nextState != null) {
                currentState = nextState;
            }
        }
    }
}
