package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.PizzaStore;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.orders.Order;

/**
 * Test suite for the PizzaQueueManager class.
 * Tests the pizza queue management, counter operations, and related features.
 */
class PizzaQueueManagerTest {
    private PizzaQueueManager pizzaQueueManager;
    
    @Mock
    private OrderManager orderManager;
    
    @Mock
    private Store store;
    
    @Mock
    private PizzaStore pizzaStore;

    @Mock
    private Order order1;
    
    @Mock
    private Order order2;
    
    @Mock
    private NPC npc;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up the pizza queue manager
        pizzaQueueManager = new PizzaQueueManager(orderManager);
        
        // Set up the store
        when(store.getPizzaStore()).thenReturn(pizzaStore);
        pizzaQueueManager.setStore(store);
        
        // Set up the orders
        when(order1.getType()).thenReturn("Vegetarian");
        when(order1.getOrderId()).thenReturn(1);
        when(order2.getType()).thenReturn("Pepperoni");
        when(order2.getOrderId()).thenReturn(2);
        
        // Set up the NPC
        when(npc.isVIP()).thenReturn(false);
        
        // Set up the pizza store
        when(pizzaStore.usePizza(anyString())).thenReturn(true);
        
        // Set up the accepted orders
        List<Order> acceptedOrders = new ArrayList<>();
        acceptedOrders.add(order1);
        acceptedOrders.add(order2);
        when(orderManager.getAcceptedOrders()).thenReturn(acceptedOrders);
    }
    
    @Test
    void shouldInitializeCorrectly() {
        // Assert
        assertNotNull(pizzaQueueManager, "PizzaQueueManager should be initialized");
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), "Counter should be empty initially");
        assertFalse(pizzaQueueManager.hasPizzasInQueue(), "Queue should be empty initially");
        assertEquals(0, pizzaQueueManager.collectMoney(), "Money earned should be 0 initially");
    }
    
    @Test
    void shouldAssignOrderToSpot() {
        // Act
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        
        // Assert
        int[] spot = pizzaQueueManager.getPizzaSpot(0);
        assertNotNull(spot, "Spot should be assigned");
        assertEquals(4 * 48, spot[0], "X coordinate should be correct");
        assertEquals(3 * 48, spot[1], "Y coordinate should be correct");
    }
    
    @Test
    void shouldNotAssignOrderToInvalidSpot() {
        // Act
        pizzaQueueManager.assignOrderToSpot(order1, 5); // Invalid spot
        
        // Assert
        int[] spot = pizzaQueueManager.getPizzaSpot(5);
        assertEquals(0, spot.length, "Invalid spot should return empty array");
    }
    
    @Test
    void shouldPlacePizzaAtSpot() {
        // Arrange
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        
        // Act
        pizzaQueueManager.placePizzaAtSpot(order1);
        
        // Assert
        List<PizzaOnCounter> pizzas = pizzaQueueManager.getPizzasOnCounter();
        assertEquals(1, pizzas.size(), "Should have one pizza on counter");
        PizzaOnCounter pizza = pizzas.get(0);
        assertEquals(1, pizza.getOrderId(), "Pizza should be for order1");
        assertEquals(4 * 48, pizza.getX(), "X coordinate should be correct");
        assertEquals(3 * 48, pizza.getY(), "Y coordinate should be correct");
    }
    
    @Test
    void shouldNotPlacePizzaAtSpotIfNoSpotAssigned() {
        // Act
        pizzaQueueManager.placePizzaAtSpot(order1);
        
        // Assert
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), "Should not place pizza if no spot assigned");
    }
    
    @Test
    void shouldNotPlacePizzaAtSpotIfCounterFull() {
        // Arrange
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        pizzaQueueManager.assignOrderToSpot(order2, 1);
        
        // Create a third order and assign it to spot 2
        Order order3 = mock(Order.class);
        when(order3.getType()).thenReturn("Cheese");
        when(order3.getOrderId()).thenReturn(3);
        pizzaQueueManager.assignOrderToSpot(order3, 2);
        
        // Place pizzas for order1 and order2
        pizzaQueueManager.placePizzaAtSpot(order1);
        pizzaQueueManager.placePizzaAtSpot(order2);
        
        // Create a fourth order
        Order order4 = mock(Order.class);
        when(order4.getType()).thenReturn("Vegetarian");
        when(order4.getOrderId()).thenReturn(4);
        pizzaQueueManager.assignOrderToSpot(order4, 0); // Try to assign to spot 0 again
        
        // Act
        pizzaQueueManager.placePizzaAtSpot(order4);
        
        // Assert
        List<PizzaOnCounter> pizzas = pizzaQueueManager.getPizzasOnCounter();
        assertEquals(3, pizzas.size(), "Should have three pizzas on counter (maximum allowed)");
    }
    
    @Test
    void shouldRemovePizzaByOrderId() {
        // Arrange
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        pizzaQueueManager.placePizzaAtSpot(order1);
        assertEquals(1, pizzaQueueManager.getPizzasOnCounter().size(), "Should have one pizza on counter");
        
        // Act
        pizzaQueueManager.removePizzaByOrderId(1);
        
        // Assert
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), "Pizza should be removed");
    }
    
    @Test
    void shouldAddPizzaToQueue() {
        // Act
        pizzaQueueManager.addPizzaToQueue();
        
        // Assert
        assertTrue(pizzaQueueManager.hasPizzasInQueue(), "Should have pizzas in queue");
        assertEquals("Vegetarian", pizzaQueueManager.getCurrentPizza(), "First pizza should be Vegetarian");
    }
    
    @Test
    void shouldNotAddPizzaToQueueIfNoPizzasInInventory() {
        // Arrange
        when(pizzaStore.usePizza(anyString())).thenReturn(false);
        
        // Act
        pizzaQueueManager.addPizzaToQueue();
        
        // Assert
        assertFalse(pizzaQueueManager.hasPizzasInQueue(), "Should not have pizzas in queue");
    }
    
    @Test
    void shouldCollectMoney() {
        // Arrange
        pizzaQueueManager.addMoneyForOrder(npc);
        
        // Act
        int collected = pizzaQueueManager.collectMoney();
        
        // Assert
        assertEquals(10, collected, "Should collect 10 money for regular order");
        assertEquals(0, pizzaQueueManager.collectMoney(), "Money should be reset after collection");
    }
    
    @Test
    void shouldAddMoneyForVIPOrder() {
        // Arrange
        when(npc.isVIP()).thenReturn(true);
        
        // Act
        pizzaQueueManager.addMoneyForOrder(npc);
        int collected = pizzaQueueManager.collectMoney();
        
        // Assert
        assertEquals(20, collected, "Should collect 20 money for VIP order");
    }
    
    @Test
    void shouldRemoveCurrentPizza() {
        // Arrange
        pizzaQueueManager.addPizzaToQueue();
        assertTrue(pizzaQueueManager.hasPizzasInQueue(), "Should have pizzas in queue");
        
        // Act
        pizzaQueueManager.removeCurrentPizza();
        
        // Assert
        assertFalse(pizzaQueueManager.hasPizzasInQueue(), "Should not have pizzas in queue after removal");
    }
    
    @Test
    void shouldResetCorrectly() {
        // Arrange
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        pizzaQueueManager.placePizzaAtSpot(order1);
        pizzaQueueManager.addPizzaToQueue();
        pizzaQueueManager.addMoneyForOrder(npc);
        
        // Act
        pizzaQueueManager.reset();
        
        // Assert
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), "Counter should be empty after reset");
        assertFalse(pizzaQueueManager.hasPizzasInQueue(), "Queue should be empty after reset");
        assertEquals(0, pizzaQueueManager.collectMoney(), "Money should be 0 after reset");
    }

    @Test
    void shouldGetPizzaQueue() {
        // Act
        Queue<String> queue = pizzaQueueManager.getPizzaQueue();
        
        // Assert
        assertNotNull(queue, "Queue should not be null");
        assertTrue(queue.isEmpty(), "Queue should be empty initially");
    }

    @Test
    void shouldRemoveOrderFromSpot() {
        // Arrange
        pizzaQueueManager.assignOrderToSpot(order1, 0);
        
        // Act
        pizzaQueueManager.removeOrderFromSpot(order1);
        
        // Assert
        int[] spot = pizzaQueueManager.getPizzaSpot(0);
        assertNotNull(spot, "Spot should still exist");
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), "Pizza should be removed from counter");
    }

    @Test
    void shouldHandleInvalidSpotAssignment() {
        // Arrange
        Order order = mock(Order.class);
        when(order.getOrderId()).thenReturn(1);
        when(order.getType()).thenReturn("Vegetarian");
        
        // Act
        pizzaQueueManager.assignOrderToSpot(order, Integer.MAX_VALUE);
        pizzaQueueManager.placePizzaAtSpot(order);
        
        // Assert
        assertTrue(pizzaQueueManager.getPizzasOnCounter().isEmpty(), 
            "No pizza should be placed when spot is invalid");
    }

    @Test
    void shouldHandleFullCounter() {
        // Arrange - Fill up the counter with pizzas
        for (int i = 0; i < 3; i++) {
            Order order = mock(Order.class);
            when(order.getOrderId()).thenReturn(i);
            when(order.getType()).thenReturn("Vegetarian");
            pizzaQueueManager.assignOrderToSpot(order, i);
            pizzaQueueManager.placePizzaAtSpot(order);
        }
        
        // Try adding an extra pizza
        Order extraOrder = mock(Order.class);
        when(extraOrder.getOrderId()).thenReturn(99);
        when(extraOrder.getType()).thenReturn("Vegetarian");
        pizzaQueueManager.assignOrderToSpot(extraOrder, 0);
        
        // Act
        int initialSize = pizzaQueueManager.getPizzasOnCounter().size();
        pizzaQueueManager.placePizzaAtSpot(extraOrder);
        
        // Assert
        assertEquals(initialSize, pizzaQueueManager.getPizzasOnCounter().size(), 
            "The amount of pizzas on the counter should not change when it is full");
    }
}