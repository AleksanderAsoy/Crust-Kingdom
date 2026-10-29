package inf112.skeleton.model.notifications;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.Serializable;

class OrderPaperNotificationTest {
    private OrderPaperNotification notification;
    private static final int TEST_X = 10;
    private static final int TEST_Y = 20;
    private static final int TEST_ORDER_ID = 1;

    @BeforeEach
    void setUp() {
        notification = new OrderPaperNotification(TEST_X, TEST_Y, TEST_ORDER_ID);
    }

    @Test
    void testConstructor() {
        assertNotNull(notification);
        assertEquals(TEST_X, notification.getX());
        assertEquals(TEST_Y, notification.getY());
        assertEquals(TEST_ORDER_ID, notification.getOrderId());
        assertTrue(notification.isVisible());
    }

    @Test
    void testGetX() {
        assertEquals(TEST_X, notification.getX());
    }

    @Test
    void testGetY() {
        assertEquals(TEST_Y, notification.getY());
    }

    @Test
    void testGetOrderId() {
        assertEquals(TEST_ORDER_ID, notification.getOrderId());
    }

    @Test
    void testGetImage() {
        BufferedImage image = notification.getImage();
        assertNotNull(image);
    }

    @Test
    void testIsVisible() {
        assertTrue(notification.isVisible());
    }

    @Test
    void testSetVisible() {
        notification.setVisible(false);
        assertFalse(notification.isVisible());
        
        notification.setVisible(true);
        assertTrue(notification.isVisible());
    }

    @Test
    void testSerialization() {
        // Test that the class implements Serializable
        assertInstanceOf(Serializable.class, notification);
    }
} 