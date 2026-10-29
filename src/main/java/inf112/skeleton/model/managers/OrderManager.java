package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.notifications.OrderPaperNotification;
import inf112.skeleton.model.orders.Order;

import java.util.HashMap;

/**
 * Handles orders in the game.
 * Uses SecureRandom to generate random orders in a secure way.
 */
public class OrderManager implements Serializable, IOrderManager {
    @Serial
    private static final long serialVersionUID = 1L;
    private final List<Order> orders;
    private final List<Order> acceptedOrders;
    private static final int MAX_ORDERS = 3; // Maximum number of orders at the same time
    private boolean showOrders = false; // Controls if the popup window should be shown
    private final List<String> pizzaTypes = Arrays.asList(
            "Pepperoni",
            "Cheese",
            "Vegetarian");
    private final Map<Integer, OrderPaperNotification> orderNotifications;

    // Uses SecureRandom instead of Random for better randomness and security
    private final transient SecureRandom secureRandom;

    /**
     * Creates a new OrderManager.
     * Initializes SecureRandom for secure generation of random orders.
     */
    public OrderManager() {
        this.orders = new ArrayList<>();
        this.acceptedOrders = new ArrayList<>();
        this.secureRandom = new SecureRandom();
        this.orderNotifications = new HashMap<>();
        // SecureRandom seeds itself on a secure way
    }

    /**
     * Processes an order.
     * 
     * @param accept if the order should be accepted
     * @param order specific order to process (null for first order in queue)
     * @return true if the order was accepted, false otherwise
     */
    public boolean processOrder(boolean accept, Order order) {
        if (orders.isEmpty()) {
            return false;
        }

        // If no specific order is provided, use the first one in queue
        if (order == null) {
            order = orders.remove(0);
        } else {
            orders.remove(order);
        }

        // Remove the notification for this order
        orderNotifications.remove(order.getOrderId());

        if (accept && !order.isExpired()) {
            acceptedOrders.add(order);
            return true;
        }
        return false;
    }

    /**
     * Processes the first order in the queue.
     * 
     * @param accept if the order should be accepted
     * @return true if the order was accepted, false otherwise
     */
    public boolean processOrder(boolean accept) {
        return processOrder(accept, null);
    }

    /**
     * Updates the order system.
     */
    public void update() {
        // Check for expired orders
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.isExpired()) {
                // Remove notification for expired order
                orderNotifications.remove(order.getOrderId());
                iterator.remove();
            }
        }
    }

    /**
     * Returns a list of pending orders.
     * 
     * @return list of pending orders
     */
    public List<Order> getOrders() {
        return orders;
    }

    /**
     * Returns a list of accepted orders.
     * 
     * @return list of accepted orders
     */
    public List<Order> getAcceptedOrders() {
        return acceptedOrders;
    }

    /**
     * Removes the first accepted order from the queue.
     */
    public void removeFirstAcceptedOrder() {
        if (!acceptedOrders.isEmpty()) {
            Order order = acceptedOrders.remove(0);
            // Remove notification for the completed order
            orderNotifications.remove(order.getOrderId());
        }
    }

    /**
     * Shows or hides the popup window.
     */
    public void toggleOrderPopup() {
        showOrders = !showOrders;
    }

    /**
     * Checks if the popup window is visible.
     * 
     * @return true if the popup window is visible, false otherwise
     */
    public boolean isOrderPopupVisible() {
        return showOrders;
    }

    /**
     * Checks if the order queue is full.
     * 
     * @return true if the order queue is full, false otherwise
     */
    public boolean isOrderQueueFull() {
        return orders.size() >= MAX_ORDERS;
    }

    /**
     * Creates a new random order.
     * Uses SecureRandom to choose a random pizza type.
     */
    public void createOrder() {
        if (!isOrderQueueFull()) {
            // Bruker SecureRandom.nextInt() to choose a random pizza
            String randomPizza = pizzaTypes.get(secureRandom.nextInt(pizzaTypes.size()));
            Order order = new Order(randomPizza);
            orders.add(order);
        }
    }

    public void clearOrders() {
        // Clear the lists
        orders.clear();
        acceptedOrders.clear();
        orderNotifications.clear();
    }

    /**
     * Adds a new order paper notification at the specified coordinates
     */
    public void addOrderNotification(int x, int y, int orderId) {
        orderNotifications.put(orderId, new OrderPaperNotification(x, y, orderId));
    }

    /**
     * Returns all active order paper notifications
     */
    public List<OrderPaperNotification> getOrderNotifications() {
        return new ArrayList<>(orderNotifications.values());
    }
}
