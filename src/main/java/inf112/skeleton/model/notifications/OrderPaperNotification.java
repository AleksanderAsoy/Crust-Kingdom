package inf112.skeleton.model.notifications;

import java.awt.image.BufferedImage;
import java.io.Serial;
import java.io.Serializable;

import inf112.skeleton.view.ui.ImageLoader;

/**
 * Represents a notification for an order paper.
 */

public class OrderPaperNotification implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final int x;
    private final int y;
    private final int orderId;
    private final transient BufferedImage image;
    private boolean isVisible;

    /**
     * Creates a new order paper notification.
     * @param x the x coordinate of the notification
     * @param y the y coordinate of the notification
     * @param orderId the id of the order
     */
    public OrderPaperNotification(int x, int y, int orderId) {
        this.x = x;
        this.y = y;
        this.orderId = orderId;
        this.image = ImageLoader.loadImage("objects/OrderPaper.png");
        this.isVisible = true;
    }

    /**
     * Gets the x coordinate of the notification.
     * @return the x coordinate of the notification
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the notification.
     * @return the y coordinate of the notification
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the id of the order.
     * @return the id of the order
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Gets the image of the notification.
     * @return the image of the notification
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Checks if the notification is visible.
     * @return true if the notification is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Sets the visibility of the notification.
     * @param visible true if the notification should be visible, false otherwise
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }
} 