package inf112.skeleton.model.entities;

import java.awt.image.BufferedImage;

public class PizzaOnCounter {
    private final int x;
    private final int y;
    private final BufferedImage image;
    private long placedTime;
    private static final long REMOVE_AFTER = 3_000; // 3 seconds
    private final int orderId; // New field for linking pizza to order

    /**
     * Constructor for PizzaOnCounter.
     * @param x the x coordinate of the pizza
     * @param y the y coordinate of the pizza
     * @param image the image of the pizza
     * @param orderId the id of the order
     */
    public PizzaOnCounter(int x, int y, BufferedImage image, int orderId) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.placedTime = System.currentTimeMillis();
        this.orderId = orderId;
    }

    /**
     * Gets the x coordinate of the pizza.
     * @return the x coordinate of the pizza
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the pizza.
     * @return the y coordinate of the pizza
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the image of the pizza.
     * @return the image of the pizza
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Checks if the pizza is finished.
     * @return true if the pizza is finished, false otherwise
     */
    public boolean isFinished() {
        return System.currentTimeMillis() - placedTime >= REMOVE_AFTER;
    }

    /**
     * Simulates the timeout of the pizza.
     */
    public void simulateTimeout() {
        placedTime = System.currentTimeMillis() - REMOVE_AFTER;  // Set placedTime to simulate the timeout
    }

    /**
     * Gets the order id of the pizza.
     * @return the order id of the pizza
     */
    public int getOrderId() {
        return orderId;
    }

}
