package inf112.skeleton.model.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for GameState class that handles the game's state.
 */
class GameStateTest {
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState();
    }

    @Nested
    class StateManagementTests {
        @Test
        void shouldInitializeWithMenuState() {
            assertEquals(GameState.State.MENU, gameState.getCurrentState(), 
                "The game should start in MENU state");
        }

        @Test
        void shouldChangeState() {
            gameState.setState(GameState.State.PLAY);
            assertEquals(GameState.State.PLAY, gameState.getCurrentState(), 
                "Should be able to change to PLAY state");

            gameState.setState(GameState.State.PAUSE);
            assertEquals(GameState.State.PAUSE, gameState.getCurrentState(), 
                "Should be able to change to PAUSE state");

            gameState.setState(GameState.State.GAME_OVER);
            assertEquals(GameState.State.GAME_OVER, gameState.getCurrentState(), 
                "Should be able to change to GAME_OVER state");
        }

        @Test
        void shouldResetToMenuState() {
            gameState.setState(GameState.State.PLAY);
            gameState.reset();
            assertEquals(GameState.State.MENU, gameState.getCurrentState(), 
                "Reset should set the state back to MENU");
        }
    }

    @Nested
    class StateCheckTests {
        @Test
        void shouldIdentifyPlayState() {
            gameState.setState(GameState.State.PLAY);
            assertTrue(gameState.isInState(GameState.State.PLAY), 
                "Should identify PLAY state");
            assertFalse((gameState.isInState(GameState.State.MENU)), 
                "Should not identify as MENU state");
            assertFalse((gameState.isInState(GameState.State.PAUSE)), 
                "Should not identify as PAUSE state");
        }

        @Test
        void shouldIdentifyMenuState() {
            gameState.setState(GameState.State.MENU);
            assertTrue((gameState.isInState(GameState.State.MENU)), 
                "Should identify MENU state");
            assertFalse((gameState.isInState(GameState.State.PLAY)), 
                "Should not identify as PLAY state");
            assertFalse((gameState.isInState(GameState.State.PAUSE)), 
                "Should not identify as PAUSE state");
        }

        @Test
        void shouldIdentifyPauseState() {
            gameState.setState(GameState.State.PAUSE);
            assertTrue((gameState.isInState(GameState.State.PAUSE)), 
                "Should identify PAUSE state");
            assertFalse((gameState.isInState(GameState.State.PLAY)), 
                "Should not identify as PLAY state");
            assertFalse((gameState.isInState(GameState.State.MENU)), 
                "Should not identify as MENU state");
        }

        @Test
        void shouldAllowMenuNavigationInMenuState() {
            gameState.setState(GameState.State.MENU);
            assertTrue(gameState.canNavigateMenu(), 
                "Should be able to navigate the menu when in MENU state");
        }

        @Test
        void shouldAllowMenuNavigationInPauseState() {
            gameState.setState(GameState.State.PAUSE);
            assertTrue(gameState.canNavigateMenu(), 
                "Should be able to navigate the menu when in PAUSE state");
        }

        @Test
        void shouldNotAllowMenuNavigationInPlayState() {
            gameState.setState(GameState.State.PLAY);
            assertFalse(gameState.canNavigateMenu(), 
                "Should not be able to navigate the menu when in PLAY state");
        }

        @Test
        void shouldNotAllowMenuNavigationInGameOverState() {
            gameState.setState(GameState.State.GAME_OVER);
            assertFalse(gameState.canNavigateMenu(), 
                "Should not be able to navigate the menu when in GAME_OVER state");
        }
    }

    @Nested
    class MenuStateTests {
        @Test
        void shouldHaveAccessToMenuState() {
            assertNotNull(gameState.getMenuState(), 
                "Should have access to MenuState");
        }

        @Test
        void shouldResetMenuStateOnReset() {
            MenuState menuState = gameState.getMenuState();
            menuState.setCurrentSelection(2);
            gameState.reset();
            assertEquals(0, menuState.getCurrentSelection(), 
                "Reset should reset the menu selection");
        }
    }
} 