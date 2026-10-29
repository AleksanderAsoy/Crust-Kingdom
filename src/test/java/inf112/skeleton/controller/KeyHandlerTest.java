package inf112.skeleton.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.game.GameState;
import inf112.skeleton.model.game.MenuState;
import inf112.skeleton.model.managers.GameEconomyManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SoundManager;
import inf112.skeleton.view.renderers.StoreRenderer;
import inf112.skeleton.view.ui.RenderManager;

/**
 * Test suite for KeyHandler class which handles keyboard input in the game.
 * <p>
 * The test suite is organized into several sections:
 * - Movement controls (WASD keys)
 * - Game state transitions (Play, Pause, Menu)
 * - Menu navigation (Up, Down, Enter)
 * - Order system interaction (1, 2, 3 keys)
 * <p>
 * Test structure:
 * - Uses TestUtils for creating KeyEvents
 * - Uses Mockito for mocking GamePanel and OrderManager
 * - Groups related tests using @Nested classes
 */
class KeyHandlerTest {

    private static final int DEFAULT_MENU_OPTIONS = 3;
    private static final long DEFAULT_TIMESTAMP = System.currentTimeMillis();
    
    private KeyHandler keyHandler;
    private GameLogic gameLogic;
    private Component component;
    private KeyEvent mockKeyEvent;

    /**
     * Utility class for creating KeyEvents in a consistent way across tests.
     * Reduces code duplication and makes tests more maintainable.
     */
    private static class TestUtils {
        /**
         * Creates a KeyEvent for testing keyboard input.
         *
         * @param component The component receiving the event
         * @param keyCode The key code (e.g., KeyEvent.VK_W)
         * @param keyChar The character representation of the key
         * @param isPressed True if key is pressed, false if released
         * @return A KeyEvent instance
         */
        static KeyEvent createKeyEvent(Component component, int keyCode, char keyChar, boolean isPressed) {
            int id = isPressed ? KeyEvent.KEY_PRESSED : KeyEvent.KEY_RELEASED;
            return new KeyEvent(component, id, DEFAULT_TIMESTAMP, 0, keyCode, keyChar);
        }
    }

    /**
     * Sets up mock objects used across tests.
     * Called before each test to ensure a clean state.
     */
    private void setupDefaultMocks() {
        gameLogic = mock(GameLogic.class);
        component = mock(Component.class);
        GameState gameState = mock(GameState.class);
        MenuState menuState = mock(MenuState.class);
        
        when(gameLogic.getGameState()).thenReturn(gameState);
        when(gameState.getMenuState()).thenReturn(menuState);
        when(gameState.getCurrentState()).thenReturn(GameState.State.PLAY);
        
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        when(gameLogic.isOrderPopupVisible()).thenReturn(false);
        when(gameLogic.processOrder(true)).thenReturn(true);
        when(gameLogic.isPlayerNearInteractableTile()).thenReturn(false);
    }

    @BeforeEach
    void setUp() {
        setupDefaultMocks();
        Player player = mock(Player.class);
        when(gameLogic.getPlayer()).thenReturn(player);
        mockKeyEvent = mock(KeyEvent.class);
        keyHandler = new KeyHandler(gameLogic);
    }

    /**
     * Generates tests for basic movement controls.
     * Tests both key press and expected state changes for WASD keys.
     */
    @TestFactory
    Stream<DynamicTest> generateMovementTests() {
        record MovementTest(int keyCode, char keyChar, String direction, boolean isPressed) {}

        var movements = List.of(
            new MovementTest(KeyEvent.VK_W, 'W', "up", true),
            new MovementTest(KeyEvent.VK_S, 'S', "down", true),
            new MovementTest(KeyEvent.VK_A, 'A', "left", true),
            new MovementTest(KeyEvent.VK_D, 'D', "right", true)
        );

        return movements.stream()
            .map(m -> DynamicTest.dynamicTest(
                "test " + m.direction() + " movement",
                () -> {
                    KeyEvent testEvent = TestUtils.createKeyEvent(component, m.keyCode(), m.keyChar(), m.isPressed());
                    keyHandler.keyPressed(testEvent);

                    switch (m.direction()) {
                        case "up" -> assertTrue(keyHandler.isUpPressed());
                        case "down" -> assertTrue(keyHandler.isDownPressed());
                        case "left" -> assertTrue(keyHandler.isLeftPressed());
                        case "right" -> assertTrue(keyHandler.isRightPressed());
                        default -> fail("Unexpected direction: " + m.direction());
                    }
                }
            ));
    }

