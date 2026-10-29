package inf112.skeleton.model.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.controller.GameController;
import inf112.skeleton.controller.KeyHandler;
import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.PizzaStore;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.managers.GameEconomyManager;
import inf112.skeleton.model.managers.KarmaManager;
import inf112.skeleton.model.managers.NPCManager;
import inf112.skeleton.model.managers.ObjectManager;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SoundManager;
import inf112.skeleton.model.managers.TileManager;
import inf112.skeleton.model.orders.Order;
import inf112.skeleton.view.ui.RenderManager;

/**
 * Test suite for GameLogic class.
 * Tests game logic, interactions, and state transitions.
 */
class GameLogicTest {
    
    @Mock
    private Player player;
    
    @Mock
    private TileManager tileManager;
    
    @Mock
    private OrderManager orderManager;
    
    @Mock
    private ObjectManager objectManager;
    
    @Mock
    private GameState gameState;
    
    @Mock
    private GameConfig config;
    
    @Mock
    private KeyHandler keyHandler;
    
    @Mock
    private MenuState menuState;
    
    @Mock
    private NPCManager npcManager;
    
    @Mock
    private PizzaManager pizzaManager;
    
    @Mock
    private GameEconomyManager economyManager;
    
    @Mock
    private KarmaManager karmaManager;
    
    @Mock
    private Store store;
    
    @Mock
    private PizzaStore pizzaStore;
    
    private GameLogic gameLogic;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup of mock objects
        when(gameState.getMenuState()).thenReturn(menuState);
        when(gameState.getCurrentState()).thenReturn(GameState.State.MENU);
        when(config.getTileSize()).thenReturn(32); // Standard tile size for testing
        when(store.getPizzaStore()).thenReturn(pizzaStore);
        
        // Create GameLogic instance with mock objects
        gameLogic = new GameLogic(player, tileManager, orderManager, objectManager, gameState, config);
        
        // Create and use GameController directly
        RenderManager renderManager = new RenderManager(config, gameLogic, null);
        new GameController(gameLogic, keyHandler, renderManager).update();
        
