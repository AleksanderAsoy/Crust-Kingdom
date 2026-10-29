package inf112.skeleton.model.orders;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a customer order in the game.
 * Each order has a type, unique ID, and expiration time.
 */
public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String type;
    private final int orderId;  // Unique identifier for each order
    private static int nextOrderId = 0;  // Static counter for generating unique IDs
    private final long creationTime;  // When the order was created
    private static final long ORDER_TIMEOUT = 10_000;  // 10 seconds timeout

    /**
     * Creates a new order with the specified type.
     * 
     * @param type The type of pizza ordered
     */
    public Order(String type) {
        this.type = type;
        this.orderId = nextOrderId++;
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Gets the type of pizza in this order.
     * 
     * @return The pizza type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the unique identifier for this order.
     * 
     * @return The order ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Checks if this order has expired.
     * 
     * @return true if the order has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - creationTime >= ORDER_TIMEOUT;
    }

    /**
     * Gets the remaining time before this order expires.
     * 
     * @return The remaining time in milliseconds
     */
    public long getRemainingTime() {
        long elapsedTime = System.currentTimeMillis() - creationTime;
        return Math.max(0, ORDER_TIMEOUT - elapsedTime);
    }
}
