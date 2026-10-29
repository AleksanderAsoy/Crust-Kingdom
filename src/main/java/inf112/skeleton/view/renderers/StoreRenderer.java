package inf112.skeleton.view.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.entities.Store.StoreItem;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * StoreRenderer is responsible for rendering the store UI.
 */
public class StoreRenderer implements Store.StoreObserver {
    private static final Logger LOGGER = Logger.getLogger(StoreRenderer.class.getName());
    private static final String FALLBACK_FONT = "Futura";
    private static final String MENU_FONT_PATH = "fonts/StardewValley.ttf";
    private static final String STORE_BG_PATH = "images/StoreBG.png";
    
    private final GameConfig config;
    private final Store store;
    private Font menuFont;
    private boolean menuVisible;
    private BufferedImage storeBackground;
    private String purchaseMessage = null;
    private long purchaseMessageTime = 0;
    private static final long MESSAGE_DURATION = 1000; // 1 seconds
    private int currentSelection;

    public StoreRenderer(GameConfig config, Store store) {
        this.config = config;
        this.store = store;
        this.menuVisible = false;
        this.currentSelection = 0;
        
        // Register as observer of Store
        store.addObserver(this);
        loadFont();
        try {
            storeBackground = ImageLoader.loadImage(STORE_BG_PATH);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading store background", e);
        }
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(MENU_FONT_PATH);
            if (is == null) {
                LOGGER.warning("Font not found: " + MENU_FONT_PATH);
                menuFont = new Font(FALLBACK_FONT, Font.PLAIN, 30);
            } else {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                menuFont = font.deriveFont(30f);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading font", e);
            menuFont = new Font(FALLBACK_FONT, Font.PLAIN, 30);
        }
    }

    @Override
    public void update(Store store) {
        // Sync selection with store
        currentSelection = store.getSelectedIndex();
    }

    /**
     * Sets the menu visibility.
     * @param visible The visibility of the menu
     */
    public void setMenuVisible(boolean visible) {
        this.menuVisible = visible;
        if (visible) {
            // Sync selection with store when menu is opened
            currentSelection = store.getSelectedIndex();
        } else {
            currentSelection = 0;
        }
    }

    /**
     * Checks if the menu is visible.
     * @return The visibility of the menu
     */
    public boolean isMenuVisible() {
        return menuVisible;
    }

    /**
     * Moves the selection.
     * @param direction The direction to move the selection
     */
    public void moveSelection(int direction) {
        int currentPage = store.getCurrentPage();
        List<StoreItem> items = store.getItems();
        int newSelection = store.getSelectedIndex();
        
        int maxSelection = currentPage == 0 ? items.size() - 1 : 2; // 3 pizza types (0-2)
        newSelection = newSelection + direction;
        
        // Wrap around selection
        if (newSelection < 0) {
            newSelection = maxSelection;
        } else if (newSelection > maxSelection) {
            newSelection = 0;
        }
        
        store.setSelectedIndex(newSelection);
    }

    /**
     * Shows a purchase message.
     * @param message The message to show
     */
    public void showPurchaseMessage(String message) {
        this.purchaseMessage = message;
        this.purchaseMessageTime = System.currentTimeMillis();
    }

    /**
     * Renders the store UI.
     * @param g2 The Graphics2D object to draw on
     */
    public void render(Graphics2D g2) {
        if (!menuVisible) return;

        // Calculate menu dimensions
        int menuWidth = 600;
        int menuHeight = 500;
        int menuX = (config.getScreenWidth() - menuWidth) / 2;
        int menuY = (config.getScreenHeight() - menuHeight) / 2;

        drawBackground(g2, menuX, menuY, menuWidth, menuHeight);
        drawTitle(g2, menuX, menuY, menuWidth);
        
        if (store.getCurrentPage() == 0) {
            renderUpgradesPage(g2, menuX, menuY, menuWidth);
        } else {
            renderPizzaStorePage(g2, menuX, menuY, menuWidth);
        }

        drawPurchaseMessage(g2, menuX, menuY, menuWidth, menuHeight);
        drawDescription(g2, menuX, menuY, menuWidth, menuHeight);
        drawInstructions(g2, menuX, menuY, menuWidth, menuHeight);
    }

    private void drawBackground(Graphics2D g2, int menuX, int menuY, int menuWidth, int menuHeight) {
        // Draw semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, config.getScreenWidth(), config.getScreenHeight());

        // Draw store background
        if (storeBackground != null) {
            g2.drawImage(storeBackground, menuX, menuY, menuWidth, menuHeight, null);
        } else {
            // Fallback to solid background if image is not loaded
            g2.setColor(new Color(40, 40, 40, 160));
            g2.fillRect(menuX, menuY, menuWidth, menuHeight);
            g2.setColor(Color.WHITE);
            g2.drawRect(menuX, menuY, menuWidth, menuHeight);
        }
    }

    private void drawTitle(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        g2.setFont(menuFont.deriveFont(50f));
        String title = store.getCurrentPage() == 0 ? "UPGRADES" : "PIZZA STORE";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.setColor(Color.WHITE);
        g2.drawString(title, menuX + (menuWidth - titleWidth) / 2, menuY + 70);
    }

    private void renderUpgradesPage(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        g2.setFont(menuFont.deriveFont(30f));
        int startY = menuY + 150;
        int itemHeight = 50;
        int selectedIndex = currentSelection;

        for (int i = 0; i < store.getItems().size(); i++) {
            StoreItem item = store.getItems().get(i);
            drawUpgradeItem(g2, item, i, selectedIndex, menuX, startY + (i * itemHeight), menuWidth);
        }
    }

