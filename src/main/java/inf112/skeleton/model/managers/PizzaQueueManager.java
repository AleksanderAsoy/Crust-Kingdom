package inf112.skeleton.model.managers;

import java.awt.image.BufferedImage;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.interfaces.managers.IPizzaQueueManager;
import inf112.skeleton.model.orders.Order;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * Manages the pizza queue and pizzas on the counter.
 */
public class PizzaQueueManager implements Serializable, IPizzaQueueManager {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(PizzaQueueManager.class.getName());

    private final transient Queue<String> pizzaQueue;
    private final transient List<PizzaOnCounter> pizzasOnCounter;
    private static final int MAX_PIZZAS_ON_COUNTER = 3;
    private final transient BufferedImage vegetarianImage;
    private final transient BufferedImage pepperoniImage;
    private final transient BufferedImage cheeseImage;
    private final transient IOrderManager orderManager;
    private Store store;
    private int moneyEarned;

    private static final int[][] PIZZA_SPOTS = {
            { 4 * 48, 3 * 48 }, // Pizza Spot 1
            { 4 * 48, 4 * 48 }, // Pizza Spot 2
            { 4 * 48, 5 * 48 } // Pizza Spot 3
    };

    private final boolean[] pizzaSpotOccupied = { false, false, false };
    private final Map<Integer, Integer> orderToSpotMap = new HashMap<>();

