package inf112.skeleton.view.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import inf112.skeleton.controller.GameController;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.view.renderers.HUDRenderer;
import inf112.skeleton.view.renderers.MenuRenderer;
import inf112.skeleton.view.renderers.StoreRenderer;
import inf112.skeleton.view.renderers.WorldRenderer;

/**
 * Handles all rendering in the game.
 * This class coordinates the different renderers and determines what should be drawn based on the game's state.
 */
public class RenderManager {
    private final GameConfig config;
    private final GameLogic gameLogic;
    private GameController gameController;

    private final MenuRenderer menuRenderer;
    private final HUDRenderer hudRenderer;
    private final WorldRenderer worldRenderer;
    private final StoreRenderer storeRenderer;

    /**
     * Creates a new RenderManager with the specified configuration and dependencies.
     * 
     * @param config The game configuration
     * @param gameLogic The game logic instance
     * @param gameController The game controller
     */
    public RenderManager(GameConfig config, GameLogic gameLogic, GameController gameController) {
        this.config = config;
        this.gameLogic = gameLogic;
        this.gameController = gameController;

        // Initialize renderers
        this.menuRenderer = new MenuRenderer(gameLogic, config);
        this.hudRenderer = new HUDRenderer(config, gameLogic);
        this.worldRenderer = new WorldRenderer(config, gameLogic);
        this.storeRenderer = new StoreRenderer(config, gameLogic.getStore());
        
        // Connect the renderers to the game logic
        connectRenderersToGameLogic();
    }
    
    /**
     * Connects the renderers to the game logic.
     * This method sets up the necessary connections between the view and model.
     */
    private void connectRenderersToGameLogic() {
        // Update the store renderer's visibility based on the game logic
        storeRenderer.setMenuVisible(gameLogic.isShopMenuOpen());
    }

    /**
     * Returns the GameLogic for access to the game state
     */
    public GameLogic getGameLogic() {
        return gameLogic;
    }

    /**
     * Returns the GameController for access to the controller
     */
    public GameController getGameController() {
        return gameController;
    }

    public StoreRenderer getStoreRenderer() {
        return storeRenderer;
    }

    /**
     * Updates the game state
     */
    public void update() {
        // Update the store renderer's visibility
        storeRenderer.setMenuVisible(gameLogic.isShopMenuOpen());
        
        // Update the game controller
        gameController.update();
    }

    /**
     * Handles all rendering based on the game's state.
     */
    public void render(Graphics2D g2) {
        switch (gameLogic.getCurrentState()) {
            case MENU -> renderMenu(g2);
            case PLAY -> renderGame(g2);
            case GAME_OVER -> renderGameOver(g2);
            case PAUSE -> renderPause(g2);
            case ABOUT -> renderAboutScreen(g2);
            case SETTINGS -> renderSettingsScreen(g2);
            default -> renderFallback(g2);
        }
    }

    private void renderMenu(Graphics2D g2) {
        menuRenderer.drawMenu(g2, 70f, 30f);
    }

    private void renderGame(Graphics2D g2) {
        worldRenderer.render(g2);
        hudRenderer.drawMoney(g2);
        hudRenderer.drawKarmaBar(g2);
        hudRenderer.drawPizzaProgress(g2);
        hudRenderer.drawInteractionPrompt(g2);
        hudRenderer.drawDayCounter(g2, gameLogic.getDayCounter(), false);
        hudRenderer.drawPizzasOnCounter(g2, config.getTileSize());
        
        if (gameLogic.isOrderPopupVisible()) {
            hudRenderer.drawOrders(g2);
        }

        hudRenderer.drawStoreSign(g2, (6 * config.getTileSize()) + 24, 15);

        // Render store UI if open
        if (gameLogic.isShopMenuOpen()) {
            storeRenderer.render(g2);
        }
    }

    private void renderGameOver(Graphics2D g2) {
        menuRenderer.drawGameOver(g2);
    }

    private void renderPause(Graphics2D g2) {
        menuRenderer.drawPauseScreen(g2);
    }

    private void renderAboutScreen(Graphics2D g2) {
        menuRenderer.drawStaticScreen(g2, "ABOUT", new String[] {
            "Welcome to Crust Kingdom!",
            "A pizza-making adventure game.",
            "",
            "Game Controls:",
            "- WASD: Move your character",
            "- E: Interact with objects/Open shop",
            "- ESC: Pause game/Return to menu",
            "- 2: Accept customer order",
            "- 3: Reject customer order",
            "",
            "How to Play:",
            "1. Walk to the counter to see orders",
            "2. Accept orders (2) or reject them (3)",
            "3. Go to the oven (E) to make pizzas",
            "4. Deliver pizzas to customers",
            "5. Earn money to upgrade your restaurant",
            "",
            "Shop Controls (when open):",
            "- W/S: Navigate items",
            "- A/D: Switch between pages",
            "- ENTER: Purchase selected item",
            "- E/ESC: Close shop",
            "",
            "Tips:",
            "- Keep an eye on your karma meter",
            "- Visit the store to upgrade equipment",
            "- Hire robots to help with production",
            "- Manage your money wisely",
            "",
            "Press ESC to go back."
        });
    }

    private void renderSettingsScreen(Graphics2D g2) {
        menuRenderer.drawSettingsScreen(g2);
    }

    private void renderFallback(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, config.getScreenWidth(), config.getScreenHeight());
        g2.setColor(Color.WHITE);
        g2.drawString("Unknown game state", 10, 10);
    }

    /**
     * Scrolls the about screen up
     */
    public void scrollUp() {
        if (menuRenderer != null) {
            menuRenderer.scroll(true);
        }
    }

    /**
     * Scrolls the about screen down
     */
    public void scrollDown() {
        if (menuRenderer != null) {
            menuRenderer.scroll(false);
        }
    }

    /**
     * Resets the scroll position
     */
    public void resetScroll() {
        if (menuRenderer != null) {
            menuRenderer.resetScroll();
        }
    }

    /**
     * Updates the GameController reference.
     * 
     * @param gameController The new GameController
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}