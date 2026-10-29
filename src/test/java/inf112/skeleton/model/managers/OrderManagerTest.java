package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.notifications.OrderPaperNotification;
import inf112.skeleton.model.orders.Order;

import java.util.List;

/**
 * Test suite for the OrderManager class.
 * Tests the order system including:
 * - Creation and handling of orders
 * - Queue management
 * - Popup functionality
 * - Time-based order generation
 */
class OrderManagerTest {
    private OrderManager orderManager;

    @BeforeEach
    void setUp() {
        orderManager = new OrderManager();
    }

    @Nested
    class OrderHandling {
        @Test
        void shouldCreateNewOrder() {
            orderManager.createOrder();
            assertFalse(orderManager.getOrders().isEmpty(), "Order list should not be empty after creation");
            assertEquals(1, orderManager.getOrders().size(), "Should have exactly one order");
        }

        @Test
        void shouldNotExceedMaxOrders() {
            // Create maximum number of orders
            for (int i = 0; i < 4; i++) {
                orderManager.createOrder();
            }
            
            assertTrue(orderManager.isOrderQueueFull(), "Queue should be full");
            assertEquals(3, orderManager.getOrders().size(), "Should not exceed maximum number of orders");
        }

        @Test
        void shouldHandleAcceptedOrder() {
            orderManager.createOrder();
            assertTrue(orderManager.processOrder(true), "Should return true for accepted order");
            assertTrue(orderManager.getOrders().isEmpty(), "Order list should be empty after acceptance");
            assertEquals(1, orderManager.getAcceptedOrders().size(), "Should have one accepted order");
        }

        @Test
        void shouldHandleRejectedOrder() {
            orderManager.createOrder();
            assertFalse(orderManager.processOrder(false), "Should return false for rejected order");
            assertTrue(orderManager.getOrders().isEmpty(), "Order list should be empty after rejection");
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "Should not have accepted orders");
        }

        @Test
        void shouldRemoveAcceptedOrder() {
            orderManager.createOrder();
            orderManager.processOrder(true);
            orderManager.removeFirstAcceptedOrder();
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "List of accepted orders should be empty");
        }
        
        @Test
        void shouldHandleAcceptedSpecificOrder() {
            // Create two orders
            orderManager.createOrder();
            orderManager.createOrder();
            
            // Get the second order
            Order specificOrder = orderManager.getOrders().get(1);
            
            // Accept the specific order
            assertTrue(orderManager.processOrder(true, specificOrder), "Should return true for accepted specific order");
            
            // Verify that only the specific order was removed
            assertEquals(1, orderManager.getOrders().size(), "Should have one order remaining");
            assertEquals(1, orderManager.getAcceptedOrders().size(), "Should have one accepted order");
        }
        
        @Test
        void shouldHandleRejectedSpecificOrder() {
            // Create two orders
            orderManager.createOrder();
            orderManager.createOrder();
            
            // Get the second order
            Order specificOrder = orderManager.getOrders().get(1);
            
            // Reject the specific order
            assertFalse(orderManager.processOrder(false, specificOrder), "Should return false for rejected specific order");
            
            // Verify that only the specific order was removed
            assertEquals(1, orderManager.getOrders().size(), "Should have one order remaining");
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "Should not have accepted orders");
        }
        
        @Test
        void shouldHandleEmptyOrderList() {
            assertFalse(orderManager.processOrder(true), "Should return false when order list is empty");
            assertTrue(orderManager.getOrders().isEmpty(), "Order list should be empty");
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "Should not have accepted orders");
        }
        
        @Test
        void shouldClearAllOrders() {
            // Create some orders
            orderManager.createOrder();
            orderManager.createOrder();
            orderManager.processOrder(true);
            
            // Clear all orders
            orderManager.clearOrders();
            
            // Verify that all lists are empty
            assertTrue(orderManager.getOrders().isEmpty(), "Order list should be empty");
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "List of accepted orders should be empty");
        }
    }

    @Nested
    class PopupHandling {
        @Test
        void shouldTogglePopupVisibility() {
            assertFalse(orderManager.isOrderPopupVisible(), "Popup should be invisible by default");
            
            orderManager.toggleOrderPopup();
            assertTrue(orderManager.isOrderPopupVisible(), "Popup should be visible after toggle");
            
            orderManager.toggleOrderPopup();
            assertFalse(orderManager.isOrderPopupVisible(), "Popup should be invisible after second toggle");
        }
    }

    @Nested
    class Updates {
        @Test
        void shouldUpdateSystem() {
            orderManager.update();
            // Verify that the system can be updated without errors
            assertNotNull(orderManager.getOrders(), "Order list should exist after update");
        }

        @Test
        void shouldHandleNullOrders() {
            assertTrue(orderManager.getOrders().isEmpty(), "Order list should be empty at startup");
            assertTrue(orderManager.getAcceptedOrders().isEmpty(), "List of accepted orders should be empty at startup");
        }
    }
    
    @Nested
    class OrderNotifications {
        @Test
        void shouldAddOrderNotification() {
            // Add an order notification
            orderManager.addOrderNotification(100, 200, 1);
            
            // Get all notifications
            List<OrderPaperNotification> notifications = orderManager.getOrderNotifications();
            
            // Verify that the notification was added
            assertEquals(1, notifications.size(), "Should have one order notification");
            assertEquals(100, notifications.get(0).getX(), "X-coordinate should be correct");
            assertEquals(200, notifications.get(0).getY(), "Y-coordinate should be correct");
            assertEquals(1, notifications.get(0).getOrderId(), "Order ID should be correct");
        }
        
        @Test
        void shouldRemoveOrderNotificationUponAcceptance() {
            // Create an order and a notification
            orderManager.createOrder();
            Order order = orderManager.getOrders().get(0);
            orderManager.addOrderNotification(100, 200, order.getOrderId());
            
            // Accept the order
            orderManager.processOrder(true, order);
            
            // Verify that the notification was removed
            assertTrue(orderManager.getOrderNotifications().isEmpty(), "Order notification should be removed");
        }
        
        @Test
        void shouldRemoveOrderNotificationUponRejection() {
            // Create an order and a notification
            orderManager.createOrder();
            Order order = orderManager.getOrders().get(0);
            orderManager.addOrderNotification(100, 200, order.getOrderId());
            
            // Reject the order
            orderManager.processOrder(false, order);
            
            // Verify that the notification was removed
            assertTrue(orderManager.getOrderNotifications().isEmpty(), "Order notification should be removed");
        }
        
        @Test
        void shouldRemoveOrderNotificationUponRemovalOfAcceptedOrder() {
            // Create an order, accept it and add a notification
            orderManager.createOrder();
            Order order = orderManager.getOrders().get(0);
            orderManager.processOrder(true, order);
            orderManager.addOrderNotification(100, 200, order.getOrderId());
            
            // Remove the accepted order
            orderManager.removeFirstAcceptedOrder();
            
            // Verify that the notification was removed
            assertTrue(orderManager.getOrderNotifications().isEmpty(), "Order notification should be removed");
        }
        
        @Test
        void shouldClearAllOrderNotifications() {
            // Add some notifications
            orderManager.addOrderNotification(100, 200, 1);
            orderManager.addOrderNotification(300, 400, 2);
            
            // Clear all orders (which also clears notifications)
            orderManager.clearOrders();
            
            // Verify that all notifications are removed
            assertTrue(orderManager.getOrderNotifications().isEmpty(), "All order notifications should be removed");
        }
    }
} 