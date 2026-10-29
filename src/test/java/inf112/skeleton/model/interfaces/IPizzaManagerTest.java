package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.interfaces.managers.IPizzaManager;
import inf112.skeleton.model.managers.KarmaManager;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SoundManager;
import inf112.skeleton.model.orders.Order;

class IPizzaManagerTest {
    private IPizzaManager pizzaManager;
    private NPC npc;
    private Order order;

    @BeforeEach
    void setUp() {
        // Reset singleton instance
        PizzaManager.resetInstance();

        OrderManager orderManager = new OrderManager();
        Store store = new Store();
        SoundManager sound = new SoundManager();
        KarmaManager karmaManager = new KarmaManager();
        pizzaManager = PizzaManager.getInstance(orderManager);
        npc = mock(NPC.class);
        order = mock(Order.class);
        
        // Set up test data
        when(order.getType()).thenReturn("Vegetarian");
        when(order.getOrderId()).thenReturn(1);
        store.getPizzaStore().purchasePizza("Vegetarian", mock(Player.class));
        
        // Add and accept the order
        orderManager.getOrders().add(order);
        orderManager.processOrder(true, order);
        
        pizzaManager.setStore(store);
        pizzaManager.setSound(sound);
        pizzaManager.setKarmaManager(karmaManager);
    }

    @Test
    void shouldHandleCookingOperations() {
        // Test cooking state
        assertFalse(pizzaManager.isCooking(), "Should not be cooking initially");
        
        pizzaManager.startCooking();
        assertTrue(pizzaManager.isCooking(), "Should be cooking after start");
        
        // Test cooking progress
        double progress = pizzaManager.getCookingProgress();
        assertTrue(progress >= 0.0 && progress <= 1.0, "Progress should be between 0 and 1");
    }

    @Test
    void shouldHandlePizzaQueueOperations() {
        // Test queue operations
        assertFalse(pizzaManager.hasPizzasInQueue(), "Queue should be empty initially");
        
        pizzaManager.addPizzaToQueue();
        assertTrue(pizzaManager.hasPizzasInQueue(), "Queue should have pizzas after adding");
    }

    @Test
    void shouldHandleOrderSpotOperations() {
        // Test spot operations
        int[] spot = pizzaManager.getPizzaSpot(0);
        assertNotNull(spot, "Should return valid spot coordinates");
        assertEquals(2, spot.length, "Spot coordinates should have x and y values");
        
        pizzaManager.assignOrderToSpot(order, 0);
        pizzaManager.placePizzaAtSpot(order);
        pizzaManager.removeOrderFromSpot(order);
    }

    @Test
    void shouldHandleMoneyOperations() {
        // Test money operations
        pizzaManager.addMoneyForOrder(npc);
        int money = pizzaManager.collectMoney();
        assertTrue(money >= 0, "Money should be non-negative");
    }

    @Test
    void shouldHandleMinigameOperations() {
        // Test minigame operations
        assertFalse(pizzaManager.isBakingMinigameActive(), "Minigame should not be active initially");
        
        pizzaManager.initiateBakingMinigame();
        assertTrue(pizzaManager.isBakingMinigameActive(), "Minigame should be active after initiation");
        
        double progress = pizzaManager.getMinigameProgress();
        assertTrue(progress >= 0.0 && progress <= 1.0, "Minigame progress should be between 0 and 1");
        
        pizzaManager.attemptMinigameHit();
    }

    @Test
    void shouldHandleResetOperations() {
        // Test reset operations
        pizzaManager.reset();
        assertFalse(pizzaManager.isCooking(), "Should not be cooking after reset");
        assertFalse(pizzaManager.isBakingMinigameActive(), "Minigame should not be active after reset");
    }
} 