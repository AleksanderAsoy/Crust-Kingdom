package inf112.skeleton.model.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for MenuState class that handles menu selection and navigation.
 */
class MenuStateTest {
    private MenuState menuState;

    @BeforeEach
    void setUp() {
        menuState = new MenuState();
    }

    @Nested
    class MenuOptionsTests {
        @Test
        void shouldProvideMenuOptions() {
            String[] options = menuState.getMenuOptions();
            assertNotNull(options, "Menu options should not be null");
            assertTrue(options.length > 0, "There should be menu options available");
            assertEquals("Start", options[0], "First option should be 'Start'");
        }

        @Test
        void shouldProvidePauseOptions() {
            String[] options = menuState.getPauseOptions();
            assertNotNull(options, "Pause menu options should not be null");
            assertTrue(options.length > 0, "There should be pause menu options available");
            assertEquals("Resume", options[0], "First option should be 'Resume'");
        }

        @Test
        void shouldProvideGameOverOptions() {
            String[] options = menuState.getGameOverOptions();
            assertNotNull(options, "Game over options should not be null");
            assertTrue(options.length > 0, "There should be game over options available");
            assertEquals("Start New Game", options[0], "First option should be 'Start New Game'");
        }

        @Test
        void shouldGetCurrentOptionsForMenu() {
            String[] options = menuState.getCurrentOptions(GameState.State.MENU);
            assertArrayEquals(menuState.getMenuOptions(), options, 
                "Should return menu options for MENU state");
        }

        @Test
        void shouldGetCurrentOptionsForPause() {
            String[] options = menuState.getCurrentOptions(GameState.State.PAUSE);
            assertArrayEquals(menuState.getPauseOptions(), options, 
                "Should return pause menu options for PAUSE state");
        }

        @Test
        void shouldGetCurrentOptionsForGameOver() {
            String[] options = menuState.getCurrentOptions(GameState.State.GAME_OVER);
            assertArrayEquals(menuState.getGameOverOptions(), options, 
                "Should return game over options for GAME_OVER state");
        }

        @Test
        void shouldGetCurrentOptionsForPlay() {
            String[] options = menuState.getCurrentOptions(GameState.State.PLAY);
            assertEquals(0, options.length, 
                "Should return empty array for PLAY state");
        }
    }

    @Nested
    class SelectionManagementTests {
        @Test
        void shouldStartWithZeroSelection() {
            assertEquals(0, menuState.getCurrentSelection(), 
                "Selection should start at 0");
        }

        @Test
        void shouldSetAndGetSelection() {
            menuState.setCurrentSelection(1);
            assertEquals(1, menuState.getCurrentSelection(), 
                "Should be able to set and get selection");
        }

        @Test
        void shouldIncrementSelection() {
            String[] options = menuState.getMenuOptions();
            menuState.incrementSelection(options);
            assertEquals(1, menuState.getCurrentSelection(), 
                "Should increment selection by 1");
        }

        @Test
        void shouldDecrementSelection() {
            String[] options = menuState.getMenuOptions();
            menuState.setCurrentSelection(1);
            menuState.decrementSelection(options);
            assertEquals(0, menuState.getCurrentSelection(), 
                "Should decrement selection by 1");
        }

        @Test
        void shouldWrapAroundWhenIncrementingAtEnd() {
            String[] options = menuState.getMenuOptions();
            menuState.setCurrentSelection(options.length - 1);
            menuState.incrementSelection(options);
            assertEquals(0, menuState.getCurrentSelection(), 
                "Should wrap to 0 when incrementing from last option");
        }

        @Test
        void shouldWrapAroundWhenDecrementingAtStart() {
            String[] options = menuState.getMenuOptions();
            menuState.setCurrentSelection(0);
            menuState.decrementSelection(options);
            assertEquals(options.length - 1, menuState.getCurrentSelection(), 
                "Should wrap to last option when decrementing from 0");
        }

        @Test
        void shouldNotIncrementWithEmptyOptions() {
            String[] emptyOptions = new String[0];
            menuState.incrementSelection(emptyOptions);
            assertEquals(0, menuState.getCurrentSelection(), 
                "Should not change selection when no options available");
        }

        @Test
        void shouldNotDecrementWithEmptyOptions() {
            String[] emptyOptions = new String[0];
            menuState.decrementSelection(emptyOptions);
            assertEquals(0, menuState.getCurrentSelection(), 
                "Should not change selection when no options available");
        }
    }

    @Nested
    class ResetTests {
        @Test
        void shouldResetToInitialState() {
            menuState.setCurrentSelection(2);
            menuState.reset();
            assertEquals(0, menuState.getCurrentSelection(), 
                "Reset should set selection back to 0");
        }

        @Test
        void shouldMaintainOptionsAfterReset() {
            String[] optionsBeforeReset = menuState.getMenuOptions();
            menuState.reset();
            String[] optionsAfterReset = menuState.getMenuOptions();
            assertArrayEquals(optionsBeforeReset, optionsAfterReset, 
                "Menu options should remain unchanged after reset");
        }
    }
} 