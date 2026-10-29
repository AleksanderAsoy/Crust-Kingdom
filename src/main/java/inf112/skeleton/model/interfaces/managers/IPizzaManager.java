package inf112.skeleton.model.interfaces.managers;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.managers.KarmaManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.orders.Order;

import java.util.List;

/**
 * Interface defining the contract for managing pizzas in the game.
 * This interface serves as a facade for the pizza management system,
 * delegating to CookingManager and PizzaQueueManager.
 */
public interface IPizzaManager {
    /**
     * Gets the singleton instance of the PizzaManager.
     * 
     * @param orderManager the order manager to use
     * @return the singleton instance of the PizzaManager
     */
    static IPizzaManager getInstance(IOrderManager orderManager) {
        return PizzaManager.getInstance(orderManager);
    }

    /**
     * Starts cooking a pizza.
     */
    void startCooking();

    /**
     * Updates the pizza manager state.
     */
    void update();

    /**
     * Checks if a pizza is currently cooking.
     * 
     * @return true if a pizza is cooking, false otherwise
     */
    boolean isCooking();

    /**
     * Checks if a pizza is ready to be taken out.
     * 
     * @return true if a pizza is ready, false otherwise
     */
    boolean isReadyToTakeOut();

    /**
     * Takes out the pizza.
     */
    void takeOutPizza();

    /**
     * Gets the cooking progress.
     * 
     * @return the cooking progress as a value between 0.0 and 1.0
     */
    double getCookingProgress();

    /**
     * Gets the list of pizzas on the counter.
     * 
     * @return list of pizzas on the counter
     */
    List<PizzaOnCounter> getPizzasOnCounter();

    /**
     * Adds a pizza to the cooking queue.
     */
    void addPizzaToQueue();

    /**
     * Resets the pizza manager state.
     */
    void reset();

    /**
     * Sets the store reference.
     * 
     * @param store the store to set
     */
    void setStore(Store store);

    /**
     * Sets the sound manager reference.
     * 
     * @param sound the sound manager to set
     */
    void setSound(ISoundManager sound);

    /**
     * Gets the current pizza being cooked.
     * 
     * @return the current pizza type, or null if not cooking
     */
    String getCurrentPizza();

    /**
     * Gets the cooking speed multiplier.
     * 
     * @return the cooking speed multiplier
     */
    double getCookingSpeedMultiplier();

    /**
     * Gets the base cooking time in milliseconds.
     * 
     * @return the base cooking time
     */
    long getCookTime();

    /**
     * Gets the start time of the current cooking process.
     * 
     * @return the start time in milliseconds
     */
    long getStartTime();

    /**
     * Checks if there are pizzas in the queue.
     * 
     * @return true if there are pizzas in the queue, false otherwise
     */
    boolean hasPizzasInQueue();

    /**
     * Assigns an order to a specific spot.
     * 
     * @param order the order to assign
     * @param spot the spot to assign to
     */
    void assignOrderToSpot(Order order, int spot);

    /**
     * Gets the coordinates for a pizza spot.
     * 
     * @param spot the spot index
     * @return array containing [x, y] coordinates
     */
    int[] getPizzaSpot(int spot);

    /**
     * Collects the money earned.
     * 
     * @return the money earned
     */
    int collectMoney();

    /**
     * Adds money for an order.
     * 
     * @param npc the NPC that placed the order
     */
    void addMoneyForOrder(NPC npc);

    /**
     * Sets the karma manager.
     * 
     * @param karmaManager the karma manager
     */
    void setKarmaManager(KarmaManager karmaManager);

    /**
     * Initiates the baking minigame.
     */
    void initiateBakingMinigame();

    /**
     * Checks if the baking minigame is active.
     * 
     * @return true if the minigame is active, false otherwise
     */
    boolean isBakingMinigameActive();

    /**
     * Gets the minigame progress.
     * 
     * @return the minigame progress as a value between 0.0 and 1.0
     */
    double getMinigameProgress();

    /**
     * Attempts a hit in the minigame.
     */
    void attemptMinigameHit();

    /**
     * Gets the start of the success zone for the minigame.
     * 
     * @return the start of the success zone
     */
    double getMinigameSuccessStart();

    /**
     * Gets the end of the success zone for the minigame.
     * 
     * @return the end of the success zone
     */
    double getMinigameSuccessEnd();

    /**
     * Places a pizza at a spot for an order.
     * 
     * @param order the order to place the pizza for
     */
    void placePizzaAtSpot(Order order);

    /**
     * Removes an order from a spot.
     * 
     * @param order the order to remove
     */
    void removeOrderFromSpot(Order order);
}