        // Get access to private fields to set mock objects
        try {
            // Set mock NPCManager
            java.lang.reflect.Field npcManagerField = GameLogic.class.getDeclaredField("npcManager");
            npcManagerField.setAccessible(true);
            npcManagerField.set(gameLogic, npcManager);
            
            // Set mock PizzaManager
            java.lang.reflect.Field pizzaManagerField = GameLogic.class.getDeclaredField("pizzaManager");
            pizzaManagerField.setAccessible(true);
            pizzaManagerField.set(gameLogic, pizzaManager);
            
            // Set mock GameEconomyManager
            java.lang.reflect.Field economyManagerField = GameLogic.class.getDeclaredField("economyManager");
            economyManagerField.setAccessible(true);
            economyManagerField.set(gameLogic, economyManager);

            // Set mock KarmaManager
            java.lang.reflect.Field karmaManagerField = GameLogic.class.getDeclaredField("karmaManager");
            karmaManagerField.setAccessible(true);
            karmaManagerField.set(gameLogic, karmaManager);
            
        } catch (Exception e) {
            fail("Could not set mock objects via reflection: " + e.getMessage());
        }
    }
    
    @Test
    void shouldUpdateGameComponentsWhenInPlayState() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f); // Karma over 0
        when(orderManager.getOrders()).thenReturn(new ArrayList<>()); // Empty order list
        when(pizzaManager.collectMoney()).thenReturn(50);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager).update();
        verify(pizzaManager).update();
        verify(npcManager).update();
        verify(economyManager).updateEconomy();
        verify(economyManager).addMoney(50);
        verify(objectManager).update();
    }
    
    @Test
    void shouldNotUpdateComponentsWhenNotInPlayState() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(false);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).update();
        verify(pizzaManager, never()).update();
        verify(npcManager, never()).update();
        verify(economyManager, never()).updateEconomy();
    }
    
    @Test
    void shouldUpdatePlayerMovementFromKeyHandler() {
        // Arrange
        when(keyHandler.isUpPressed()).thenReturn(true);
        when(keyHandler.isDownPressed()).thenReturn(false);
        when(keyHandler.isLeftPressed()).thenReturn(false);
        when(keyHandler.isRightPressed()).thenReturn(false);
        
        // Create GameController with the keyHandler
        RenderManager renderManager = new RenderManager(config, gameLogic, null);
        GameController gameController = new GameController(gameLogic, keyHandler, renderManager);
        
        // Act
        gameController.update();
        
        // Assert
        verify(player).updateMovement(true, false, false, false);
    }
    
    @Test
    void shouldDetectPlayerNearInteractableTile() {
        // Arrange
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.isTileInteractable(2, 3)).thenReturn(true);
        
        // Act
        boolean result = gameLogic.isPlayerNearInteractableTile();
        
        // Assert
        assertTrue(result, "Should recognize that the player is near an interactive tile");
        verify(tileManager).isTileInteractable(2, 3);
    }
    
    @Test
    void shouldDetectPlayerNearOvenTile() {
        // Arrange
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.getTileType(2, 3)).thenReturn("oven");
        
        // Act
        boolean result = gameLogic.isPlayerNearOvenTile();
        
        // Assert
        assertTrue(result, "Should recognize that the player is near an oven tile");
        verify(tileManager).getTileType(2, 3);
    }
    
    @Test
    void shouldShowInteractionPromptWhenConditionsMet() {
        // Arrange
        when(pizzaManager.hasPizzasInQueue()).thenReturn(true);
        when(pizzaManager.isCooking()).thenReturn(false);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(new ArrayList<>()); // Empty counter
        
        // Mock player near oven tile
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.getTileType(2, 3)).thenReturn("oven");
        when(tileManager.isTileInteractable(2, 3)).thenReturn(true);
        
        // Act
        boolean result = gameLogic.isInteractionPromptVisible();
        
        // Assert
        assertTrue(result, "Interaction prompt should be displayed when all conditions are met");
        verify(tileManager, times(2)).getTileType(2, 3); // Verify both calls
        verify(tileManager).isTileInteractable(2, 3);
    }
    
    @Test
    void shouldStartCooking() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        
        // Act
        gameLogic.startCooking();
        
        // Assert
        verify(pizzaManager).initiateBakingMinigame();
    }
    
    @Test
    void shouldResetGameState() {
        // Act
        gameLogic.resetGame();
        
        // Assert
        verify(player).setDefaultValues();
        verify(npcManager).clearNPCs();
        verify(economyManager).resetEconomy();
        verify(orderManager).clearOrders();
        verify(pizzaManager).reset();
        verify(objectManager).reset();
        verify(gameState).setState(GameState.State.PLAY);
    }
    
    @Test
    void shouldHandleMoneyOperations() {
        // Test addMoney
        gameLogic.addMoney(100);
        verify(economyManager).addMoney(100);
        
        // Test spendMoney
        gameLogic.spendMoney(50);
        verify(economyManager).spendMoney(50);
        
        // Test getMoney
        when(economyManager.getMoney()).thenReturn(150);
        assertEquals(150, gameLogic.getMoney());
    }

    @Test
    void shouldHandlePlayerCollisionWithObjects() {
        // Arrange
        GameObject mockObject = mock(GameObject.class);
        List<GameObject> objects = Collections.singletonList(mockObject);
        when(objectManager.getObjects()).thenReturn(objects);
        when(mockObject.collidesWith(any(Player.class))).thenReturn(true);
        
        // Act
        gameLogic.updatePlayerMovement(true, false, false, false);
        
        // Assert
        verify(player).updateMovement(true, false, false, false);
    }

    @Test
    void shouldHandleSoundSettings() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        
        // Act & Assert
        gameLogic.setSoundVolume(0.5f);
        assertEquals(0.5f, gameLogic.getSoundVolume());
        
        gameLogic.toggleSound();
        assertFalse(gameLogic.isSoundEnabled());
        
        gameLogic.toggleSound();
        assertTrue(gameLogic.isSoundEnabled());
    }

    @Test
    void shouldHandleMenuNavigation() {
        // Arrange
        when(gameState.isInState(GameState.State.MENU)).thenReturn(true);
        when(menuState.getCurrentSelection()).thenReturn(0);
        String[] menuOptions = {"Start", "Options", "Exit"};
        when(menuState.getCurrentOptions(any())).thenReturn(menuOptions);
        
        // Act & Assert
        gameLogic.incrementMenuSelection();
        verify(menuState).incrementSelection(menuOptions);
        
        gameLogic.decrementMenuSelection();
        verify(menuState).decrementSelection(menuOptions);
        
        assertEquals(0, gameLogic.getCurrentMenuSelection());
    }

    @Test
    void shouldHandlePizzaOperations() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.isTileInteractable(anyInt(), anyInt())).thenReturn(true);
        when(tileManager.getTileType(anyInt(), anyInt())).thenReturn("oven");
        
        // Act & Assert
        gameLogic.addPizzaToQueue();
        verify(pizzaManager).addPizzaToQueue();
        
        when(pizzaManager.isCooking()).thenReturn(true);
        assertTrue(gameLogic.isPizzaCooking());
        
        when(pizzaManager.hasPizzasInQueue()).thenReturn(true);
        when(pizzaManager.isCooking()).thenReturn(false);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(Collections.singletonList(mock(PizzaOnCounter.class)));
        assertTrue(gameLogic.isInteractionPromptVisible());
        
        List<PizzaOnCounter> mockPizzas = Collections.singletonList(mock(PizzaOnCounter.class));
        when(pizzaManager.getPizzasOnCounter()).thenReturn(mockPizzas);
        assertEquals(mockPizzas, gameLogic.getPizzasOnCounter());
    }

    @Test
    void shouldHandleTileOperations() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(tileManager.getMapTileNum(anyInt(), anyInt())).thenReturn(1);
        when(tileManager.getTileImage(anyInt())).thenReturn(mock(java.awt.image.BufferedImage.class));
        
        // Act & Assert
        assertEquals(1, gameLogic.getTileNum(0, 0));
        assertNotNull(gameLogic.getTileImage(1));
    }

    @Test
    void shouldHandleGameStateTransitions() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(gameState.getCurrentState()).thenReturn(GameState.State.PAUSE);
        
        // Act & Assert
        gameLogic.setState(GameState.State.PAUSE);
        verify(gameState).setState(GameState.State.PAUSE);
        
        assertEquals(GameState.State.PAUSE, gameLogic.getCurrentState());
        assertNotNull(gameLogic.getGameState());
    }

    @Test
    void shouldHandleEconomyOperations() {
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(economyManager.getMoney()).thenReturn(100);
        
        gameLogic.addMoney(100);
        verify(economyManager).addMoney(100);
        
        gameLogic.spendMoney(50);
        verify(economyManager).spendMoney(50);
        
        gameLogic.resetMoney();
        verify(economyManager).resetEconomy();
        
        assertEquals(100, gameLogic.getMoney());
        assertNotNull(gameLogic.getEconomyManager());
    }

    @Test
    void shouldHandleNPCOperations() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        List<NPC> mockNPCs = Collections.singletonList(mock(NPC.class));
        when(npcManager.getNPCs()).thenReturn(mockNPCs);
        
        // Act & Assert
        assertEquals(mockNPCs, gameLogic.getNPCs());
    }

    @Test
    void shouldHandleGameObjectOperations() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        List<GameObject> mockObjects = Collections.singletonList(mock(GameObject.class));
        when(objectManager.getObjects()).thenReturn(mockObjects);
        
        // Act & Assert
        assertEquals(mockObjects, gameLogic.getGameObjects());
    }

    @Test
    void shouldHandleMenuStateTransitions() {
        // Arrange
        when(gameState.isInState(any())).thenReturn(true);
        when(menuState.getCurrentSelection()).thenReturn(0);
        String[] menuOptions = {"Start", "Settings", "About"};
        when(menuState.getCurrentOptions(any())).thenReturn(menuOptions);
        
        // Act & Assert
        // Test transition from MENU to PLAY
        gameLogic.setState(GameState.State.MENU);
        verify(gameState).setState(GameState.State.MENU);
        
        // Test transition from PLAY to PAUSE
        gameLogic.setState(GameState.State.PLAY);
        verify(gameState).setState(GameState.State.PLAY);
        
        // Test transition from PAUSE to PLAY
        gameLogic.setState(GameState.State.PAUSE);
        verify(gameState).setState(GameState.State.PAUSE);
        
        // Test transition to SETTINGS and back
        gameLogic.setState(GameState.State.SETTINGS);
        verify(gameState).setState(GameState.State.SETTINGS);
        
        // Test transition to ABOUT and back
        gameLogic.setState(GameState.State.ABOUT);
        verify(gameState).setState(GameState.State.ABOUT);
        
        // Test transition to GAME_OVER
        gameLogic.setState(GameState.State.GAME_OVER);
        verify(gameState).setState(GameState.State.GAME_OVER);
    }
    
    /**
     * Tests the observer pattern functionality in GameLogic.
     */
    @Test
    void shouldNotifyObserversWhenGameStateChanges() {
        // Arrange
        GameLogic.GameLogicObserver mockObserver = mock(GameLogic.GameLogicObserver.class);
        gameLogic.addObserver(mockObserver);
        
        // Act
        gameLogic.setState(GameState.State.PLAY);
        
        // Assert
        verify(mockObserver).update(gameLogic);
    }
    
    @Test
    void shouldNotAddDuplicateObservers() {
        // Arrange
        GameLogic.GameLogicObserver mockObserver = mock(GameLogic.GameLogicObserver.class);
        
        // Act
        gameLogic.addObserver(mockObserver);
        gameLogic.addObserver(mockObserver);
        gameLogic.setState(GameState.State.PLAY);
        
        // Assert
        verify(mockObserver, times(1)).update(gameLogic);
    }
    
    @Test
    void shouldRemoveObserver() {
        // Arrange
        GameLogic.GameLogicObserver mockObserver = mock(GameLogic.GameLogicObserver.class);
        gameLogic.addObserver(mockObserver);
        gameLogic.removeObserver(mockObserver);
        
        // Act
        gameLogic.setState(GameState.State.PLAY);
        
        // Assert
        verify(mockObserver, never()).update(gameLogic);
    }
    
    /**
     * Tests the popup message functionality in GameLogic.
     */
    @Test
    void shouldHandlePopupMessages() {
        // Arrange
        String testMessage = "Test popup message";
        
        // Act
        gameLogic.setPopupMessage(testMessage);
        
        // Assert
        assertEquals(testMessage, gameLogic.getPopupMessage());
    }
    
    @Test
    void shouldHandleKarmaSystem() {
        // Arrange
        KarmaManager mockKarmaManager = mock(KarmaManager.class);
        when(mockKarmaManager.getCurrentKarma()).thenReturn(75.0f);
        
        // Set up the mock KarmaManager using reflection
        try {
            java.lang.reflect.Field karmaManagerField = GameLogic.class.getDeclaredField("karmaManager");
            karmaManagerField.setAccessible(true);
            karmaManagerField.set(gameLogic, mockKarmaManager);
        } catch (Exception e) {
            fail("Failed to set karmaManager: " + e.getMessage());
        }
        
        // Act
        float karma = gameLogic.getKarma();
        
        // Assert
        assertEquals(75.0f, karma);
        verify(mockKarmaManager).getCurrentKarma();
    }
    
    @Test
    void shouldHandleShopMenu() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        
        // Act
        gameLogic.toggleShopMenu();
        
        // Assert
        assertTrue(gameLogic.isShopMenuOpen());
        
        // Act again
        gameLogic.toggleShopMenu();
        
        // Assert
        assertFalse(gameLogic.isShopMenuOpen());
    }
    
    @Test
    void shouldHandleCollisionMasks() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        
        // Act
        gameLogic.toggleCollisionMasks();
        
        // Assert
        assertTrue(gameLogic.shouldShowCollisionMasks());
        
        // Act again
        gameLogic.toggleCollisionMasks();
        
        // Assert
        assertFalse(gameLogic.shouldShowCollisionMasks());
    }
    
    @Test
    void shouldTransitionToGameOverWhenKarmaReachesZero() {
        // Arrange
        KarmaManager mockKarmaManager = mock(KarmaManager.class);
        when(mockKarmaManager.getCurrentKarma()).thenReturn(0.0f);
        
        // Set up the mock KarmaManager using reflection
        try {
            java.lang.reflect.Field karmaManagerField = GameLogic.class.getDeclaredField("karmaManager");
            karmaManagerField.setAccessible(true);
            karmaManagerField.set(gameLogic, mockKarmaManager);
        } catch (Exception e) {
            fail("Failed to set karmaManager: " + e.getMessage());
        }
        
        // Set game state to PLAY
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(gameState).setState(GameState.State.GAME_OVER);
    }
    
    @Test
    void shouldDetectPlayerNearShop() {
        // Arrange
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.getTileType(2, 3)).thenReturn("Store");
        
        // Act
        boolean result = gameLogic.isPlayerNearShop();
        
        // Assert
        assertTrue(result, "Should detect player near shop");
        verify(tileManager).getTileType(2, 3);
    }
    
    @Test
    void shouldNotDetectPlayerNearShopWhenNotOnStoreTile() {
        // Arrange
        when(player.getX()).thenReturn(64);
        when(player.getY()).thenReturn(96);
        when(tileManager.getTileType(2, 3)).thenReturn("floor");
        
        // Act
        boolean result = gameLogic.isPlayerNearShop();
        
        // Assert
        assertFalse(result, "Should not detect player near shop when not on store tile");
        verify(tileManager).getTileType(2, 3);
    }
    
    @Test
    void shouldGetStore() {
        // Act
        Store gameStore = gameLogic.getStore();
        
        // Assert
        assertNotNull(gameStore, "Store should not be null");
    }
    
    @Test
    void shouldUpdatePizzaCookingProgress() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.isCooking()).thenReturn(true);
        when(pizzaManager.getCurrentPizza()).thenReturn("Margherita");
        when(pizzaManager.getStartTime()).thenReturn(System.currentTimeMillis() - 1000); // 1s has gone
        when(pizzaManager.getCookTime()).thenReturn(2000L); // 2s total cooking time
        when(pizzaManager.getCookingSpeedMultiplier()).thenReturn(1.0);

        // Act
        gameLogic.update();

        // Assert
        verify(pizzaManager).update();
        assertEquals(0.5, gameLogic.getPizzaCookingProgress(), 0.1, 
            "Cooking progress should be approximately 0.5 after half the cooking time");
    }

    @ParameterizedTest
    @CsvSource({
        "true, 0.5, 'Pizza is cooking with partial progress', true, 0.5",
        "false, 0.0, 'Pizza is not cooking', false, 0.0",
        "true, 1.0, 'Pizza cooking is complete', true, 1.0",
        "true, 0.5, 'Pizza cooking is in progress', true, 0.5",
        "true, 1.0, 'Vegetarian pizza ready', true, 1.0",
        "false, 0.5, 'Pepperoni pizza cooking', false, 0.5",
        "true, 2.0, 'Hawaiian pizza ready', true, 2.0",
        "false, 1.5, 'Margherita pizza cooking', false, 1.5"
    })
    void shouldUpdatePizzaCookingProgress(boolean isCooking, double progress, String testDescription, 
            boolean isReadyToTakeOut, double cookingSpeedMultiplier) {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.isCooking()).thenReturn(isCooking);
        when(pizzaManager.getCookingProgress()).thenReturn(progress);
        when(pizzaManager.isReadyToTakeOut()).thenReturn(isReadyToTakeOut);
        when(pizzaManager.getCookingSpeedMultiplier()).thenReturn(cookingSpeedMultiplier);

        // Act
        gameLogic.update();

        // Assert
        verify(pizzaManager).update();
        if (isCooking) {
            assertEquals(progress, pizzaManager.getCookingProgress(), 0.001, 
                "Cooking progress should match for: " + testDescription);
        }
    }

    // --- Minigame Delegate Tests ---

    @Test
    void shouldDelegateIsBakingMinigameActive() {
        when(pizzaManager.isBakingMinigameActive()).thenReturn(true);
        assertTrue(gameLogic.isBakingMinigameActive());
        verify(pizzaManager).isBakingMinigameActive();
    }

    @Test
    void shouldDelegateGetMinigameProgress() {
        when(pizzaManager.getMinigameProgress()).thenReturn(0.5);
        assertEquals(0.5, gameLogic.getMinigameProgress(), 0.001);
        verify(pizzaManager).getMinigameProgress();
    }

    @Test
    void shouldDelegateAttemptMinigameHit() {
        gameLogic.attemptMinigameHit();
        verify(pizzaManager).attemptMinigameHit();
    }

    @Test
    void shouldDelegateGetMinigameSuccessStart() {
        when(pizzaManager.getMinigameSuccessStart()).thenReturn(0.7);
        assertEquals(0.7, gameLogic.getMinigameSuccessStart(), 0.001);
        verify(pizzaManager).getMinigameSuccessStart();
    }

    @Test
    void shouldDelegateGetMinigameSuccessEnd() {
        when(pizzaManager.getMinigameSuccessEnd()).thenReturn(0.9);
        assertEquals(0.9, gameLogic.getMinigameSuccessEnd(), 0.001);
        verify(pizzaManager).getMinigameSuccessEnd();
    }

    // --- End Minigame Delegate Tests ---

    @Test
    void isInteractionPromptVisible_shouldReturnTrueWhenOnStoreTile() {
        // Setup
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        when(config.getTileSize()).thenReturn(32);
        when(tileManager.getTileType(3, 3)).thenReturn("Store");
        when(tileManager.isTileInteractable(3, 3)).thenReturn(true);
        when(pizzaManager.hasPizzasInQueue()).thenReturn(false);
        when(pizzaManager.isCooking()).thenReturn(false);
        when(pizzaManager.getPizzasOnCounter()).thenReturn(new ArrayList<>());
        
        // Execute
        boolean result = gameLogic.isInteractionPromptVisible();
        
        // Verify
        assertTrue(result);
    }

    @Test
    void isInteractionPromptVisible_shouldReturnFalseWhenNotOnInteractableTile() {
        // Setup
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        when(config.getTileSize()).thenReturn(32);
        when(tileManager.getTileType(3, 3)).thenReturn("floor");
        when(tileManager.isTileInteractable(3, 3)).thenReturn(false);
        
        // Execute
        boolean result = gameLogic.isInteractionPromptVisible();
        
        // Verify
        assertFalse(result);
    }

    @Test
    void isInteractionPromptVisible_shouldReturnFalseWhenPizzaIsCooking() {
        // Setup
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        when(config.getTileSize()).thenReturn(32);
        when(tileManager.getTileType(3, 3)).thenReturn("oven");
        when(tileManager.isTileInteractable(3, 3)).thenReturn(true);
        when(pizzaManager.isCooking()).thenReturn(true);
        
        // Execute
        boolean result = gameLogic.isInteractionPromptVisible();
        
        // Verify
        assertFalse(result);
    }

    @Test
    void isInteractionPromptVisible_shouldReturnFalseWhenPizzasInQueue() {
        // Setup
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        when(config.getTileSize()).thenReturn(32);
        when(tileManager.getTileType(3, 3)).thenReturn("oven");
        when(tileManager.isTileInteractable(3, 3)).thenReturn(true);
        when(pizzaManager.hasPizzasInQueue()).thenReturn(true);
        when(pizzaManager.isCooking()).thenReturn(true); // Pizza is cooking
        when(pizzaManager.getPizzasOnCounter()).thenReturn(new ArrayList<>());
        
        // Execute
        boolean result = gameLogic.isInteractionPromptVisible();
        
        // Verify
        assertFalse(result);
    }

    @Test
    void shouldHandlePlayerBankruptcy() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        SoundManager mockSound = mock(SoundManager.class);
        
        // Set up the mock SoundManager using reflection
        try {
            java.lang.reflect.Field soundField = GameLogic.class.getDeclaredField("sound");
            soundField.setAccessible(true);
            soundField.set(gameLogic, mockSound);
        } catch (Exception e) {
            fail("Failed to set sound: " + e.getMessage());
        }
        
        // Act
        gameLogic.handlePlayerBankruptcy();
        
        // Assert
        verify(gameState).setState(GameState.State.GAME_OVER);
        verify(mockSound).stopLoop();
        verify(mockSound).play(4);
    }
    
    @Test
    void shouldHandlePopupMessageExpiration() {
        // Arrange
        String testMessage = "Test message";
        
        // Act
        gameLogic.setPopupMessage(testMessage);
        
        // Assert
        assertEquals(testMessage, gameLogic.getPopupMessage());
        
        // Simulate time passing beyond POPUP_DURATION
        try {
            java.lang.reflect.Field popupMessageTimeField = GameLogic.class.getDeclaredField("popupMessageTime");
            popupMessageTimeField.setAccessible(true);
            
            // Get POPUP_DURATION using reflection
            java.lang.reflect.Field popupDurationField = GameLogic.class.getDeclaredField("POPUP_DURATION");
            popupDurationField.setAccessible(true);
            long popupDuration = popupDurationField.getLong(null);
            
            popupMessageTimeField.set(gameLogic, System.currentTimeMillis() - popupDuration - 1000);
        } catch (Exception e) {
            fail("Failed to set popupMessageTime: " + e.getMessage());
        }
        
        // Assert
        assertNull(gameLogic.getPopupMessage());
    }

    @Test
    void shouldGetPizzaCookingProgress() {
        // Arrange
        when(pizzaManager.isReadyToTakeOut()).thenReturn(true);
        
        // Act
        double progress = gameLogic.getPizzaCookingProgress();
        
        // Assert
        assertEquals(1.0, progress, 0.001, "Progress should be 1.0 when pizza is ready");
        verify(pizzaManager).isReadyToTakeOut();
    }

    @Test
    void update_shouldHandlePauseState() {
        // Arrange
        when(gameState.isInState(GameState.State.PAUSE)).thenReturn(true);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).update();
        verify(pizzaManager, never()).update();
        verify(npcManager, never()).update();
    }

    @Test
    void update_shouldHandleGameOverState() {
        // Arrange
        when(gameState.isInState(GameState.State.GAME_OVER)).thenReturn(true);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).update();
        verify(pizzaManager, never()).update();
        verify(npcManager, never()).update();
    }

    @Test
    void updatePlayerMovement_shouldHandleCollisions() {
        // Arrange
        GameObject obstacle = mock(GameObject.class);
        when(objectManager.getObjects()).thenReturn(List.of(obstacle));
        when(obstacle.collidesWith(player)).thenReturn(true);
        int initialX = 100;
        int initialY = 100;
        when(player.getX()).thenReturn(initialX);
        when(player.getY()).thenReturn(initialY);
        
        // Act
        gameLogic.updatePlayerMovement(true, false, false, false);
        
        // Assert
        verify(player).setX(initialX);
        verify(player).setY(initialY);
    }

    @Test
    void getInteractionPromptText_shouldShowOvenPromptWhenNearOven() {
        // Arrange
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        when(config.getTileSize()).thenReturn(32);
        when(tileManager.getTileType(3, 3)).thenReturn("oven");
        when(tileManager.isTileInteractable(3, 3)).thenReturn(true);
        when(pizzaManager.isReadyToTakeOut()).thenReturn(false); // Pizza is not ready to be taken out
        
        // Act
        String promptText = gameLogic.getInteractionPromptText();
        
        // Assert
        assertEquals("Start cooking", promptText);
    }

    @Test
    void shouldHandleExpiredOrders() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        
        Order expiredOrder = mock(Order.class);
        when(expiredOrder.isExpired()).thenReturn(true);
        List<Order> orders = Collections.singletonList(expiredOrder);
        when(orderManager.getOrders()).thenReturn(orders);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager).processOrder(false, expiredOrder);
        verify(karmaManager).decreaseKarma();
    }

    @Test
    void shouldNotProcessNonExpiredOrders() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        Order nonExpiredOrder = mock(Order.class);
        when(nonExpiredOrder.isExpired()).thenReturn(false);
        List<Order> orders = Collections.singletonList(nonExpiredOrder);
        when(orderManager.getOrders()).thenReturn(orders);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).processOrder(anyBoolean(), any(Order.class));
        verify(karmaManager, never()).decreaseKarma();
    }

    @Test
    void shouldUpdateAllManagersInPlayState() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.collectMoney()).thenReturn(100);
        
        // Act
        gameLogic.update();
        
        // Assert - verify that all managers are getting updated
        verify(orderManager).update();
        verify(pizzaManager).update();
        verify(npcManager).update();
        verify(economyManager).updateEconomy();
        verify(economyManager).addMoney(100);
        verify(objectManager).update();
    }

    @Test
    void shouldNotUpdateManagersWhenNotInPlayState() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(false);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).update();
        verify(pizzaManager, never()).update();
        verify(npcManager, never()).update();
        verify(economyManager, never()).updateEconomy();
        verify(economyManager, never()).addMoney(anyInt());
        verify(objectManager, never()).update();
    }

    @Test
    void shouldHandleMultipleExpiredOrders() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        
        Order expiredOrder1 = mock(Order.class);
        Order expiredOrder2 = mock(Order.class);
        when(expiredOrder1.isExpired()).thenReturn(true);
        when(expiredOrder2.isExpired()).thenReturn(true);
        List<Order> orders = Arrays.asList(expiredOrder1, expiredOrder2);
        when(orderManager.getOrders()).thenReturn(orders);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager).processOrder(false, expiredOrder1);
        verify(orderManager).processOrder(false, expiredOrder2);
        verify(karmaManager, times(2)).decreaseKarma();
    }

    @Test
    void shouldHandleMixedExpiredAndNonExpiredOrders() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        
        Order expiredOrder = mock(Order.class);
        Order validOrder = mock(Order.class);
        when(expiredOrder.isExpired()).thenReturn(true);
        when(validOrder.isExpired()).thenReturn(false);
        List<Order> orders = Arrays.asList(expiredOrder, validOrder);
        when(orderManager.getOrders()).thenReturn(orders);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager).processOrder(false, expiredOrder);
        verify(orderManager, never()).processOrder(false, validOrder);
        verify(karmaManager).decreaseKarma();
    }

    @Test
    void shouldHandleEmptyOrderList() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(orderManager.getOrders()).thenReturn(Collections.emptyList());
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).processOrder(anyBoolean(), any(Order.class));
        verify(karmaManager, never()).decreaseKarma();
    }

    @Test
    void shouldHandleNullOrderList() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(orderManager.getOrders()).thenReturn(null);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(orderManager, never()).processOrder(anyBoolean(), any(Order.class));
        verify(karmaManager, never()).decreaseKarma();
    }

    @Test
    void shouldHandleNegativeMoneyCollection() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.collectMoney()).thenReturn(-50);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(economyManager).addMoney(-50);
    }

    @Test
    void shouldHandleZeroMoneyCollection() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.collectMoney()).thenReturn(0);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(economyManager).addMoney(0);
    }

    @Test
    void shouldHandleLargeMoneyCollection() {
        // Arrange
        when(gameState.isInState(GameState.State.PLAY)).thenReturn(true);
        when(karmaManager.getCurrentKarma()).thenReturn(100.0f);
        when(orderManager.getOrders()).thenReturn(new ArrayList<>());
        when(pizzaManager.collectMoney()).thenReturn(Integer.MAX_VALUE);
        
        // Act
        gameLogic.update();
        
        // Assert
        verify(economyManager).addMoney(Integer.MAX_VALUE);
    }
} 