    private void drawUpgradeItem(Graphics2D g2, StoreItem item, int index, int selectedIndex, int menuX, int itemY, int menuWidth) {
        String itemText = (index == selectedIndex ? "> " : "  ") + item.getName();
        
        // Draw item text
        g2.setColor(index == selectedIndex ? Color.YELLOW : Color.WHITE);
        g2.drawString(itemText, menuX + 50, itemY);
        
        // Draw price
        g2.setColor(Color.WHITE);
        g2.drawString(item.getPrice() + " $", menuX + menuWidth - 250, itemY);
        
        // Draw purchased status
        if (item.isPurchased()) {
            g2.setColor(Color.WHITE);
            g2.drawString("(Purchased)", menuX + menuWidth - 150, itemY);
        }
    }

    private void renderPizzaStorePage(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        drawInventorySection(g2, menuX, menuY, menuWidth);
        drawSeparator(g2, menuX, menuY, menuWidth);
        drawPizzaStoreItems(g2, menuX, menuY, menuWidth);
    }

    private void drawInventorySection(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        // Draw inventory header
        g2.setColor(Color.WHITE);
        g2.setFont(menuFont.deriveFont(25f));
        String inventoryHeader = "INVENTORY ITEMS:";
        int headerWidth = g2.getFontMetrics().stringWidth(inventoryHeader);
        g2.drawString(inventoryHeader, menuX + (menuWidth - headerWidth) / 2, menuY + 120);
        
        // Draw inventory items
        g2.setFont(menuFont.deriveFont(20f));
        String[] pizzaTypes = {"Vegetarian", "Pepperoni", "Cheese"};
        int inventoryStartY = menuY + 150;
        int inventoryItemHeight = 30;
        
        for (int i = 0; i < pizzaTypes.length; i++) {
            drawInventoryItem(g2, pizzaTypes[i], menuX, menuWidth, inventoryStartY + (i * inventoryItemHeight));
        }
    }

    private void drawInventoryItem(Graphics2D g2, String pizzaType, int menuX, int menuWidth, int itemY) {
        int count = store.getPizzaStore().getPizzaCount(pizzaType);
        String itemText = pizzaType + " Pizza: " + count;
        
        int textWidth = g2.getFontMetrics().stringWidth(itemText);
        int itemX = menuX + (menuWidth - textWidth) / 2;
        
        g2.setColor(Color.WHITE);
        g2.drawString(itemText, itemX, itemY);
    }

    private void drawSeparator(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        g2.setColor(Color.WHITE);
        g2.drawLine(menuX + 50, menuY + 230, menuX + menuWidth - 50, menuY + 230);
    }

    private void drawPizzaStoreItems(Graphics2D g2, int menuX, int menuY, int menuWidth) {
        g2.setFont(menuFont.deriveFont(30f));
        int startY = menuY + 260;
        int itemHeight = 50;
        int selectedIndex = currentSelection;
        String[] pizzaTypes = {"Vegetarian", "Pepperoni", "Cheese"};
        
        for (int i = 0; i < pizzaTypes.length; i++) {
            drawPizzaStoreItem(g2, pizzaTypes[i], i, selectedIndex, menuX, startY + (i * itemHeight), menuWidth);
        }
    }

    private void drawPizzaStoreItem(Graphics2D g2, String pizzaType, int index, int selectedIndex, int menuX, int itemY, int menuWidth) {
        String itemText = (index == selectedIndex ? "> " : "  ") + pizzaType + " Pizza";
        
        // Draw item text
        g2.setColor(index == selectedIndex ? Color.YELLOW : Color.WHITE);
        g2.drawString(itemText, menuX + 50, itemY);
        
        // Draw price
        g2.setColor(Color.WHITE);
        g2.drawString(store.getPizzaStore().getPizzaPrice() + " $", menuX + menuWidth - 250, itemY);
    }

    private void drawPurchaseMessage(Graphics2D g2, int menuX, int menuY, int menuWidth, int menuHeight) {
        if (purchaseMessage != null && System.currentTimeMillis() - purchaseMessageTime < MESSAGE_DURATION) {
            g2.setColor(Color.GREEN);
            g2.setFont(menuFont.deriveFont(25f));
            int messageWidth = g2.getFontMetrics().stringWidth(purchaseMessage);
            g2.drawString(purchaseMessage, menuX + (menuWidth - messageWidth) / 2, menuY + menuHeight - 60);
        } else {
            purchaseMessage = null;
        }
    }

    private void drawDescription(Graphics2D g2, int menuX, int menuY, int menuWidth, int menuHeight) {
        if (store.getCurrentPage() == 0) {
            StoreItem selectedItem = store.getSelectedItem();
            if (selectedItem != null) {
                g2.setColor(Color.WHITE);
                g2.setFont(menuFont.deriveFont(20f));
                String description = selectedItem.getDescription();
                int descriptionWidth = g2.getFontMetrics().stringWidth(description);
                g2.drawString(description, menuX + (menuWidth - descriptionWidth) / 2, menuY + menuHeight - 80);
            }
        }
    }

    private void drawInstructions(Graphics2D g2, int menuX, int menuY, int menuWidth, int menuHeight) {
        g2.setColor(Color.WHITE);
        g2.setFont(menuFont.deriveFont(20f));
        String instructions = "Use W/S to navigate, A/D to switch pages, ENTER to purchase, E to exit";
        int instructionsWidth = g2.getFontMetrics().stringWidth(instructions);
        g2.drawString(instructions, menuX + (menuWidth - instructionsWidth) / 2, menuY + menuHeight - 30);
    }
} 