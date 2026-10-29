package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaStore;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.orders.Order;

/**
 * Test suite for the PizzaManager class.
 * Tests focus on the facade pattern and delegation to CookingManager and PizzaQueueManager.
 */
class PizzaManagerTest {
    private PizzaManager pizzaManager;
    private OrderManager orderManager;
    @Mock
    private KarmaManager karmaManager;
    @Mock
    private CookingManager cookingManager;
    @Mock
    private PizzaQueueManager queueManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        PizzaManager.resetInstance();
        orderManager = new OrderManager();
        Store store = new Store();
        pizzaManager = new PizzaManager(orderManager);
        
        // Inject mocked dependencies using reflection
        try {
            Field cookingManagerField = PizzaManager.class.getDeclaredField("cookingManager");
            cookingManagerField.setAccessible(true);
            cookingManagerField.set(pizzaManager, cookingManager);
            
            Field queueManagerField = PizzaManager.class.getDeclaredField("queueManager");
            queueManagerField.setAccessible(true);
            queueManagerField.set(pizzaManager, queueManager);
        } catch (Exception e) {
            fail("Failed to inject mocked dependencies: " + e.getMessage());
        }
        
        pizzaManager.setStore(store);
        pizzaManager.setKarmaManager(karmaManager);
        
        // Add initial inventory to the store for test pizzas
        PizzaStore pizzaStore = store.getPizzaStore();
        // Directly manipulate the inventory map for testing purposes
        Field inventoryField;
        try {
            inventoryField = PizzaStore.class.getDeclaredField("pizzaInventory");
            inventoryField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<String, Integer> inventoryMap =
                (java.util.Map<String, Integer>) inventoryField.get(pizzaStore);

            // Add/Set inventory counts needed for tests
            inventoryMap.put("Test", 10);
            inventoryMap.put("Pepperoni", 10);
            inventoryMap.put("Vegetarian", 10);
            inventoryMap.put("Cheese", 10);

        } catch (NoSuchFieldException | IllegalAccessException e) {
             fail("Failed to access pizzaInventory via reflection: " + e.getMessage());
        }

