package inf112.skeleton.model.game;

import java.io.Serial;
import java.io.Serializable;

/**
 * Handles the menu state and options.
 * This class manages:
 * - Current menu selection
 * - Available menu options for different game states (menu, pause, game over)
 * - Navigation through menu options
 */
public class MenuState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String START = "Start";
    private static final String SETTINGS = "Settings";
    private static final String ABOUT = "About";
    private static final String RESUME = "Resume";
    private static final String START_NEW_GAME = "Start New Game";

    private final String[] menuOptions = {START, SETTINGS, ABOUT};
    private final String[] pauseOptions = {RESUME, SETTINGS, ABOUT, START_NEW_GAME};
    private final String[] gameOverOptions = {START_NEW_GAME, SETTINGS, ABOUT};
    private int currentSelection = 0;

    /**
     * Increments the current selection index, wrapping around to 0 if at the end.
     * 
     * @param currentOptions The array of options to navigate through
     */
    public void incrementSelection(String[] currentOptions) {
        currentSelection++;
        if (currentSelection >= currentOptions.length) {
            currentSelection = 0;
        }
    }

    /**
     * Decrements the current selection index, wrapping around to the end if at 0.
     * 
     * @param options The array of options to navigate through
     */
    public void decrementSelection(String[] options) {
        if (options.length > 0) {
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection = options.length - 1;
            }
        }
    }

    /**
     * Gets the current selection index.
     * 
     * @return The current selection index
     */
    public int getCurrentSelection() {
        return currentSelection;
    }

    /**
     * Sets the current selection index.
     * 
     * @param selection The new selection index
     */
    public void setCurrentSelection(int selection) {
        this.currentSelection = selection;
    }

    /**
     * Gets the menu options for the main menu.
     * 
     * @return The menu options
     */
    public String[] getMenuOptions() {
        return menuOptions;
    }

    /**
     * Gets the menu options for the pause menu.
     * 
     * @return The pause menu options
     */
    public String[] getPauseOptions() {
        return pauseOptions;
    }

    /**
     * Gets the menu options for the game over menu.
     * 
     * @return The game over menu options
     */
    public String[] getGameOverOptions() {
        return gameOverOptions;
    }

    /**
     * Gets the menu options for the specified game state.
     * 
     * @param state The game state
     * @return The menu options for the specified state
     */
    public String[] getCurrentOptions(GameState.State state) {
        return switch (state) {
            case MENU -> menuOptions;
            case PAUSE -> pauseOptions;
            case GAME_OVER -> gameOverOptions;
            default -> new String[0];
        };
    }

    /**
     * Resets the current selection to 0.
     */
    public void reset() {
        currentSelection = 0;
    }

}