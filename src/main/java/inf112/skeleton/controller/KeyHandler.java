package inf112.skeleton.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.game.GameState;
import inf112.skeleton.model.game.MenuState;
import inf112.skeleton.view.renderers.StoreRenderer;

/**
 * Handles keyboard input for the game.
 * Implements the KeyListener interface to process keyboard events.
 */
public class KeyHandler implements KeyListener {
    private GameLogic gameLogic;
    private GameController gameController;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    /**
     * Creates a new KeyHandler with the specified GameLogic.
     * 
     * @param gameLogic The game logic to handle input for
     */
    public KeyHandler(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    /**
     * Sets the GameController reference.
     * 
     * @param gameController The game controller
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
        this.gameLogic = gameController.gameLogic();
    }

    /**
     * @return true if the up key is pressed, otherwise false
     */
    public boolean isUpPressed() {
        return upPressed;
    }

    /**
     * @return true if the down key is pressed, otherwise false
     */
    public boolean isDownPressed() {
        return downPressed;
    }

    /**
     * @return true if the left key is pressed, otherwise false
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     * @return true if the right key is pressed, otherwise false
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * Resets all movement variables to false.
     */
    private void resetMovement() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }

    /**
     * Handles key typed events.
     * This method is intentionally empty as the game only uses keyPressed and keyReleased events.
     * 
     * @param e The key event
     */
    @Override
    public void keyTyped(KeyEvent e) {
        /*
         * This method should be empty.
         * The game only uses keyPressed and keyReleased to handle keyboard input.
         */
    }

    /**
     * Handles key pressed events.
     * Processes keyboard input based on the current game state.
     * 
     * @param e The key event
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        GameState.State currentState = gameLogic.getCurrentState();

        // Handle store menu input if open, regardless of game state
        if (gameLogic.isShopMenuOpen()) {
            handleStoreInput(code);
            return;
        }

        if (code == KeyEvent.VK_ESCAPE) {
            handleEscapeKey();
            return;
        }

        if (currentState == GameState.State.SETTINGS) {
            handleSettingsInput(code);
            return;
        }

        if (currentState == GameState.State.ABOUT) {
            handleAboutInput(code);
            return;
        }

        if (currentState == GameState.State.PLAY) {
            handlePlayStateInput(code);
        } else if (currentState == GameState.State.MENU || currentState == GameState.State.PAUSE
                || currentState == GameState.State.GAME_OVER) {
            handleMenuStateInput(code);
        }
    }

    /**
     * Handles the ESC key to toggle between game and pause state.
     */
    private void handleEscapeKey() {
        resetMovement(); // Reset movement when leaving settings/about

        GameState.State currentState = gameLogic.getCurrentState();

        if (currentState == GameState.State.SETTINGS || currentState == GameState.State.ABOUT) {
            gameLogic.setState(gameLogic.getGameState().getPreviousState());
        } else if (currentState == GameState.State.PLAY) {
            gameLogic.setState(GameState.State.PAUSE);
        } else if (currentState == GameState.State.PAUSE) {
            gameLogic.setState(GameState.State.PLAY);
        }
    }

    /**
     * Handles keyboard input when the game is in the play state.
     * 
     * @param code The key code to handle
     */
    private void handlePlayStateInput(int code) {
        // Handle movement keys
        handleMovementKeys(code);

        // Handle interaction keys
        handleInteractionKeys(code);

        // Handle order keys
        handleOrderKeys(code);

        // Update player movement
        gameLogic.updatePlayerMovement(upPressed, downPressed, leftPressed, rightPressed);
    }

