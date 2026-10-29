package inf112.skeleton.model.interfaces.managers;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.orders.Order;
import inf112.skeleton.model.entities.Store;

import java.util.List;
import java.util.Queue;

/**
 * Interface defining the contract for managing pizza queues and counter operations.
 * Handles pizza queue management, counter operations, order assignments, and money.
 */
public interface IPizzaQueueManager {
    /**
     * Gets the list of pizzas on the counter.
     * 
     * @return list of pizzas on the counter
     */
    List<PizzaOnCounter> getPizzasOnCounter();

    /**
     * Gets the pizza queue.
     * 
     * @return the pizza queue
     */
    Queue<String> getPizzaQueue();

    /**
     * Adds a pizza to the cooking queue.
     */
    void addPizzaToQueue();

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
     * Removes an order from a spot.
     * 
     * @param order the order to remove
     */
    void removeOrderFromSpot(Order order);

    /**
     * Places a pizza at a spot for an order.
     * 
     * @param order the order to place the pizza for
     */
    void placePizzaAtSpot(Order order);

    /**
     * Removes a pizza by order id.
     * 
     * @param orderId the id of the order
     */
    void removePizzaByOrderId(int orderId);

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
     * Resets the queue manager state.
     */
    void reset();

    /**
     * Removes the current pizza from the queue.
     */
    void removeCurrentPizza();

    /**
     * Sets the store reference.
     * 
     * @param store the store to set
     */
    void setStore(Store store);

    /**
     * Gets the current pizza in the queue.
     * 
     * @return the current pizza type, or null if none
     */
    String getCurrentPizza();
} 