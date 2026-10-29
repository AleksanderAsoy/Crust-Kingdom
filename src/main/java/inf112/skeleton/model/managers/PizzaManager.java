package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;
import java.util.Queue;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.interfaces.managers.ICookingManager;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.interfaces.managers.IPizzaManager;
import inf112.skeleton.model.interfaces.managers.IPizzaQueueManager;
import inf112.skeleton.model.interfaces.managers.ISoundManager;
import inf112.skeleton.model.orders.Order;

/**
 * Manages pizzas in the game by delegating to CookingManager and PizzaQueueManager.
 * This class serves as a facade for the pizza management system.
 */
public class PizzaManager implements Serializable, IPizzaManager {
    @Serial
    private static final long serialVersionUID = 1L;
    private static PizzaManager instance;

    private final transient ICookingManager cookingManager;
    private final transient IPizzaQueueManager queueManager;
    private final transient IOrderManager orderManager;

    /**
     * Creates a new PizzaManager with the specified OrderManager.
     * 
     * @param orderManager the order manager to use
     */
    public PizzaManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
        this.cookingManager = new CookingManager();
        this.queueManager = new PizzaQueueManager(orderManager);
    }

    /**
     * Gets the singleton instance of the PizzaManager.
     * 
     * @param orderManager the order manager to use
     * @return the singleton instance of the PizzaManager
     */
    public static PizzaManager getInstance(IOrderManager orderManager) {
        if (instance == null) {
            instance = new PizzaManager(orderManager);
        }
        return instance;
    }

    /**
     * Starts cooking a pizza.
     */
    public void startCooking() {
        cookingManager.startCooking();
    }

    /**
     * Updates the pizza manager state.
     */
    public void update() {
        cookingManager.update();
    }

    /**
     * Checks if a pizza is currently cooking.
     * 
     * @return true if a pizza is cooking, false otherwise
     */
    public boolean isCooking() {
        return cookingManager.isCooking();
    }

    /**
     * Checks if a pizza is ready to be taken out.
     * 
     * @return true if a pizza is ready, false otherwise
     */
    public boolean isReadyToTakeOut() {
        return cookingManager.isReadyToTakeOut();
    }

    /**
     * Takes out the pizza.
     */
    public void takeOutPizza() {
        if (cookingManager.isReadyToTakeOut()) {
            cookingManager.takeOutPizza();
            if (!orderManager.getAcceptedOrders().isEmpty()) {
                Order completedOrder = orderManager.getAcceptedOrders().remove(0);
                queueManager.placePizzaAtSpot(completedOrder);
            }
            queueManager.removeCurrentPizza();
        }
    }

    /**
     * Gets the cooking progress.
     * 
     * @return the cooking progress as a value between 0.0 and 1.0
     */
    public double getCookingProgress() {
        return cookingManager.getCookingProgress();
    }

    /**
     * Gets the list of pizzas on the counter.
     * 
     * @return list of pizzas on the counter
     */
    public java.util.List<PizzaOnCounter> getPizzasOnCounter() {
        return queueManager.getPizzasOnCounter();
    }

    /**
     * Adds a pizza to the cooking queue.
     */
    public void addPizzaToQueue() {
        queueManager.addPizzaToQueue();
    }

    /**
     * Resets the pizza manager state.
     */
    public void reset() {
        cookingManager.reset();
        queueManager.reset();
    }

    /**
     * Sets the store reference.
     * 
     * @param store the store to set
     */
    public void setStore(Store store) {
        queueManager.setStore(store);
    }

    /**
     * Sets the sound manager reference.
     * 
     * @param sound the sound manager to set
     */
    public void setSound(ISoundManager sound) {
        cookingManager.setSound(sound);
    }

    /**
     * Gets the current pizza being cooked.
     * 
     * @return the current pizza type, or null if not cooking
     */
    public String getCurrentPizza() {
        return queueManager.getCurrentPizza();
    }

    /**
     * Gets the cooking speed multiplier.
     * 
     * @return the cooking speed multiplier
     */
    public double getCookingSpeedMultiplier() {
        return cookingManager.getCookingSpeedMultiplier();
    }

    /**
     * Sets the cooking speed multiplier.
     * 
     * @param multiplier the new cooking speed multiplier
     */
    public void setCookingSpeedMultiplier(double multiplier) {
        cookingManager.setCookingSpeedMultiplier(multiplier);
    }

    /**
     * Gets the base cooking time in milliseconds.
     * 
     * @return the base cooking time
     */
    public long getCookTime() {
        return cookingManager.getCookTime();
    }

    /**
     * Gets the start time of the current cooking process.
     * 
     * @return the start time in milliseconds
     */
    public long getStartTime() {
        return cookingManager.getStartTime();
    }

    /**
     * Checks if there are pizzas in the queue.
     * 
     * @return true if there are pizzas in the queue, false otherwise
     */
    public boolean hasPizzasInQueue() {
        return queueManager.hasPizzasInQueue();
    }

    /**
     * Assigns an order to a specific spot.
     * 
     * @param order the order to assign
     * @param spot the spot to assign to
     */
    public void assignOrderToSpot(Order order, int spot) {
        queueManager.assignOrderToSpot(order, spot);
    }

    /**
     * Gets the coordinates for a pizza spot.
     * 
     * @param spot the spot index
     * @return array containing [x, y] coordinates
     */
    public int[] getPizzaSpot(int spot) {
        return queueManager.getPizzaSpot(spot);
    }

    /**
     * Removes a pizza by order id.
     * 
     * @param orderId the id of the order
     */
    public void removePizzaByOrderId(int orderId) {
        queueManager.removePizzaByOrderId(orderId);
    }

    /**
     * Collects the money earned.
     * 
     * @return the money earned
     */
    public int collectMoney() {
        return queueManager.collectMoney();
    }

    /**
     * Adds money for an order.
     * 
     * @param npc the NPC that placed the order
     */
    public void addMoneyForOrder(NPC npc) {
        queueManager.addMoneyForOrder(npc);
    }

    /**
     * Sets the karma manager.
     * 
     * @param karmaManager the karma manager
     */
    public void setKarmaManager(KarmaManager karmaManager) {
        cookingManager.setKarmaManager(karmaManager);
    }

    /**
     * Initiates the baking minigame.
     */
    public void initiateBakingMinigame() {
        cookingManager.initiateBakingMinigame();
    }

    /**
     * Checks if the baking minigame is active.
     * 
     * @return true if the minigame is active, false otherwise
     */
    public boolean isBakingMinigameActive() {
        return cookingManager.isBakingMinigameActive();
    }

    /**
     * Gets the minigame progress.
     * 
     * @return the minigame progress as a value between 0.0 and 1.0
     */
    public double getMinigameProgress() {
        return cookingManager.getMinigameProgress();
    }

    /**
     * Attempts a hit in the minigame.
     */
    public void attemptMinigameHit() {
        cookingManager.attemptMinigameHit();
    }

    /**
     * Gets the start of the success zone for the minigame.
     * 
     * @return the start of the success zone
     */
    public double getMinigameSuccessStart() {
        return cookingManager.getMinigameSuccessStart();
    }

    /**
     * Gets the end of the success zone for the minigame.
     * 
     * @return the end of the success zone
     */
    public double getMinigameSuccessEnd() {
        return cookingManager.getMinigameSuccessEnd();
    }

    /**
     * Gets the start time of the minigame.
     * 
     * @return the start time in milliseconds
     */
    public long getMinigameStartTime() {
        return cookingManager.getMinigameStartTime();
    }

    /**
     * Gets the pizza queue.
     * 
     * @return the pizza queue
     */
    public Queue<String> getPizzaQueue() {
        return queueManager.getPizzaQueue();
    }

    /**
     * Places a pizza at a spot for an order.
     * 
     * @param order the order to place the pizza for
     */
    public void placePizzaAtSpot(Order order) {
        queueManager.placePizzaAtSpot(order);
    }

    /**
     * Removes an order from a spot.
     * 
     * @param order the order to remove
     */
    public void removeOrderFromSpot(Order order) {
        queueManager.removeOrderFromSpot(order);
    }

    /**
     * Resets the instance of the PizzaManager.
     */
    public static void resetInstance() {
        instance = null;
    }
}