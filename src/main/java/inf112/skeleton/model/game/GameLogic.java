package inf112.skeleton.model.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.factories.NPCFactory;
import inf112.skeleton.model.interfaces.managers.IOrderManager;
import inf112.skeleton.model.interfaces.managers.IPizzaManager;
import inf112.skeleton.model.interfaces.managers.ISoundManager;
import inf112.skeleton.model.managers.GameEconomyManager;
import inf112.skeleton.model.managers.KarmaManager;
import inf112.skeleton.model.managers.NPCManager;
import inf112.skeleton.model.managers.ObjectManager;
import inf112.skeleton.model.managers.TileManager;
import inf112.skeleton.model.orders.Order;

/**
 * Handles game logic and interactions between game objects.
 * This includes:
 * - Player updates
 * - Interaction detection
 * - State updates
 */
public class GameLogic implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Player localPlayer;
    private final TileManager tileManager;
    private final transient IOrderManager orderManager;
    private final ObjectManager objectManager;
    private final NPCManager npcManager;
    private final GameState gameState;
    private final GameEconomyManager economyManager;
    private final GameConfig config;
    private final ISoundManager sound;
    private boolean isShopMenuOpen;
    private final Store store;
    private final KarmaManager karmaManager;
    private final transient IPizzaManager pizzaManager;
    private String popupMessage;
    private long popupMessageTime;
    private static final long POPUP_DURATION = 3000; // 3 seconds
    private boolean showCollisionMasks;
    
    // Add observer list
    private final List<GameLogicObserver> observers = new ArrayList<>();
    
    // Observer interface
    public interface GameLogicObserver {
        void update(GameLogic gameLogic);
    }
    
    // Add observer methods
    public void addObserver(GameLogicObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(GameLogicObserver observer) {
        observers.remove(observer);
    }
    
    protected void notifyObservers() {
        for (GameLogicObserver observer : observers) {
            observer.update(this);
        }
    }
    
    public GameLogic(Player player, TileManager tileManager, IOrderManager orderManager,
            ObjectManager objectManager, GameState gameState, GameConfig config) {
        this.localPlayer = player;
        this.tileManager = tileManager;
        this.orderManager = orderManager;
        this.objectManager = objectManager;
        this.gameState = gameState;
        this.config = config;
        this.npcManager = new NPCManager(config, orderManager, this);
        this.economyManager = new GameEconomyManager(this);
        this.sound = new inf112.skeleton.model.managers.SoundManager();
        this.isShopMenuOpen = false;
        this.store = new Store();
        this.karmaManager = new KarmaManager();
        this.pizzaManager = IPizzaManager.getInstance(orderManager);
        
        // Connect PizzaManager with Store, Sound, and KarmaManager
        this.pizzaManager.setStore(this.store);
        this.pizzaManager.setSound(this.sound);
        this.pizzaManager.setKarmaManager(this.karmaManager);
        
        // Start menu music when game launches
        sound.loop(0); // Loop preloaded menu music
    }

    /**
     * Returns the TileManager for access to tile information.
     */
    public TileManager getTileManager() {
        return tileManager;
    }

    /**
     * Updates the game state.
     */
    public void update() {
        if (gameState.isInState(GameState.State.PLAY)) {
            localPlayer.update();
            
            // Check if karma has reached 0
            if (karmaManager.getCurrentKarma() <= 0) {
                gameState.setState(GameState.State.GAME_OVER);
                return;
            }
            
            // Check for expired orders and reject them
            List<Order> expiredOrders = orderManager.getOrders().stream()
                .filter(Order::isExpired)
                .toList();
            
            for (Order order : expiredOrders) {
                processOrder(false, order);
                karmaManager.decreaseKarma(); // Decrease karma for expired orders
            }
            
            orderManager.update();
            pizzaManager.update();
            npcManager.update();
            economyManager.updateEconomy();
            economyManager.addMoney(pizzaManager.collectMoney());
            objectManager.update();
            
            notifyObservers();
        }
    }

    /**
     * Updates player movement.
     */
    public void updatePlayerMovement(boolean upPressed, boolean downPressed, boolean leftPressed,
            boolean rightPressed) {
        // Store current position
        int oldX = localPlayer.getX();
        int oldY = localPlayer.getY();
        
        // Try to move
        localPlayer.updateMovement(upPressed, downPressed, leftPressed, rightPressed);
        
        // Check for collisions with objects
        for (GameObject obj : objectManager.getObjects()) {
            if (obj.collidesWith(localPlayer)) {
                // If collision detected, revert position
                localPlayer.setX(oldX);
                localPlayer.setY(oldY);
                break;
            }
        }
        
        notifyObservers();
    }

    /**
     * Checks if the player is near an interactive tile.
     */
    public boolean isPlayerNearInteractableTile() {
        int playerCol = localPlayer.getX() / config.getTileSize();
        int playerRow = localPlayer.getY() / config.getTileSize();
        String tileType = tileManager.getTileType(playerCol, playerRow);
        
        // Show order table when on counter tile
        if ("counter".equals(tileType)) {
            if (!orderManager.isOrderPopupVisible()) {
                orderManager.toggleOrderPopup();
            }
            return true;
        } else if (orderManager.isOrderPopupVisible()) {
            // Hide order table when walking off counter tile
            orderManager.toggleOrderPopup();
        }
        
        return tileManager.isTileInteractable(playerCol, playerRow);
    }

    /**
     * Checks if the player is near the oven.
     */
    public boolean isPlayerNearOvenTile() {
        int playerCol = localPlayer.getX() / config.getTileSize();
        int playerRow = localPlayer.getY() / config.getTileSize();
        return "oven".equals(tileManager.getTileType(playerCol, playerRow));
    }

    /**
     * Checks if the interaction prompt should be shown.
     */
    public boolean isInteractionPromptVisible() {
        if (!isPlayerNearInteractableTile()) return false;
        
        // Get the tile type
        int playerCol = localPlayer.getX() / config.getTileSize();
        int playerRow = localPlayer.getY() / config.getTileSize();
        String tileType = tileManager.getTileType(playerCol, playerRow);
        
        // For store tiles, always show the prompt
        if ("Store".equals(tileType)) {
            return true;
        }
        
        // For oven tiles, show prompt only when ready to take out or can start cooking
        if ("oven".equals(tileType)) {
            return pizzaManager.isReadyToTakeOut() || 
                   (pizzaManager.hasPizzasInQueue() && !pizzaManager.isCooking() && pizzaManager.getPizzasOnCounter().size() < 2);
        }
        
        // For counter tiles, don't show the prompt (orders are shown separately)
        if ("counter".equals(tileType)) {
            return false;
        }
        
        // For other interactable tiles
        return tileManager.isTileInteractable(playerCol, playerRow);
    }

    public String getInteractionPromptText() {
        if (!isPlayerNearInteractableTile()) return "";

        int playerCol = localPlayer.getX() / config.getTileSize();
        int playerRow = localPlayer.getY() / config.getTileSize();
        String tileType = tileManager.getTileType(playerCol, playerRow);

        if ("oven".equals(tileType)) {
            if (pizzaManager.isReadyToTakeOut()) {
                return "Take out pizza";
            }
            return "Start cooking";
        }

        return "";
    }

    /**
     * Adds a pizza to the queue.
     */
    public void addPizzaToQueue() {
        pizzaManager.addPizzaToQueue();
        notifyObservers();
    }

    /**
     * Handles an order.
     */
    public boolean processOrder(boolean accept, Order order) {
        if (orderManager.getOrders().isEmpty()) {
            return false; // No orders to process
        }

        // If no specific order is provided, use the first one in queue
        if (order == null) {
            order = orderManager.getOrders().get(0);
        }

        // Check if we have enough pizzas in inventory before accepting
        if (accept) {
            int pizzaCount = store.getPizzaStore().getPizzaCount(order.getType());
            if (pizzaCount <= 0) {
                setPopupMessage("No " + order.getType() + " left!");
                return false;
            }
        }

        // Process the order first to ensure proper cleanup
        orderManager.processOrder(accept, order);

        if (accept) {
            karmaManager.increaseKarma(); // Increase karma for accepted orders
            setPopupMessage(order.getType() + " accepted!");
            notifyObservers();
            return true;
        } else {
            // If rejected, handle NPC leaving
            for (NPC npc : npcManager.getNPCs()) {
                if (npc.getOrder() == order) {
                    npc.getMovementStrategy().rejectOrder();
                    break;
                }
            }
            setPopupMessage("Order rejected!");
            notifyObservers();
            return false;
        }
    }

    /**
     * Handles the first order in the queue.
     */
    public boolean processOrder(boolean accept) {
        return processOrder(accept, null);
    }

    /**
     * Checks if the order window is visible.
     */
    public boolean isOrderPopupVisible() {
        return orderManager.isOrderPopupVisible();
    }

    /**
     * Starts pizza cooking.
     */
    public void startCooking() {
        // Instead of directly starting cooking, initiate the minigame
        pizzaManager.initiateBakingMinigame();
        notifyObservers();
    }

    /**
     * Resets the game.
     */
    public void resetGame() {
        // Play powerup sound
        sound.stopLoop(); // Stop only the looping music
        sound.play(3); // Play preloaded powerup sound

        // Reset all game-related data
        localPlayer.setDefaultValues();
        npcManager.clearNPCs();
        economyManager.resetEconomy();
        orderManager.clearOrders();
        pizzaManager.reset();
        objectManager.reset();
        karmaManager.reset();
        
        // Reset store and close store menu
        store.reset();
        isShopMenuOpen = false;

        // Reset the robber timer and renderer
        NPCFactory.resetGameStartTime();

        gameState.setState(GameState.State.PLAY);
        notifyObservers();
    }

    public void addMoney(int amount) {
        economyManager.addMoney(amount);
        // Play coin sound
        sound.play(2); // Play preloaded coin sound
        notifyObservers();
    }

    public void spendMoney(int amount) {
        economyManager.spendMoney(amount);
        notifyObservers();
    }

    public int getMoney() {
        return economyManager.getMoney();
    }

    public void resetMoney() {
        economyManager.resetEconomy();
        notifyObservers();
    }

    public int getDayCounter() {
        return economyManager.getDayCounter();
    }

    /**
     * Gets the current state of the game.
     * 
     * @return The current game state
     */
    public GameState.State getCurrentState() {
        return gameState.getCurrentState();
    }

    /**
     * Sets the current state of the game.
     * 
     * @param state The new game state to set
     */
    public void setState(GameState.State state) {
        GameState.State currentState = gameState.getCurrentState();
        
        if (state == GameState.State.GAME_OVER) {
            sound.play(4);
        } else if (currentState == GameState.State.PLAY) {
            sound.stopLoop();
            sound.loop(1);
        } else if (currentState == GameState.State.GAME_OVER && state == GameState.State.MENU) {
            sound.loop(0);
        } else if (currentState == GameState.State.MENU && state == GameState.State.PLAY) {
            sound.loop(1);
        } else if (currentState == GameState.State.MENU || currentState == GameState.State.PAUSE) {
            sound.stopLoop();
            sound.loop(0);
        }
        
        gameState.setState(state);
        notifyObservers();
    }

    /**
     * Decrements the current menu selection.
     */
    public void decrementMenuSelection() {
        gameState.getMenuState().decrementSelection(gameState.getMenuState().getCurrentOptions(getCurrentState()));
        notifyObservers();
    }

    /**
     * Increments the current menu selection.
     */
    public void incrementMenuSelection() {
        gameState.getMenuState().incrementSelection(gameState.getMenuState().getCurrentOptions(getCurrentState()));
        notifyObservers();
    }

    /**
     * Gets the current menu selection index.
     * 
     * @return The current menu selection index
     */
    public int getCurrentMenuSelection() {
        return gameState.getMenuState().getCurrentSelection();
    }

    /**
     * Gets the available menu options.
     * 
     * @return Array of menu option strings
     */
    public String[] getMenuOptions() {
        return gameState.getMenuState().getMenuOptions();
    }

    /**
     * Gets the available pause menu options.
     * 
     * @return Array of pause menu option strings
     */
    public String[] getPauseOptions() {
        return gameState.getMenuState().getPauseOptions();
    }

    /**
     * Gets the available game over menu options.
     * 
     * @return Array of game over menu option strings
     */
    public String[] getGameOverOptions() {
        return gameState.getMenuState().getGameOverOptions();
    }

    /**
     * Gets the list of current orders.
     * 
     * @return List of order strings
     */
    public List<String> getOrders() {
        return orderManager.getOrders().stream()
                .map(Order::getType)
                .toList();
    }

    /**
     * Gets the list of order timers.
     * 
     * @return List of order timer values in milliseconds
     */
    public List<Long> getOrderTimers() {
        return orderManager.getOrders().stream()
                .map(Order::getRemainingTime)
                .toList();
    }

    /**
     * Checks if a pizza is currently cooking.
     * 
     * @return true if a pizza is cooking, false otherwise
     */
    public boolean isPizzaCooking() {
        return pizzaManager.isCooking();
    }

    /**
     * Gets the list of pizzas on the counter.
     * 
     * @return List of PizzaOnCounter objects
     */
    public List<PizzaOnCounter> getPizzasOnCounter() {
        return pizzaManager.getPizzasOnCounter();
    }

    /**
     * Gets the tile number at the specified coordinates.
     * 
     * @param col The column coordinate
     * @param row The row coordinate
     * @return The tile number at the specified coordinates
     */
    public int getTileNum(int col, int row) {
        return tileManager.getMapTileNum(col, row);
    }

    /**
     * Gets the tile image for the specified tile number.
     * 
     * @param tileNum The tile number
     * @return The BufferedImage for the specified tile
     */
    public java.awt.image.BufferedImage getTileImage(int tileNum) {
        return tileManager.getTileImage(tileNum);
    }

    /**
     * Gets the current progress of pizza cooking.
     * 
     * @return The cooking progress as a value between 0.0 and 1.0
     */
    public double getPizzaCookingProgress() {
        if (pizzaManager.isReadyToTakeOut()) {
            return 1.0;
        }
        if (pizzaManager.getCurrentPizza() != null) {
            double elapsedTime = (double)System.currentTimeMillis() - pizzaManager.getStartTime();
            double adjustedCookTime = pizzaManager.getCookTime() / pizzaManager.getCookingSpeedMultiplier();
            return elapsedTime / adjustedCookTime;
        }
        return 0;
    }

    /**
     * Toggles the visibility of collision masks.
     */
    public void toggleCollisionMasks() {
        showCollisionMasks = !showCollisionMasks;
        notifyObservers();
    }

    /**
     * Returns whether collision masks should be shown.
     */
    public boolean shouldShowCollisionMasks() {
        return showCollisionMasks;
    }

    /**
     * Checks if sound is enabled.
     * 
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return sound.isSoundEnabled();
    }

    /**
     * Gets the current sound volume.
     * 
     * @return The current sound volume
     */
    public float getSoundVolume() {
        return sound.getVolume();
    }

    /**
     * Sets the sound volume.
     * 
     * @param volume The new sound volume
     */
    public void setSoundVolume(float volume) {
        sound.setVolume(volume);
        notifyObservers();
    }

    /**
     * Toggles sound on/off.
     */
    public void toggleSound() {
        sound.setSoundEnabled(!sound.isSoundEnabled());
        notifyObservers();
    }

    /**
     * Gets the player instance.
     * 
     * @return The Player instance
     */
    public Player getPlayer() {
        return localPlayer;
    }

    /**
     * Gets the list of game objects.
     * 
     * @return List of GameObject instances
     */
    public List<GameObject> getGameObjects() {
        return objectManager.getObjects();
    }

    /**
     * Gets the game state manager.
     * 
     * @return The GameState instance
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Gets the list of NPCs.
     * 
     * @return List of NPC instances
     */
    public List<NPC> getNPCs() {
        return npcManager.getNPCs();
    }

    /**
     * Gets the economy manager.
     * 
     * @return The GameEconomyManager instance
     */
    public GameEconomyManager getEconomyManager() {
        return economyManager;
    }

    /**
     * Gets the order manager.
     * 
     * @return The OrderManager instance
     */
    public IOrderManager getOrderManager() {
        return orderManager;
    }

    /**
     * Gets the pizza manager.
     * 
     * @return The PizzaManager instance
     */
    public IPizzaManager getPizzaManager() {
        return pizzaManager;
    }

    /**
     * Checks if the player is near the shop.
     * 
     * @return true if the player is near the shop, false otherwise
     */
    public boolean isPlayerNearShop() {
        int playerCol = localPlayer.getX() / config.getTileSize();
        int playerRow = localPlayer.getY() / config.getTileSize();
        String tileType = tileManager.getTileType(playerCol, playerRow);
        return "Store".equals(tileType);
    }

    /**
     * Toggles the shop menu open/closed.
     */
    public void toggleShopMenu() {
        isShopMenuOpen = !isShopMenuOpen;
        notifyObservers();
    }

    /**
     * Checks if the shop menu is open.
     * 
     * @return true if the shop menu is open, false otherwise
     */
    public boolean isShopMenuOpen() {
        return isShopMenuOpen;
    }

    /**
     * Gets the store instance.
     * 
     * @return The Store instance
     */
    public Store getStore() {
        return store;
    }

    /**
     * Handles the game over state when the player goes bankrupt.
     * This method is called by the Player class when their money goes below zero.
     */
    public void handlePlayerBankruptcy() {
        gameState.setState(GameState.State.GAME_OVER);
        // Play fanfare sound
        sound.stopLoop();
        sound.play(4);
        notifyObservers();
    }

    public ISoundManager getSound() {
        return sound;
    }

    /**
     * Gets the current karma value.
     * 
     * @return The current karma value
     */
    public float getKarma() {
        return karmaManager.getCurrentKarma();
    }

    /**
     * Sets a popup message to be displayed.
     * 
     * @param message The message to display
     */
    public void setPopupMessage(String message) {
        this.popupMessage = message;
        this.popupMessageTime = System.currentTimeMillis();
        notifyObservers();
    }

    /**
     * Gets the current popup message.
     * 
     * @return The current popup message, or null if no message is being displayed
     */
    public String getPopupMessage() {
        if (popupMessage != null && System.currentTimeMillis() - popupMessageTime < POPUP_DURATION) {
            return popupMessage;
        }
        return null;
    }

    /**
     * Checks if the baking minigame is active.
     */
    public boolean isBakingMinigameActive() {
        return pizzaManager.isBakingMinigameActive();
    }

    /**
     * Gets the progress of the baking minigame.
     */
    public double getMinigameProgress() {
        return pizzaManager.getMinigameProgress();
    }

    /**
     * Attempts to hit the target during the baking minigame.
     * Called by KeyHandler when 'E' is pressed during the minigame.
     */
    public void attemptMinigameHit() {
        pizzaManager.attemptMinigameHit();
        notifyObservers(); // Notify observers if needed after the attempt
    }

    /**
     * Gets the start position (0.0-1.0) of the current minigame's success zone.
     */
    public double getMinigameSuccessStart() {
        return pizzaManager.getMinigameSuccessStart();
    }

    /**
     * Gets the end position (0.0-1.0) of the current minigame's success zone.
     */
    public double getMinigameSuccessEnd() {
        return pizzaManager.getMinigameSuccessEnd();
    }
}
