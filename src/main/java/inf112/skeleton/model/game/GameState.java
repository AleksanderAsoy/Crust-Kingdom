package inf112.skeleton.model.game;

import java.io.Serial;
import java.io.Serializable;

/**
 * Handles the game state.
 * This includes:
 * - Current game state (MENU, PLAY, PAUSE, GAME_OVER)
 * - Menu selection via MenuState
 */
public class GameState implements Serializable {
    public enum State {
        MENU,
        PLAY,
        PAUSE,
        GAME_OVER,
        ABOUT,
        SETTINGS
    }

    @Serial
    private static final long serialVersionUID = 1L;
    private State currentState = State.MENU;
    private State previousState = State.MENU;
    private final MenuState menuState = new MenuState();

    /**
     * Gets the current game state.
     * 
     * @return The current state
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Sets the game state to the specified state.
     * If the new state is SETTINGS or ABOUT, the current state is saved as the previous state.
     * 
     * @param newState The new state to set
     */
    public void setState(State newState) {
        if (newState == State.SETTINGS || newState == State.ABOUT) {
            previousState = currentState;
        }
        this.currentState = newState;
    }

    /**
     * Gets the menu state manager.
     * 
     * @return The menu state manager
     */
    public MenuState getMenuState() {
        return menuState;
    }

    /**
     * Resets the game state to MENU and resets the menu state.
     */
    public void reset() {
        currentState = State.MENU;
        menuState.reset();
    }

    /**
     * Checks if the game is in the specified state.
     * 
     * @param state The state to check
     * @return true if the game is in the specified state, false otherwise
     */
    public boolean isInState(State state) {
        return currentState == state;
    }

    /**
     * Checks if the game is in a state where menu navigation is allowed.
     * 
     * @return true if menu navigation is allowed, false otherwise
     */
    public boolean canNavigateMenu() {
        return currentState == State.MENU || currentState == State.PAUSE;
    }

    /**
     * Gets the previous game state.
     * 
     * @return The previous state
     */
    public State getPreviousState() {
        return previousState;
    }
}