        // Set up default mock behavior
        when(cookingManager.isCooking()).thenReturn(false);
        when(cookingManager.isReadyToTakeOut()).thenReturn(false);
        when(cookingManager.isBakingMinigameActive()).thenReturn(false);
        when(queueManager.hasPizzasInQueue()).thenReturn(false);
        when(queueManager.getPizzasOnCounter()).thenReturn(new java.util.ArrayList<>());
    }

    @Test
    void shouldDelegateStartCookingToCookingManager() {
        // Act
        pizzaManager.startCooking();

        // Assert - verify that cooking state is delegated correctly
        verify(cookingManager).startCooking();
    }

    @Test
    void shouldDelegateQueueOperationsToPizzaQueueManager() {
        // Arrange
        Order testOrder = new Order("Pepperoni");
        orderManager.getAcceptedOrders().add(testOrder);
        
        // Act
        pizzaManager.assignOrderToSpot(testOrder, 0);
        pizzaManager.addPizzaToQueue();

        // Assert
        verify(queueManager).assignOrderToSpot(testOrder, 0);
        verify(queueManager).addPizzaToQueue();
    }

    @Test
    void shouldHandleSingletonPattern() {
        // Arrange
        PizzaManager instance1 = PizzaManager.getInstance(orderManager);
        PizzaManager instance2 = PizzaManager.getInstance(orderManager);
        
        // Assert
        assertSame(instance1, instance2, "Should return the same instance");
        
        // Reset for other tests
        PizzaManager.resetInstance();
    }

    @Test
    void shouldDelegateMinigameOperationsToCookingManager() {
        // Act & Assert
        pizzaManager.initiateBakingMinigame();
        verify(cookingManager).initiateBakingMinigame();
        
        pizzaManager.attemptMinigameHit();
        verify(cookingManager).attemptMinigameHit();
    }

    @Test
    void shouldDelegateMoneyOperationsToPizzaQueueManager() {
        // Arrange
        NPC testNpc = new NPC(orderManager, 0, 0, "TestNPC", null);
        when(queueManager.collectMoney()).thenReturn(10);

        // Act
        pizzaManager.addMoneyForOrder(testNpc);
        int money = pizzaManager.collectMoney();

        // Assert
        verify(queueManager).addMoneyForOrder(testNpc);
        verify(queueManager).collectMoney();
        assertEquals(10, money, "Should delegate money operations to PizzaQueueManager");
    }

    @Test
    void shouldDelegateSpotManagementToPizzaQueueManager() {
        // Arrange
        Order testOrder = new Order("Pepperoni");
        int[] expectedSpot = new int[]{100, 200};
        when(queueManager.getPizzaSpot(0)).thenReturn(expectedSpot);

        // Act
        pizzaManager.assignOrderToSpot(testOrder, 0);
        int[] spot = pizzaManager.getPizzaSpot(0);

        // Assert
        verify(queueManager).assignOrderToSpot(testOrder, 0);
        verify(queueManager).getPizzaSpot(0);
        assertArrayEquals(expectedSpot, spot, "Should delegate spot management to PizzaQueueManager");
    }

    @Test
    void shouldDelegateAdditionalQueueOperationsToPizzaQueueManager() {
        // Arrange
        Order testOrder = new Order("Pepperoni");
        Queue<String> expectedQueue = new LinkedList<>();
        when(queueManager.getPizzaQueue()).thenReturn(expectedQueue);

        // Act & Assert
        // Test getPizzaQueue
        assertSame(expectedQueue, pizzaManager.getPizzaQueue(), "Should delegate getPizzaQueue to PizzaQueueManager");
        verify(queueManager).getPizzaQueue();

        // Test removeOrderFromSpot
        pizzaManager.removeOrderFromSpot(testOrder);
        verify(queueManager).removeOrderFromSpot(testOrder);

        // Test placePizzaAtSpot
        pizzaManager.placePizzaAtSpot(testOrder);
        verify(queueManager).placePizzaAtSpot(testOrder);
    }

    @Test
    void shouldHandleTakeOutPizzaWhenReady() {
        // Arrange
        Order testOrder = new Order("Pepperoni");
        orderManager.getAcceptedOrders().add(testOrder);
        when(cookingManager.isReadyToTakeOut()).thenReturn(true);

        // Act
        pizzaManager.takeOutPizza();

        // Assert
        verify(cookingManager).takeOutPizza();
        verify(queueManager).placePizzaAtSpot(testOrder);
        verify(queueManager).removeCurrentPizza();
    }

    @Test
    void shouldNotTakeOutPizzaWhenNotReady() {
        // Arrange
        when(cookingManager.isReadyToTakeOut()).thenReturn(false);

        // Act
        pizzaManager.takeOutPizza();

        // Assert
        verify(cookingManager, never()).takeOutPizza();
        verify(queueManager, never()).placePizzaAtSpot(any());
        verify(queueManager, never()).removeCurrentPizza();
    }

    @Test
    void shouldDelegateCookingProgress() {
        // Arrange
        double expectedProgress = 0.75;
        when(cookingManager.getCookingProgress()).thenReturn(expectedProgress);

        // Act
        double progress = pizzaManager.getCookingProgress();

        // Assert
        verify(cookingManager).getCookingProgress();
        assertEquals(expectedProgress, progress, "Should return cooking progress from CookingManager");
    }

    @Test
    void shouldDelegateTimeRelatedMethods() {
        // Arrange
        long expectedCookTime = 1000L;
        long expectedStartTime = 5000L;
        when(cookingManager.getCookTime()).thenReturn(expectedCookTime);
        when(cookingManager.getStartTime()).thenReturn(expectedStartTime);

        // Act & Assert
        assertEquals(expectedCookTime, pizzaManager.getCookTime(), "Should return cook time from CookingManager");
        assertEquals(expectedStartTime, pizzaManager.getStartTime(), "Should return start time from CookingManager");
        verify(cookingManager).getCookTime();
        verify(cookingManager).getStartTime();
    }

    @Test
    void shouldDelegateCookingSpeedMultiplier() {
        // Arrange
        double multiplier = 1.5;

        // Act
        pizzaManager.setCookingSpeedMultiplier(multiplier);

        // Assert
        verify(cookingManager).setCookingSpeedMultiplier(multiplier);
    }

    @Test
    void shouldResetState() {
        // Act
        pizzaManager.reset();

        // Assert
        verify(cookingManager).reset();
        verify(queueManager).reset();
    }

    @Test
    void shouldDelegateMinigameTimeMethods() {
        // Arrange
        long expectedStartTime = 1000L;
        when(cookingManager.getMinigameStartTime()).thenReturn(expectedStartTime);

        // Act & Assert
        assertEquals(expectedStartTime, pizzaManager.getMinigameStartTime(), 
            "Should return minigame start time from CookingManager");
        verify(cookingManager).getMinigameStartTime();
    }

    @Test
    void shouldDelegateMinigameProgressMethods() {
        // Arrange
        double expectedProgress = 0.5;
        double expectedSuccessStart = 0.3;
        double expectedSuccessEnd = 0.7;
        when(cookingManager.getMinigameProgress()).thenReturn(expectedProgress);
        when(cookingManager.getMinigameSuccessStart()).thenReturn(expectedSuccessStart);
        when(cookingManager.getMinigameSuccessEnd()).thenReturn(expectedSuccessEnd);

        // Act & Assert
        assertEquals(expectedProgress, pizzaManager.getMinigameProgress(), 
            "Should return minigame progress from CookingManager");
        assertEquals(expectedSuccessStart, pizzaManager.getMinigameSuccessStart(), 
            "Should return minigame success start from CookingManager");
        assertEquals(expectedSuccessEnd, pizzaManager.getMinigameSuccessEnd(), 
            "Should return minigame success end from CookingManager");
        verify(cookingManager).getMinigameProgress();
        verify(cookingManager).getMinigameSuccessStart();
        verify(cookingManager).getMinigameSuccessEnd();
    }
} 