    /**
     * Tests related to movement key functionality.
     * Includes tests for movement restrictions and multiple key presses.
     */
    @Nested
    class MovementKeyTests {
        private GameLogic testGameLogic;
        private KeyHandler testKeyHandler;
        private Component testComponent;

        @BeforeEach
        void setUp() {
            testGameLogic = mock(GameLogic.class);
            testComponent = mock(Component.class);
            testKeyHandler = new KeyHandler(testGameLogic);
            
            GameState gameState = mock(GameState.class);
            MenuState menuState = mock(MenuState.class);
            
            when(testGameLogic.getGameState()).thenReturn(gameState);
            when(gameState.getMenuState()).thenReturn(menuState);
            when(gameState.getCurrentState()).thenReturn(GameState.State.PLAY);
            
            String[] options = new String[]{"Start", "Options", "Quit"};
            when(menuState.getCurrentOptions(any())).thenReturn(options);
            when(menuState.getCurrentSelection()).thenReturn(0);
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        }

        @Test
        void keyPressed_noMovementWhenNotInPlayState() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PAUSE);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);

            testKeyHandler.keyPressed(keyEvent);

            assertFalse(testKeyHandler.isUpPressed());
        }

        @Test
        void keyPressed_multipleKeysCanBePressedSimultaneously() {
            KeyEvent keyEventW = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);
            KeyEvent keyEventD = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_D, 'D', true);

            testKeyHandler.keyPressed(keyEventW);
            testKeyHandler.keyPressed(keyEventD);

            assertAll(
                () -> assertTrue(testKeyHandler.isUpPressed()),
                () -> assertTrue(testKeyHandler.isRightPressed())
            );
        }

        @Test
        void keyPressed_shouldHandleInvalidKeyCode() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, 999, '\0', true);

            testKeyHandler.keyPressed(keyEvent);

            assertAll(
                () -> assertFalse(testKeyHandler.isUpPressed()),
                () -> assertFalse(testKeyHandler.isDownPressed()),
                () -> assertFalse(testKeyHandler.isLeftPressed()),
                () -> assertFalse(testKeyHandler.isRightPressed())
            );
        }

        @Test
        void keyReleased_shouldHandleReleaseWithoutPress() {
            KeyEvent releaseEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', false);
            testKeyHandler.keyReleased(releaseEvent);

            assertFalse(testKeyHandler.isUpPressed());
        }

        @Test
        void keyPressed_shouldHandleRapidKeyPressAndRelease() {
            KeyEvent pressW = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);
            KeyEvent releaseW = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', false);

            testKeyHandler.keyPressed(pressW);
            assertTrue(testKeyHandler.isUpPressed());

            testKeyHandler.keyReleased(releaseW);
            assertFalse(testKeyHandler.isUpPressed());
        }

        @Test
        void keyPressed_shouldResetMovementOnStateChange() {
            // Set up initial state
            KeyEvent pressW = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);
            testKeyHandler.keyPressed(pressW);
            assertTrue(testKeyHandler.isUpPressed());

            // Change game state
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PAUSE);
            KeyEvent pressEsc = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED, true);
            testKeyHandler.keyPressed(pressEsc);

            // Verify movement flags are reset
            assertAll(
                () -> assertFalse(testKeyHandler.isUpPressed()),
                () -> assertFalse(testKeyHandler.isDownPressed()),
                () -> assertFalse(testKeyHandler.isLeftPressed()),
                () -> assertFalse(testKeyHandler.isRightPressed())
            );
        }
    }

    /**
     * Tests related to game control functionality.
     * Includes tests for state transitions, menu navigation, and order system.
     */
    @Nested
    class GameControlTests {
        private GameLogic testGameLogic;
        private KeyHandler testKeyHandler;
        private Component testComponent;

        @BeforeEach
        void setUp() {
            testGameLogic = mock(GameLogic.class);
            testKeyHandler = new KeyHandler(testGameLogic);
            testComponent = mock(Component.class);
            GameEconomyManager economyManager = mock(GameEconomyManager.class);
            PizzaManager pizzaManager = mock(PizzaManager.class);
            
            GameState gameState = mock(GameState.class);
            MenuState menuState = mock(MenuState.class);
            SoundManager sound = mock(SoundManager.class);
            
            when(testGameLogic.getGameState()).thenReturn(gameState);
            when(testGameLogic.getEconomyManager()).thenReturn(economyManager);
            when(testGameLogic.getPizzaManager()).thenReturn(pizzaManager);
            when(testGameLogic.getSound()).thenReturn(sound);
            when(gameState.getMenuState()).thenReturn(menuState);
            when(gameState.getCurrentState()).thenReturn(GameState.State.PLAY);
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
            
            String[] options = new String[]{"Start", "Options", "Quit"};
            when(menuState.getCurrentOptions(any())).thenReturn(options);
        }

        @Test
        void keyPressed_escapeTogglesFromPlayToPause() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED, true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setState(GameState.State.PAUSE);
        }

        @Test
        void keyPressed_escapeTogglesFromPauseToPlay() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PAUSE);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED, true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setState(GameState.State.PLAY);
        }

        @Test
        void keyPressed_enterInMenuPosition0ShouldStartGame() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.MENU);
            when(menuState.getCurrentSelection()).thenReturn(0);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setState(GameState.State.PLAY);
        }

        @Test
        void keyPressed_enterInMenuPosition1ShouldNotChangeState() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(menuState.getCurrentSelection()).thenReturn(1);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic, never()).setState(any());
        }

        @Test
        void keyPressed_enterInMenuPosition2ShouldNotChangeState() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(menuState.getCurrentSelection()).thenReturn(2);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic, never()).setState(any());
        }
        
        @Test
        void keyPressed_menuNavigationShouldHandleInvalidSelection() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.MENU);
            String[] emptyOptions = new String[0];
            when(menuState.getCurrentOptions(any())).thenReturn(emptyOptions);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);

            testKeyHandler.keyPressed(keyEvent);
            
            verify(menuState).decrementSelection(emptyOptions);
            verify(testGameLogic, never()).setState(any());
        }

        @Test
        void keyPressed_shouldHandleShopInteraction() {
            when(testGameLogic.isPlayerNearShop()).thenReturn(true);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).toggleShopMenu();
        }

        @Test
        void keyPressed_shouldHandleCookingInteraction() {
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(true);
            when(testGameLogic.getPizzaManager().hasPizzasInQueue()).thenReturn(true);
            when(testGameLogic.getPizzaManager().isCooking()).thenReturn(false);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).startCooking();
        }

        @Test
        void keyPressed_shouldNotHandleCookingOnNonOvenTiles() {
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(false);
            when(testGameLogic.isPlayerNearInteractableTile()).thenReturn(true);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic, never()).startCooking();
        }

        @Test
        void keyPressed_shouldHandleOrderAcceptance() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
            when(testGameLogic.isOrderPopupVisible()).thenReturn(true);
            when(testGameLogic.processOrder(true)).thenReturn(true);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_2, '2', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).processOrder(true);
            verify(testGameLogic).addPizzaToQueue();
        }

        @Test
        void keyPressed_shouldHandleOrderRejection() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
            when(testGameLogic.isOrderPopupVisible()).thenReturn(true);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_3, '3', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).processOrder(false);
        }

        @Test
        void keyPressed_shouldHandleSettingsVolumeAdjustment() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.SETTINGS);
            when(testGameLogic.getSoundVolume()).thenReturn(0.5f);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED, true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setSoundVolume(0.4f);
        }

        @Test
        void keyPressed_shouldHandleSettingsVolumeMaxLimit() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.SETTINGS);
            when(testGameLogic.getSoundVolume()).thenReturn(1.0f);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED, true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setSoundVolume(1.0f);
        }

        @Test
        void keyPressed_shouldHandleSettingsVolumeMinLimit() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.SETTINGS);
            when(testGameLogic.getSoundVolume()).thenReturn(0.0f);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED, true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setSoundVolume(0.0f);
        }

        @Test
        void keyPressed_shouldHandleSettingsSoundToggle() {
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.SETTINGS);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).toggleSound();
        }

        @Test
        void keyPressed_shouldInitiateMinigameWhenNearOvenAndNotReadyAndNotActive() {
            // Arrange
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(true);
            when(testGameLogic.getPizzaManager().isReadyToTakeOut()).thenReturn(false);
            when(testGameLogic.isBakingMinigameActive()).thenReturn(false); // Minigame NOT active
            when(testGameLogic.getPizzaManager().hasPizzasInQueue()).thenReturn(true);
            when(testGameLogic.getPizzaManager().isCooking()).thenReturn(false);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            // Act
            testKeyHandler.keyPressed(keyEvent);

            // Assert
            verify(testGameLogic).startCooking(); // Verifies initiation is triggered
            verify(testGameLogic, never()).attemptMinigameHit();
            verify(testGameLogic.getPizzaManager(), never()).takeOutPizza();
        }
        
        @Test
        void keyPressed_shouldAttemptMinigameHitWhenNearOvenAndNotReadyAndActive() {
            // Arrange
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(true);
            when(testGameLogic.getPizzaManager().isReadyToTakeOut()).thenReturn(false);
            when(testGameLogic.isBakingMinigameActive()).thenReturn(true); // Minigame IS active
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            // Act
            testKeyHandler.keyPressed(keyEvent);

            // Assert
            verify(testGameLogic).attemptMinigameHit(); // Verifies hit attempt is triggered
            verify(testGameLogic, never()).startCooking();
            verify(testGameLogic.getPizzaManager(), never()).takeOutPizza();
        }
        
        @Test
        void keyPressed_shouldTakeOutPizzaWhenNearOvenAndReady() {
             // Arrange
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(true);
            when(testGameLogic.getPizzaManager().isReadyToTakeOut()).thenReturn(true); // Pizza IS ready
            // Minigame active state doesn't matter if pizza is ready
            when(testGameLogic.isBakingMinigameActive()).thenReturn(false); // Or true, outcome is same
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);

            // Act
            testKeyHandler.keyPressed(keyEvent);

            // Assert
            verify(testGameLogic.getPizzaManager()).takeOutPizza(); // Verifies take out is triggered
            verify(testGameLogic, never()).startCooking();
            verify(testGameLogic, never()).attemptMinigameHit();
        }
        
        @Test
        void keyPressed_shouldDoNothingWithEIfNotNearOven() {
            // Arrange
            when(testGameLogic.isPlayerNearOvenTile()).thenReturn(false); // NOT near oven
             // Other states don't matter
            when(testGameLogic.getPizzaManager().isReadyToTakeOut()).thenReturn(false);
            when(testGameLogic.isBakingMinigameActive()).thenReturn(false);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);
            
            // Act
            testKeyHandler.keyPressed(keyEvent);
            
            // Assert
            verify(testGameLogic.getPizzaManager(), never()).takeOutPizza();
            verify(testGameLogic, never()).startCooking();
            verify(testGameLogic, never()).attemptMinigameHit();
        }
    }

    /**
     * Tests that the KeyHandler is properly initialized with default values.
     * All movement flags should be false when created.
     */
    @Test
    void constructor_initializesWithCorrectDefaultValues() {
        KeyHandler newHandler = new KeyHandler(gameLogic);

        assertAll(
            "All movement flags should be false initially",
            () -> assertFalse(newHandler.isUpPressed(), "upPressed should be false"),
            () -> assertFalse(newHandler.isDownPressed(), "downPressed should be false"),
            () -> assertFalse(newHandler.isLeftPressed(), "leftPressed should be false"),
            () -> assertFalse(newHandler.isRightPressed(), "rightPressed should be false")
        );
    }

    @Nested
    class MenuNavigationTests {
        private GameLogic testGameLogic;
        private KeyHandler testKeyHandler;
        private Component testComponent;

        @BeforeEach
        void setUp() {
            testGameLogic = mock(GameLogic.class);
            testComponent = mock(Component.class);
            testKeyHandler = new KeyHandler(testGameLogic);
            
            GameState gameState = mock(GameState.class);
            MenuState menuState = mock(MenuState.class);
            
            when(testGameLogic.getGameState()).thenReturn(gameState);
            when(gameState.getMenuState()).thenReturn(menuState);
            when(gameState.getCurrentState()).thenReturn(GameState.State.MENU);
            
            String[] options = new String[]{"Start", "Options", "Quit"};
            when(menuState.getCurrentOptions(any())).thenReturn(options);
            when(menuState.getCurrentSelection()).thenReturn(0);
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.MENU);
        }

        @Test
        void keyPressed_upArrowShouldDecrementMenuSelection() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, KeyEvent.CHAR_UNDEFINED, true);
            testKeyHandler.keyPressed(keyEvent);
            
            verify(menuState).decrementSelection(any());
        }

        @Test
        void keyPressed_downArrowShouldIncrementMenuSelection() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_S, KeyEvent.CHAR_UNDEFINED, true);
            testKeyHandler.keyPressed(keyEvent);
            
            verify(menuState).incrementSelection(any());
        }

        @Test
        void keyPressed_menuNavigationShouldWrapAround() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            // Press up arrow at top position
            when(menuState.getCurrentSelection()).thenReturn(0);
            KeyEvent keyEventUp = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, KeyEvent.CHAR_UNDEFINED, true);
            testKeyHandler.keyPressed(keyEventUp);
            verify(menuState).decrementSelection(any());

            // Press down arrow at bottom position
            when(menuState.getCurrentSelection()).thenReturn(DEFAULT_MENU_OPTIONS - 1);
            KeyEvent keyEventDown = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_S, KeyEvent.CHAR_UNDEFINED, true);
            testKeyHandler.keyPressed(keyEventDown);
            verify(menuState).incrementSelection(any());
        }

        @Test
        void keyPressed_enterInMenuPosition0ShouldStartGame() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.MENU);
            when(menuState.getCurrentSelection()).thenReturn(0);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setState(GameState.State.PLAY);
        }

        @Test
        void keyPressed_enterInPausePosition0ShouldResumeGame() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PAUSE);
            when(menuState.getCurrentSelection()).thenReturn(0);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic).setState(GameState.State.PLAY);
        }

        @Test
        void keyPressed_enterInMenuPosition1ShouldNotChangeState() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(menuState.getCurrentSelection()).thenReturn(1);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic, never()).setState(any());
        }

        @Test
        void keyPressed_enterInMenuPosition2ShouldNotChangeState() {
            GameState gameState = testGameLogic.getGameState();
            MenuState menuState = gameState.getMenuState();
            
            when(menuState.getCurrentSelection()).thenReturn(2);
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);

            testKeyHandler.keyPressed(keyEvent);

            verify(testGameLogic, never()).setState(any());
        }
    }
    
    /**
     * Tests for the About menu functionality.
     */
    @Nested
    class AboutMenuTests {
        private GameLogic testGameLogic;
        private KeyHandler testKeyHandler;
        private Component testComponent;
        private RenderManager renderManager;

        @BeforeEach
        void setUp() {
            testGameLogic = mock(GameLogic.class);
            testComponent = mock(Component.class);
            testKeyHandler = new KeyHandler(testGameLogic);
            
            GameState gameState = mock(GameState.class);
            MenuState menuState = mock(MenuState.class);
            
            when(testGameLogic.getGameState()).thenReturn(gameState);
            when(gameState.getMenuState()).thenReturn(menuState);
            when(gameState.getCurrentState()).thenReturn(GameState.State.ABOUT);
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.ABOUT);
            
            // Set up GameController and RenderManager
            GameController gameController = mock(GameController.class);
            renderManager = mock(RenderManager.class);
            when(gameController.renderManager()).thenReturn(renderManager);
            when(gameController.gameLogic()).thenReturn(testGameLogic);
            
            // Use reflection to set the gameController field without calling setGameController
            try {
                java.lang.reflect.Field field = KeyHandler.class.getDeclaredField("gameController");
                field.setAccessible(true);
                field.set(testKeyHandler, gameController);
            } catch (Exception e) {
                fail("Failed to set gameController field: " + e.getMessage());
            }
        }
        
        @Test
        void keyPressed_shouldHandleAboutMenuUpNavigation() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(renderManager).scrollUp();
        }
        
        @Test
        void keyPressed_shouldHandleAboutMenuDownNavigation() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_S, 'S', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(renderManager).scrollDown();
        }
        
        @Test
        void keyPressed_shouldHandleAboutMenuEscapeKey() {
            when(testGameLogic.getGameState().getPreviousState()).thenReturn(GameState.State.MENU);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED, true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(testGameLogic).setState(GameState.State.MENU);
        }
        
        @Test
        void keyPressed_shouldIgnoreUnhandledKeysInAboutMenu() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_A, 'A', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(renderManager, never()).scrollUp();
            verify(renderManager, never()).scrollDown();
        }
    }
    
    /**
     * Tests for the Store menu functionality.
     */
    @Nested
    class StoreMenuTests {
        private GameLogic testGameLogic;
        private KeyHandler testKeyHandler;
        private Component testComponent;
        private StoreRenderer storeRenderer;
        private Store store;
        private SoundManager sound;

        @BeforeEach
        void setUp() {
            testGameLogic = mock(GameLogic.class);
            testComponent = mock(Component.class);
            testKeyHandler = new KeyHandler(testGameLogic);
            
            GameState gameState = mock(GameState.class);
            MenuState menuState = mock(MenuState.class);
            
            when(testGameLogic.getGameState()).thenReturn(gameState);
            when(gameState.getMenuState()).thenReturn(menuState);
            when(gameState.getCurrentState()).thenReturn(GameState.State.PLAY);
            
            when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
            when(testGameLogic.isShopMenuOpen()).thenReturn(true);
            
            // Set up GameController and RenderManager
            GameController gameController = mock(GameController.class);
            RenderManager renderManager = mock(RenderManager.class);
            storeRenderer = mock(StoreRenderer.class);
            store = mock(Store.class);
            sound = mock(SoundManager.class);
            
            when(gameController.renderManager()).thenReturn(renderManager);
            when(renderManager.getStoreRenderer()).thenReturn(storeRenderer);
            when(testGameLogic.getStore()).thenReturn(store);
            when(testGameLogic.getSound()).thenReturn(sound);
            when(gameController.gameLogic()).thenReturn(testGameLogic);
            
            // Use reflection to set the gameController field without calling setGameController
            try {
                java.lang.reflect.Field field = KeyHandler.class.getDeclaredField("gameController");
                field.setAccessible(true);
                field.set(testKeyHandler, gameController);
            } catch (Exception e) {
                fail("Failed to set gameController field: " + e.getMessage());
            }
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuUpNavigation() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_W, 'W', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(storeRenderer).moveSelection(-1);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuDownNavigation() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_S, 'S', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(storeRenderer).moveSelection(1);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuLeftNavigation() {
            when(store.getCurrentPage()).thenReturn(1);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_A, 'A', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(store).setPage(0);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuRightNavigation() {
            when(store.getCurrentPage()).thenReturn(0);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_D, 'D', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(store).setPage(1);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuPageWrapping() {
            when(store.getCurrentPage()).thenReturn(0);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_A, 'A', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(store).setPage(1);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuPurchase() {
            when(store.purchaseItem(any())).thenReturn(true);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(store).purchaseItem(any());
            verify(testGameLogic).getSound();
            verify(sound).play(5);
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuPurchaseFailure() {
            when(store.purchaseItem(any())).thenReturn(false);
            
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ENTER, '\n', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(store).purchaseItem(any());
            verify(testGameLogic, never()).getSound();
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuEscapeKey() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED, true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(testGameLogic).toggleShopMenu();
        }
        
        @Test
        void keyPressed_shouldHandleStoreMenuEKey() {
            KeyEvent keyEvent = TestUtils.createKeyEvent(testComponent, KeyEvent.VK_E, 'E', true);
            
            testKeyHandler.keyPressed(keyEvent);
            
            verify(testGameLogic).toggleShopMenu();
        }
    }
    
    /**
     * Tests for the GameController integration.
     */
    @Test
    void setGameController_shouldUpdateGameLogicReference() {
        GameController gameController = mock(GameController.class);
        GameLogic newGameLogic = mock(GameLogic.class);
        
        when(gameController.gameLogic()).thenReturn(newGameLogic);
        
        keyHandler.setGameController(gameController);
        
        // We can't directly verify the private gameLogic field was updated,
        // but we can verify that the keyHandler still works with the new gameLogic
        when(newGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        
        KeyEvent keyEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_W, 'W', true);
        keyHandler.keyPressed(keyEvent);
        
        assertTrue(keyHandler.isUpPressed());
    }
    
    /**
     * Tests for the keyTyped method.
     */
    @Test
    void keyTyped_shouldNotThrowException() {
        KeyEvent keyEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_A, 'A', true);
        
        // This should not throw an exception
        assertDoesNotThrow(() -> keyHandler.keyTyped(keyEvent));
    }
    
    /**
     * Tests for the pizza taking out functionality.
     */
    @Test
    void keyPressed_shouldHandleTakingOutPizza() {
        GameLogic testGameLogic = mock(GameLogic.class);
        PizzaManager pizzaManager = mock(PizzaManager.class);
        when(testGameLogic.getPizzaManager()).thenReturn(pizzaManager);
        when(testGameLogic.isPlayerNearOvenTile()).thenReturn(true);
        when(pizzaManager.isReadyToTakeOut()).thenReturn(true);
        when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        
        KeyHandler testKeyHandler = new KeyHandler(testGameLogic);
        KeyEvent keyEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_E, 'E', true);
        
        testKeyHandler.keyPressed(keyEvent);
        
        verify(pizzaManager).takeOutPizza();
        verify(testGameLogic, never()).startCooking();
    }
    
    /**
     * Tests for the collision mask toggle functionality.
     */
    @Test
    void keyPressed_shouldToggleCollisionMasks() {
        GameLogic testGameLogic = mock(GameLogic.class);
        when(testGameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        
        KeyHandler testKeyHandler = new KeyHandler(testGameLogic);
        KeyEvent keyEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_C, 'C', true);
        
        testKeyHandler.keyPressed(keyEvent);
        
        verify(testGameLogic).toggleCollisionMasks();
    }

    @Test
    void shouldHandleMovementKeys() {
        // Test W key (opp)
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_W);
        keyHandler.keyPressed(mockKeyEvent);
        assertTrue(keyHandler.isUpPressed());
        keyHandler.keyReleased(mockKeyEvent);
        assertFalse(keyHandler.isUpPressed());

        // Test S key (ned)
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_S);
        keyHandler.keyPressed(mockKeyEvent);
        assertTrue(keyHandler.isDownPressed());
        keyHandler.keyReleased(mockKeyEvent);
        assertFalse(keyHandler.isDownPressed());

        // Test A key (left)
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_A);
        keyHandler.keyPressed(mockKeyEvent);
        assertTrue(keyHandler.isLeftPressed());
        keyHandler.keyReleased(mockKeyEvent);
        assertFalse(keyHandler.isLeftPressed());

        // Test D key (right)
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_D);
        keyHandler.keyPressed(mockKeyEvent);
        assertTrue(keyHandler.isRightPressed());
        keyHandler.keyReleased(mockKeyEvent);
        assertFalse(keyHandler.isRightPressed());
    }

    @Test
    void shouldHandleInteractionKey() {
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_E);
        keyHandler.keyPressed(mockKeyEvent);
        verify(gameLogic).updatePlayerMovement(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());
    }

    @Test
    void shouldHandleEscapeKey() {
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_ESCAPE);
        keyHandler.keyPressed(mockKeyEvent);
        verify(gameLogic).setState(any());
    }

    @Test
    void shouldHandleInvalidKeys() {
        when(mockKeyEvent.getKeyCode()).thenReturn(999); // Invalid key
        keyHandler.keyPressed(mockKeyEvent);
        assertFalse(keyHandler.isUpPressed());
        assertFalse(keyHandler.isDownPressed());
        assertFalse(keyHandler.isLeftPressed());
        assertFalse(keyHandler.isRightPressed());
    }

    @Test
    void shouldHandleMultipleKeys() {
        // Test W og D taster
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_W);
        keyHandler.keyPressed(mockKeyEvent);
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_D);
        keyHandler.keyPressed(mockKeyEvent);
        assertTrue(keyHandler.isUpPressed());
        assertTrue(keyHandler.isRightPressed());

        keyHandler.keyReleased(mockKeyEvent);
        when(mockKeyEvent.getKeyCode()).thenReturn(KeyEvent.VK_W);
        keyHandler.keyReleased(mockKeyEvent);
        assertFalse(keyHandler.isUpPressed());
        assertFalse(keyHandler.isRightPressed());
    }

    @Test
    void shouldHandleKeyReleaseEvents() {
        // Test W key release
        KeyEvent pressEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_W, 'W', true);
        KeyEvent releaseEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_W, 'W', false);
        
        keyHandler.keyPressed(pressEvent);
        assertTrue(keyHandler.isUpPressed());
        
        keyHandler.keyReleased(releaseEvent);
        assertFalse(keyHandler.isUpPressed());

        // Test S key release
        pressEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_S, 'S', true);
        releaseEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_S, 'S', false);
        
        keyHandler.keyPressed(pressEvent);
        assertTrue(keyHandler.isDownPressed());
        
        keyHandler.keyReleased(releaseEvent);
        assertFalse(keyHandler.isDownPressed());

        // Test A key release
        pressEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_A, 'A', true);
        releaseEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_A, 'A', false);
        
        keyHandler.keyPressed(pressEvent);
        assertTrue(keyHandler.isLeftPressed());
        
        keyHandler.keyReleased(releaseEvent);
        assertFalse(keyHandler.isLeftPressed());

        // Test D key release
        pressEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_D, 'D', true);
        releaseEvent = TestUtils.createKeyEvent(component, KeyEvent.VK_D, 'D', false);
        
        keyHandler.keyPressed(pressEvent);
        assertTrue(keyHandler.isRightPressed());
        
        keyHandler.keyReleased(releaseEvent);
        assertFalse(keyHandler.isRightPressed());
    }
}
