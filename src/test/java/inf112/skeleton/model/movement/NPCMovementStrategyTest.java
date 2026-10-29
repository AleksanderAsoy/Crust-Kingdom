package inf112.skeleton.model.movement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SpotManager;
import inf112.skeleton.model.orders.Order;

/**
 * Test suite for NPCMovementStrategy class.
 * Tests movement logic for NPCs, including:
 * - Initialization
 * - Movement from start to counter
 * - Movement from counter back to start
 * - Direction changes
 */
class NPCMovementStrategyTest {

    // Constants
    private static final int START_X = 100;
    private static final int START_Y = 100;
    private static final int ORDER_ID = 1;
    private static final int SPEED = 2;

    // --- Mocks ---
    @Mock private NPC npc;
    @Mock private Order order;
    @Mock private OrderManager orderManager;
    @Mock private SpotManager spotManager;
    @Mock private PizzaManager pizzaManager;
    @Mock private PizzaOnCounter pizzaOnCounter;
    @Mock private GameLogic gameLogic;

    private MockedStatic<SpotManager> spotManagerMockedStatic;
    private MockedStatic<PizzaManager> pizzaManagerMockedStatic;
    private NPCMovementStrategy strategy;

    // Helpers
    private void setCurrentState(NPCMovementStrategy.NPCState state) throws Exception {
        Field stateField = NPCMovementStrategy.class.getDeclaredField("currentState");
        stateField.setAccessible(true);
        stateField.set(strategy, state);
    }

    private void setAssignedSpot(int spotId) throws Exception {
        Field spotField = NPCMovementStrategy.class.getDeclaredField("assignedSpot");
        spotField.setAccessible(true);
        spotField.set(strategy, spotId);
    }

    // --- Setup & Teardown ---
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spotManagerMockedStatic = mockStatic(SpotManager.class);
        pizzaManagerMockedStatic = mockStatic(PizzaManager.class);
        
        spotManagerMockedStatic.when(SpotManager::getInstance).thenReturn(spotManager);
        pizzaManagerMockedStatic.when(() -> PizzaManager.getInstance(any())).thenReturn(pizzaManager);

        when(npc.getSpeed()).thenReturn(SPEED);
        when(npc.getOrder()).thenReturn(order);
        when(order.getOrderId()).thenReturn(ORDER_ID);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(new int[]{150, 150});
        when(pizzaOnCounter.getOrderId()).thenReturn(ORDER_ID);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(List.of(pizzaOnCounter));