    /**
     * Handles movement keys (WASD).
     * 
     * @param code The key code to handle
     */
    private void handleMovementKeys(int code) {
        switch (code) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_C -> gameLogic.toggleCollisionMasks(); // Toggle collision mask visualization
            default -> {
                // No handling for other keys, this is an intentional empty case
            }
        }
    }

    /**
     * Handles interaction keys (E).
     * 
     * @param code The key code to handle
     */
    private void handleInteractionKeys(int code) {
        // Early return if the key is not E
        if (code != KeyEvent.VK_E) {
            return;
        }

        // Handle store interaction
        if (gameLogic.isShopMenuOpen()) {
            gameLogic.toggleShopMenu();
            return;
        }

        // Check for shop interaction
        if (gameLogic.isPlayerNearShop()) {
            gameLogic.toggleShopMenu();
            return;
        }

        // Only handle oven interaction if player is near oven tile
        if (gameLogic.isPlayerNearOvenTile()) {
            if (gameLogic.getPizzaManager().isReadyToTakeOut()) {
                // Take out pizza if ready
                gameLogic.getPizzaManager().takeOutPizza();
            } else if (gameLogic.isBakingMinigameActive()) {
                // If minigame is active, attempt a hit
                gameLogic.attemptMinigameHit();
            } else if (gameLogic.getPizzaManager().hasPizzasInQueue() && !gameLogic.getPizzaManager().isCooking()) {
                // Only start cooking if there are pizzas in queue and not already cooking
                gameLogic.startCooking();
            }
        }
    }

    /**
     * Handles order keys (2, 3).
     * 
     * @param code The key code to handle
     */
    private void handleOrderKeys(int code) {
        // Handle order acceptance (2) and rejection (3)
        if (gameLogic.isOrderPopupVisible() && code == KeyEvent.VK_2) {
            boolean result = gameLogic.processOrder(true);
            if (result) {
                gameLogic.addPizzaToQueue();
            }
        } else if (gameLogic.isOrderPopupVisible() && code == KeyEvent.VK_3) {
            gameLogic.processOrder(false);
        }
    }

    /**
     * Handles keyboard input when the game is in menu or pause state.
     * 
     * @param code The key code to handle
     */
    private void handleMenuStateInput(int code) {
        MenuState menuState = gameLogic.getGameState().getMenuState();
        GameState.State currentState = gameLogic.getGameState().getCurrentState();

        switch (code) {
            case KeyEvent.VK_W -> menuState.decrementSelection(menuState.getCurrentOptions(currentState));
            case KeyEvent.VK_S -> menuState.incrementSelection(menuState.getCurrentOptions(currentState));
            case KeyEvent.VK_ENTER -> {
                String selectedOption = menuState.getCurrentOptions(currentState)[menuState.getCurrentSelection()];

                switch (selectedOption) {
                    case "Start", "Start New Game" -> {
                        gameLogic.resetGame();
                        gameLogic.setState(GameState.State.PLAY);
                    }
                    case "Resume" -> gameLogic.setState(GameState.State.PLAY);
                    case "Settings" -> gameLogic.setState(GameState.State.SETTINGS);
                    case "About" -> gameLogic.setState(GameState.State.ABOUT);
                    default -> { /* No handling for unknown menu options */ }
                }
            }
            default -> {
                // No handling for other keys, this is an intentional empty case
            }
        }
    }

    private void handleSettingsInput(int code) {
        switch (code) {
            case KeyEvent.VK_ENTER -> gameLogic.toggleSound();
            case KeyEvent.VK_LEFT -> {
                float currentVolume = gameLogic.getSoundVolume();
                gameLogic.setSoundVolume(Math.max(0.0f, currentVolume - 0.1f));
            }
            case KeyEvent.VK_RIGHT -> {
                float currentVolume = gameLogic.getSoundVolume();
                gameLogic.setSoundVolume(Math.min(1.0f, currentVolume + 0.1f));
            }
            default -> {
                // No handling for other keys
            }
        }
    }

    private void handleStoreInput(int code) {
        if (!gameLogic.isShopMenuOpen()) return;

        StoreRenderer storeRenderer = gameController.renderManager().getStoreRenderer();
        int currentPage = gameLogic.getStore().getCurrentPage();
        int newPage;

        switch (code) {
            case KeyEvent.VK_W:
                storeRenderer.moveSelection(-1);
                break;
            case KeyEvent.VK_S:
                storeRenderer.moveSelection(1);
                break;
            case KeyEvent.VK_A:
                // Switch to previous page (loop)
                newPage = (currentPage - 1 + 2) % 2; // Add 2 and mod 2 to handle negative numbers
                gameLogic.getStore().setPage(newPage);
                break;
            case KeyEvent.VK_D:
                // Switch to next page (loop)
                newPage = (currentPage + 1) % 2; // Mod 2 to loop between 0 and 1
                gameLogic.getStore().setPage(newPage);
                break;
            case KeyEvent.VK_ENTER:
                if (gameLogic.getStore().purchaseItem(gameLogic.getPlayer())) {
                    // Play purchase sound without stopping background music
                    gameLogic.getSound().play(5); // Play preloaded store sound
                }
                break;
            case KeyEvent.VK_ESCAPE, KeyEvent.VK_E:  // Add E key to close store
                gameLogic.toggleShopMenu();
                break;
            default:
                // No handling for other keys
                break;
        }
    }

    /**
     * Handles keyboard input when in the about menu state.
     * 
     * @param code The key code to handle
     */
    private void handleAboutInput(int code) {
        switch (code) {
            case KeyEvent.VK_W:
                gameController.renderManager().scrollUp();
                break;
            case KeyEvent.VK_S:
                gameController.renderManager().scrollDown();
                break;
            default:
                // No handling for other keys
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        
        switch (code) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_ESCAPE -> resetMovement();
            default -> {
                // No handling for other keys, this is an intentional empty case
            }
        }
        
        // Update player movement when keys are released
        if (gameLogic.getCurrentState() == GameState.State.PLAY) {
            gameLogic.updatePlayerMovement(upPressed, downPressed, leftPressed, rightPressed);
        }
    }
}