    public PizzaQueueManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
        this.pizzaQueue = new LinkedList<>();
        this.pizzasOnCounter = new ArrayList<>();
        this.moneyEarned = 0;
        // Load the images for the three pizza types
        vegetarianImage = ImageLoader.loadImage("objects/Vegetarian.png");
        pepperoniImage = ImageLoader.loadImage("objects/Pepperoni.png");
        cheeseImage = ImageLoader.loadImage("objects/Cheese.png");
    }

    /**
     * Assigns an order to a spot.
     * 
     * @param order the order to assign
     * @param spot  the spot to assign the order to
     */
    public void assignOrderToSpot(Order order, int spot) {
        if (spot >= 0 && spot < PIZZA_SPOTS.length) {
            orderToSpotMap.put(order.getOrderId(), spot);
            pizzaSpotOccupied[spot] = true;
        }
    }

    /**
     * Removes an order from a spot.
     * 
     * @param order the order to remove
     */
    public void removeOrderFromSpot(Order order) {
        orderToSpotMap.remove(order.getOrderId());
    }

    /**
     * Places a pizza at a spot for an order.
     * 
     * @param order the order to place the pizza for
     */
    public void placePizzaAtSpot(Order order) {
        Integer assignedSpot = orderToSpotMap.get(order.getOrderId());
        if (assignedSpot == null) {
            LOGGER.warning(() -> String.format("No spot assigned for order ID: %d", order.getOrderId()));
            return;
        }

        if (assignedSpot >= PIZZA_SPOTS.length) {
            LOGGER.warning(() -> String.format("Invalid spot assigned: %d", assignedSpot));
            return;
        }

        if (pizzasOnCounter.size() >= MAX_PIZZAS_ON_COUNTER) {
            LOGGER.warning(() -> "Cannot place pizza: counter is full");
            return;
        }

        // Place the new pizza
        int[] spot = PIZZA_SPOTS[assignedSpot];
        pizzasOnCounter.add(new PizzaOnCounter(spot[0], spot[1], getPizzaImage(order.getType()), order.getOrderId()));
        pizzaSpotOccupied[assignedSpot] = true;
    }

    /**
     * Removes a pizza by order id.
     * 
     * @param orderId the id of the order
     */
    public void removePizzaByOrderId(int orderId) {
        Iterator<PizzaOnCounter> iterator = pizzasOnCounter.iterator();
        while (iterator.hasNext()) {
            PizzaOnCounter pizza = iterator.next();
            if (pizza.getOrderId() == orderId) {
                // Find the correct spot and free it
                for (int i = 0; i < PIZZA_SPOTS.length; i++) {
                    if (PIZZA_SPOTS[i][0] == pizza.getX() && PIZZA_SPOTS[i][1] == pizza.getY()) {
                        pizzaSpotOccupied[i] = false;
                        break;
                    }
                }
                iterator.remove(); // Remove the pizza from the counter
                orderToSpotMap.remove(orderId); // Remove the mapping from order to spot
                break;
            }
        }
    }

    /**
     * Gets the pizzas on the counter.
     * 
     * @return the pizzas on the counter
     */
    public List<PizzaOnCounter> getPizzasOnCounter() {
        return pizzasOnCounter;
    }

    /**
     * Adds a pizza to the queue.
     */
    public void addPizzaToQueue() {
        // Check if we have pizzas in inventory before adding to queue
        if (store != null && store.getPizzaStore() != null) {
            String pizzaType = getCurrentCookingPizzaType();
            if (pizzaType != null && store.getPizzaStore().usePizza(pizzaType)) {
                pizzaQueue.offer(pizzaType);
                LOGGER.info(() -> String.format("Added %s pizza to queue. Queue size: %d", pizzaType, pizzaQueue.size()));
            } else {
                LOGGER.warning("Could not add pizza to queue: no pizza type available or no pizzas in inventory");
            }
        } else {
            LOGGER.warning("Could not add pizza to queue: store not initialized");
        }
    }

    /**
     * Gets the current pizza type to cook.
     * 
     * @return the pizza type to cook, or null if none available
     */
    private String getCurrentCookingPizzaType() {
        // First, check if there's an order without a pizza in the queue
        return findOrderWithoutPizza();
    }

    /**
     * Finds an order that doesn't have a pizza in the queue.
     * 
     * @return the pizza type for the order, or null if none found
     */
    private String findOrderWithoutPizza() {
        // First, count how many pizzas of each type are in the queue
        Map<String, Integer> pizzasInQueue = new HashMap<>();
        for (String pizzaType : pizzaQueue) {
            pizzasInQueue.put(pizzaType, pizzasInQueue.getOrDefault(pizzaType, 0) + 1);
        }

        // Then, count how many pizzas of each type are on the counter
        Map<String, Integer> pizzasOnCounterCount = new HashMap<>();
        for (PizzaOnCounter pizza : this.pizzasOnCounter) {
            String type = getPizzaTypeForOrderId(pizza.getOrderId());
            if (type != null) {
                pizzasOnCounterCount.put(type, pizzasOnCounterCount.getOrDefault(type, 0) + 1);
            }
        }

        // Now check each order to see if it needs a pizza
        for (Order order : orderManager.getAcceptedOrders()) {
            String type = order.getType();
            int neededPizzas = countOrdersOfType(type);
            int availablePizzas = pizzasInQueue.getOrDefault(type, 0) + pizzasOnCounterCount.getOrDefault(type, 0);
            
            if (availablePizzas < neededPizzas) {
                return type;
            }
        }
        return null;
    }

    private String getPizzaTypeForOrderId(int orderId) {
        for (Order order : orderManager.getAcceptedOrders()) {
            if (order.getOrderId() == orderId) {
                return order.getType();
            }
        }
        return null;
    }

    private int countOrdersOfType(String pizzaType) {
        int count = 0;
        for (Order order : orderManager.getAcceptedOrders()) {
            if (order.getType().equals(pizzaType)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Collects the money earned.
     * 
     * @return the money earned
     */
    public int collectMoney() {
        int collected = moneyEarned;
        moneyEarned = 0;
        return collected;
    }

    /**
     * Adds money for an order.
     * 
     * @param npc the NPC that placed the order
     */
    public void addMoneyForOrder(NPC npc) {
        // Add money based on NPC VIP status
        int baseAmount = 10;
        int bonus = npc.isVIP() ? baseAmount : 0; // VIP customers pay double
        moneyEarned += baseAmount + bonus;
    }

    /**
     * Gets the pizza queue.
     * 
     * @return the pizza queue
     */
    public Queue<String> getPizzaQueue() {
        return pizzaQueue;
    }

    /**
     * Gets the pizza image for a pizza type.
     * 
     * @param pizzaType the pizza type
     * @return the pizza image
     */
    private BufferedImage getPizzaImage(String pizzaType) {
        return switch (pizzaType) {
            case "Vegetarian" -> vegetarianImage;
            case "Pepperoni" -> pepperoniImage;
            case "Cheese" -> cheeseImage;
            default -> null;
        };
    }

    /**
     * Resets the pizza queue manager.
     */
    public void reset() {
        pizzaQueue.clear();
        pizzasOnCounter.clear();
        moneyEarned = 0;
        Arrays.fill(pizzaSpotOccupied, false);
        orderToSpotMap.clear();
    }

    /**
     * Gets the pizza spot coordinates.
     * 
     * @param spot the spot index
     * @return the pizza spot coordinates
     */
    public int[] getPizzaSpot(int spot) {
        if (spot >= 0 && spot < PIZZA_SPOTS.length) {
            return PIZZA_SPOTS[spot];
        }
        return new int[0];
    }

    /**
     * Sets the store.
     * 
     * @param store the store
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Gets the current pizza in the queue.
     * 
     * @return the current pizza type, or null if none
     */
    public String getCurrentPizza() {
        return pizzaQueue.isEmpty() ? null : pizzaQueue.peek();
    }

    /**
     * Checks if there are pizzas in the queue.
     * 
     * @return true if there are pizzas in the queue, false otherwise
     */
    public boolean hasPizzasInQueue() {
        return !pizzaQueue.isEmpty();
    }

    /**
     * Removes the current pizza from the queue.
     */
    public void removeCurrentPizza() {
        if (!pizzaQueue.isEmpty()) {
            pizzaQueue.poll();
        }
    }
} 