        strategy = new NPCMovementStrategy(npc, orderManager, START_X, START_Y, gameLogic);
    }

    @AfterEach
    void tearDown() {
        if (pizzaManagerMockedStatic != null) {
            pizzaManagerMockedStatic.close();
        }
        if (spotManagerMockedStatic != null) {
            spotManagerMockedStatic.close();
        }
    }

    // --- Test Cases ---

    @Test
    void resetNPC_shouldResetNPCState() throws Exception {
        // Setup initial position and assigned spot
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(100);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        
        // Set assigned spot
        setAssignedSpot(1);
        
        // Reset all previous interactions
        clearInvocations(npc);
        clearInvocations(spotManager);
        
        // Execute
        strategy.resetNPC();
        
        // Verify
        verify(npc, times(1)).setX(START_X);
        verify(npc, times(1)).setY(START_Y);
        verify(npc, times(1)).setSpeed(2);
        verify(npc, times(1)).setVisible(true);
        verify(spotManager).releaseSpot(1);
    }

    @Test
    void update_shouldWaitAtCounterWhenOrderPlaced() throws Exception {
        // Arrange
        Order newOrder = mock(Order.class);
        when(newOrder.getOrderId()).thenReturn(ORDER_ID);
        when(orderManager.isOrderQueueFull()).thenReturn(false);
        when(orderManager.getOrders()).thenReturn(List.of(newOrder));
        when(npc.getOrder()).thenReturn(null);
        when(npc.getSkin()).thenReturn("RegularNPC");
        when(pizzaManager.getPizzaSpot(1)).thenReturn(new int[] {160, 160});

        setAssignedSpot(1);
        setCurrentState(NPCMovementStrategy.NPCState.PLACE_ORDER);

        // Act
        strategy.update();

        // Assert
        verify(orderManager).createOrder();
        verify(npc).setOrder(newOrder);
        verify(npc).setWaiting(true);
        verify(npc).setDirection("waiting_at_counter");
        verify(pizzaManager).assignOrderToSpot(newOrder, 1);
        verify(orderManager).addOrderNotification(160, 160, ORDER_ID);
    }

    @Test
    void update_shouldMoveToExitWhenOrderRejected() {
        // Setup
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        
        // Execute
        strategy.rejectOrder();

        // Verify movement towards exit
        verify(npc).setWaiting(false);
        verify(npc).setDirection("down");
        verify(npc).setOrder(null);
    }

    @Test
    void update_shouldMoveTowardsCounterWhenEntering() {
        // Setup
        when(npc.getX()).thenReturn(START_X);
        when(npc.getY()).thenReturn(START_Y);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(new int[]{150, 150});
        
        // Remove call from resetNPC()
        clearInvocations(npc);
        
        // Execute
        strategy.update();

        // Verify movement towards counter
        verify(npc).setX(START_X + SPEED);
        verify(npc).setY(START_Y + SPEED);
        verify(npc).setDirection("right");
    }

    @Test
    void update_shouldHandleFullOrderQueue() {
        // Setup
        when(npc.getX()).thenReturn(START_X);
        when(npc.getY()).thenReturn(START_Y);
        when(orderManager.isOrderQueueFull()).thenReturn(true);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(new int[]{150, 150});
        
        // Remove call from resetNPC()
        clearInvocations(npc);
        
        // Execute
        strategy.update();

        // Verify
        verify(npc).setX(START_X + SPEED);
        verify(npc).setY(START_Y + SPEED);
        verify(npc).setDirection("right");
        verify(orderManager, never()).createOrder();
    }

    @Test
    void update_shouldHandleInvalidSpotCoordinates_gracefully() {
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(100);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(null);

        // Remove call from fra resetNPC()
        clearInvocations(npc);

        assertDoesNotThrow(() -> strategy.update());
    }

    @Test
    void update_shouldHandleNullOrder() {
        // Arrange
        when(npc.getOrder()).thenReturn(null);
        when(npc.getX()).thenReturn(90);
        when(npc.getY()).thenReturn(90);
        when(npc.getSpeed()).thenReturn(SPEED);
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(new int[] {100, 100});

        // Remove call from fra resetNPC()
        clearInvocations(npc);

        // Act
        strategy.update();

        // Assert
        verify(npc).setWaiting(false);
    }

    @Test
    void update_shouldHandleDifferentMovementDirections() {
        // Arrange
        when(spotManager.findAndReserveSpot()).thenReturn(1);
        when(spotManager.getSpotCoordinates(1)).thenReturn(new int[]{140, 160});
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        when(npc.getSpeed()).thenReturn(SPEED);

        // Act
        strategy.update();

        // Assert
        verify(npc).setX(148);
        verify(npc).setY(152);
        verify(npc, atLeastOnce()).setDirection(anyString());
    }

    @Test
    void update_shouldHandleWaitingAtCounter() throws Exception {
        // Setup
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        when(npc.getOrder()).thenReturn(order);
        when(order.getOrderId()).thenReturn(ORDER_ID);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(List.of(pizzaOnCounter));
        when(pizzaOnCounter.getOrderId()).thenReturn(ORDER_ID);
        
        // Set state to WAIT_FOR_PIZZA
        setCurrentState(NPCMovementStrategy.NPCState.WAIT_FOR_PIZZA);
        
        // Remove call from fra resetNPC()
        clearInvocations(npc);
        
        // Execute
        strategy.update();

        // Verify
        verify(npc).setWaiting(true);
        verify(npc).setDirection("waiting_at_counter");
    }

    @Test
    void update_shouldHandleOrderRejection() throws Exception {
        // Arrange
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        when(npc.getOrder()).thenReturn(order);
        setCurrentState(NPCMovementStrategy.NPCState.PLACE_ORDER);

        // Remove call from fra resetNPC()
        clearInvocations(npc);

        // Act
        strategy.rejectOrder();
        strategy.update();

        // Assert
        verify(npc, atLeastOnce()).setWaiting(false);
        verify(npc).setDirection("down");
        verify(npc).setOrder(null);
    }

    @Test
    void update_shouldHandleMovementToExit() {
        // Arrange
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(100);
        when(spotManager.getSpotCoordinates(anyInt())).thenReturn(new int[]{150, 150});
        when(npc.getSpeed()).thenReturn(5);
        
        // Act
        strategy.update();
        
        // Assert
        verify(npc).setX(105);
        verify(npc).setY(105);
        verify(npc, atLeastOnce()).setDirection(anyString());
    }

    @Test
    void update_shouldHandleVIPNPCBehavior() throws Exception {
        // Setup
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        when(npc.getSkin()).thenReturn("NpcVIP");
        when(npc.getOrder()).thenReturn(order);
        when(order.getOrderId()).thenReturn(ORDER_ID);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(List.of(pizzaOnCounter));
        when(pizzaOnCounter.getOrderId()).thenReturn(ORDER_ID);
        
        // Set state to WAIT_FOR_PIZZA
        setCurrentState(NPCMovementStrategy.NPCState.WAIT_FOR_PIZZA);
        
        // Remove call from fra resetNPC()
        clearInvocations(npc);
        
        // Execute
        strategy.update();

        // Verify
        verify(npc).setWaiting(true);
        verify(npc).setDirection("waiting_at_counter");
    }

    @Test
    void update_shouldHandleMovementToPizzaSpot() {
        // Arrange
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(100);
        when(spotManager.getSpotCoordinates(anyInt())).thenReturn(new int[]{80, 80});
        when(npc.getSpeed()).thenReturn(5);
        when(npc.getOrder()).thenReturn(order);
        
        // Act
        strategy.update();
        
        // Assert
        verify(npc).setX(95);
        verify(npc).setY(95);
        verify(npc, atLeastOnce()).setDirection(anyString());
    }

    @Test
    void update_shouldHandleMovementBackToStart() {
        // Arrange
        when(npc.getX()).thenReturn(150);
        when(npc.getY()).thenReturn(150);
        when(npc.getSpeed()).thenReturn(SPEED);
        strategy.rejectOrder();

        // Act
        strategy.update();

        // Assert
        verify(npc).setX(148);
        verify(npc).setY(148);
        verify(npc).setDirection("left");
    }

    @Test
    void update_shouldHandleDifferentSpotIds() {
        // Arrange
        when(spotManager.findAndReserveSpot()).thenReturn(2); // Use spot 2 instead of 1
        when(spotManager.getSpotCoordinates(2)).thenReturn(new int[]{160, 160});
        when(npc.getX()).thenReturn(START_X);
        when(npc.getY()).thenReturn(START_Y);
        
        // Remove call from resetNPC()
        clearInvocations(npc);
        
        // Execute
        strategy.update();

        // Verify movement towards counter
        verify(npc).setX(START_X + SPEED);
        verify(npc).setY(START_Y + SPEED);
        verify(npc).setDirection("right");
    }
}