package inf112.skeleton.model.entities;

import java.io.Serial;
import java.io.Serializable;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;

/**
 * Represents the player character in the game.
 * Handles player movement and state.
 */
public class Player extends AbstractEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final GameConfig config;
    private GameLogic gameLogic;
    private boolean moved = false;

    /**
     * Creates a new player.
     *
     * @param config game configuration
     * @param gameLogic game logic to handle game state
     */
    public Player(GameConfig config, GameLogic gameLogic) {
        this.config = config;
        this.gameLogic = gameLogic;
        setDefaultValues();
    }

    /**
     * Sets the game logic reference.
     * @param gameLogic the game logic instance
     */
    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    /**
     * Sets the default values for the player's position, speed and direction.
     */
    public void setDefaultValues() {
        x = 75;
        y = 200;
        speed = 4;
        direction = "down";
        width = config.getTileSize();
        height = config.getTileSize();
    }

    /**
     * Updates the player's position and animation based on movement direction.
     */
    @Override
    public void update() {
        if (moved) {
            updateAnimation();
            moved = false;
        }
    }

    /**
     * Updates the player's position based on movement input.
     * @param upPressed whether the up key is pressed
     * @param downPressed whether the down key is pressed
     * @param leftPressed whether the left key is pressed
     * @param rightPressed whether the right key is pressed
     */
    public void updateMovement(boolean upPressed, boolean downPressed, boolean leftPressed, boolean rightPressed) {
        moved = false;
        
        if (upPressed && y - speed >= 2.3 * config.getTileSize()) {
            direction = "up";
            y -= speed;
            moved = true;
        } else if (downPressed && y + speed <= config.getScreenHeight() - config.getTileSize()) {
            direction = "down";
            y += speed;
            moved = true;
        } else if (leftPressed && x - speed >= 0) {
            direction = "left";
            x -= speed;
            moved = true;
        } else if (rightPressed && x + speed <= config.getScreenWidth() - config.getTileSize()) {
            direction = "right";
            x += speed;
            moved = true;
        }
        
        if (moved) {
            updateAnimation();
        }
    }

    public int getMoney() {
        return gameLogic.getMoney();
    }

    public void spendMoney(int amount) {
        gameLogic.spendMoney(amount);
        if (gameLogic.getMoney() < 0) {
            gameLogic.handlePlayerBankruptcy();
        }
    }

    public void addMoney(int amount) {
        gameLogic.addMoney(amount);
    }
}
