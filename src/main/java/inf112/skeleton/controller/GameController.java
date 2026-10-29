package inf112.skeleton.controller;

import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.view.ui.RenderManager;

/**
 * Controller class that mediates between the model and view.
 * Handles user input and updates the model accordingly.
 */
public record GameController(GameLogic gameLogic, KeyHandler keyHandler, RenderManager renderManager) {

    /**
     * Updates the game state based on user input.
     * This method is called in each update cycle of the game loop.
     */
    public void update() {
        // Update player movement based on key input
        updatePlayerMovement();

        // Update the game logic
        gameLogic.update();
    }

    /**
     * Updates player movement based on key input.
     */
    private void updatePlayerMovement() {
        boolean upPressed = keyHandler.isUpPressed();
        boolean downPressed = keyHandler.isDownPressed();
        boolean leftPressed = keyHandler.isLeftPressed();
        boolean rightPressed = keyHandler.isRightPressed();

        gameLogic.updatePlayerMovement(upPressed, downPressed, leftPressed, rightPressed);
    }
} 