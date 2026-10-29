package inf112.skeleton.app;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import inf112.skeleton.controller.GameController;
import inf112.skeleton.controller.KeyHandler;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.game.GameState;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.ObjectManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.TileManager;
import inf112.skeleton.view.ui.GamePanel;
import inf112.skeleton.view.ui.RenderManager;

/**
 * The main class for the Crust Kingdom game.
 * Sets up the game window and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        // Create the game configuration
        GameConfig config = new GameConfig();
        
        // Initialize the game logic and components
        GameState gameState = new GameState();
        TileManager tileManager = new TileManager(config);
        IOrderManager orderManager = new OrderManager();
        PizzaManager pizzaManager = PizzaManager.getInstance(orderManager);
        ObjectManager objectManager = new ObjectManager(config, pizzaManager);
        
        // Create player first
        Player player = new Player(config, null);
        
        GameLogic gameLogic = new GameLogic(
            player,
            tileManager,
            orderManager,
            objectManager,
            gameState,
            config
        );

        // Set the gameLogic reference in player
        player.setGameLogic(gameLogic);
        
        // Create the KeyHandler
        GamePanel gamePanel = getGamePanel(gameLogic, config);

        // Set up the game window
        JFrame window = setupGameWindow();
        
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
        
        // Start the game loop
        gamePanel.startGameThread();
    }

    private static GamePanel getGamePanel(GameLogic gameLogic, GameConfig config) {
        KeyHandler keyHandler = new KeyHandler(gameLogic);

        // Create the RenderManager and GameController
        RenderManager renderManager = new RenderManager(config, gameLogic, null);
        GameController gameController = new GameController(gameLogic, keyHandler, renderManager);

        // Set the gameController on the KeyHandler
        keyHandler.setGameController(gameController);

        // Update the RenderManager with the GameController
        renderManager.setGameController(gameController);

        // Create the game panel
        return new GamePanel(config, renderManager);
    }

    public static JFrame setupGameWindow() {
        JFrame window = new JFrame("Crust Kingdom");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        return window;
    }
}

