package inf112.skeleton.model.interfaces.managers;

import inf112.skeleton.model.notifications.OrderPaperNotification;
import inf112.skeleton.model.orders.Order;

import java.util.List;

/**
 * Interface defining the contract for managing orders in the game.
 * Handles order creation, processing, notifications, and queue management.
 */
public interface IOrderManager {
    /**
     * Gets the list of accepted orders.
     * 
     * @return the list of accepted orders
     */
    List<Order> getAcceptedOrders();

    /**
     * Gets the list of all orders.
     * 
     * @return the list of all orders
     */
    List<Order> getOrders();

    /**
     * Creates a new order.
     */
    void createOrder();

    /**
     * Processes an order.
     * 
     * @param accept whether to accept the order
     * @param order the order to process
     * @return true if the order was processed successfully, false otherwise
     */
    boolean processOrder(boolean accept, Order order);

    /**
     * Updates the order manager state.
     */
    void update();

    /**
     * Clears all orders.
     */
    void clearOrders();

    /**
     * Checks if the order queue is full.
     * 
     * @return true if the queue is full, false otherwise
     */
    boolean isOrderQueueFull();

    /**
     * Checks if the order popup is visible.
     * 
     * @return true if the popup is visible, false otherwise
     */
    boolean isOrderPopupVisible();

    /**
     * Toggles the order popup visibility.
     */
    void toggleOrderPopup();

    /**
     * Adds a new order paper notification at the specified coordinates.
     * 
     * @param x x-coordinate for the notification
     * @param y y-coordinate for the notification
     * @param orderId ID of the order the notification is for
     */
    void addOrderNotification(int x, int y, int orderId);

    /**
     * Gets all active order paper notifications.
     * 
     * @return list of active order notifications
     */
    List<OrderPaperNotification> getOrderNotifications();
} 