package inf112.skeleton.view.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * HUDRenderer is responsible for drawing all UI elements in the game.
 * This includes money, orders, interaction prompts, and other messages.
 */
public class HUDRenderer {
    private static final Logger LOGGER = Logger.getLogger(HUDRenderer.class.getName());
    private static final String FALLBACK_FONT = "Futura";
    
    private final GameConfig config;
    private final GameLogic gameLogic;
    private BufferedImage threeTileBackground;
    private BufferedImage orderBackground;
    private BufferedImage orderQueueBackground;
    private BufferedImage cachedOrderQueueBackground;
    private BufferedImage storeSign;
    private int lastOrderCount = 0; // Track the last order count
    private Font messageFont;
    
    // Preloaded fonts and metrics
    private Font font20;
    private Font font23;
    private FontMetrics metrics23;
    private Font promptFont;
    private FontMetrics promptMetrics;

    /**
     * Creates a new HUDRenderer.
     * 
     * @param config The game configuration with screen size
     * @param gameLogic The game logic to get the game state
     */
    public HUDRenderer(GameConfig config, GameLogic gameLogic) {
        this.config = config;
        this.gameLogic = gameLogic;
        loadResources();
        preloadFonts();
    }

    /**
     * Loads resources such as images and fonts.
     */
    private void loadResources() {
        this.orderBackground = ImageLoader.loadImage("images/4_Tile_Sign.png");
        this.messageFont = loadCustomFont("fonts/StardewValley.ttf", config.getHudFontSize());
        this.threeTileBackground = ImageLoader.loadImage("images/3_Tile_Sign.png");
        this.orderQueueBackground = ImageLoader.loadImage("images/OrderQueue.png");
        this.cachedOrderQueueBackground = null; // Initialize as null
        this.storeSign = ImageLoader.loadImage("images/3_Tile_Sign.png");
    }

    /**
     * Pre-loads all fonts and metrics we'll need
     */
    private void preloadFonts() {
        if (messageFont == null) {
            LOGGER.warning("Attempted to preload fonts before messageFont was initialized");
            return;
        }

        // Create a single temporary Graphics2D to get all metrics
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tempImage.createGraphics();
        
        try {
            // Preload all fonts we'll use
            this.font20 = messageFont.deriveFont(20f);
            this.font23 = messageFont.deriveFont(23f);
            this.promptFont = new Font(FALLBACK_FONT, Font.BOLD, 14);
            
            // Preload all metrics in a single pass
            g2.setFont(font23);
            this.metrics23 = g2.getFontMetrics();
            
            g2.setFont(promptFont);
            this.promptMetrics = g2.getFontMetrics();
        } finally {
            g2.dispose();
        }
    }

    /**
     * Loads a custom font.
     * 
     * @param path The path to the font
     * @param size The size of the font
     * @return The font object, or a fallback font if the font could not be loaded
     */
    private Font loadCustomFont(String path, float size) {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is == null) {
                LOGGER.log(Level.WARNING, "Could not find font - {0}", path);
                return new Font(FALLBACK_FONT, Font.PLAIN, 20); // Fallback
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e, () -> "Could not load font: " + path);
            return new Font(FALLBACK_FONT, Font.PLAIN, 20); // Fallback
        }
    }

    /**
     * Draws text centered in a specified area.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param text The text to draw
     * @param x The x-coordinate of the area
     * @param y The y-coordinate of the area
     * @param scaledWidth The width of the area
     * @param scaledHeight The height of the area
     */
    private void drawCenteredTextInArea(Graphics2D g2, String text, int x, int y, int scaledWidth, int scaledHeight) {
        g2.setFont(font23);
        g2.setColor(Color.DARK_GRAY);

        int textWidth = metrics23.stringWidth(text);
        int textHeight = metrics23.getHeight();

        int textX = x + (scaledWidth - textWidth) / 2;
        int textY = y + (scaledHeight - textHeight) / 2 + metrics23.getAscent() + 2;

        g2.drawString(text, textX, textY);
    }

    private void drawBackgroundWithText(Graphics2D g2, BufferedImage background, String text, int x, int y) {
        int originalWidth = background.getWidth();
        int originalHeight = background.getHeight();
        int scaledWidth = originalWidth * 3;
        int scaledHeight = originalHeight * 3;

        // Use Rectangle objects to draw the background
        java.awt.Rectangle destination = new java.awt.Rectangle(x, y, scaledWidth, scaledHeight);
        g2.drawImage(background, destination.x, destination.y, destination.width, destination.height, null);

        drawCenteredTextInArea(g2, text, x, y, scaledWidth, scaledHeight);
    }

    /**
         * Helper class to represent a rectangle with position and dimensions.
         */
        private record Rectangle(int x, int y, int width, int height) {
    }
    
    /**
     * Draws a section of an image.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param image The image to draw from
     * @param source The source area in the original image
     * @param destination The destination area on the screen
     */
    private void drawImageSection(Graphics2D g2, BufferedImage image, Rectangle source, Rectangle destination) {
        g2.drawImage(image.getSubimage(source.x, source.y, source.width, source.height), 
                    destination.x, destination.y, destination.width, destination.height, null);
    }

    /**
     * Draws centered text.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param text The text to draw
     * @param x The x-position of the text
     * @param y The y-position of the text
     * @param width The width of the area the text should be centered in
     */
    private void drawCenteredText(Graphics2D g2, String text, int x, int y, int width) {
        g2.setFont(font23);
        g2.setColor(Color.DARK_GRAY);
        int textWidth = metrics23.stringWidth(text);
        int textX = x + (width - textWidth) / 2;
        g2.drawString(text, textX, y);
    }

    /**
     * Helper method to draw text above a bar and set up the bar dimensions
     * 
     * @param g2 The Graphics2D object to draw on
     * @param text The text to draw
     * @param x The x-position of the bar
     * @param y The y-position of the bar
     * @param scaledWidth The scaled width of the container
     * @param scaledHeight The scaled height of the container
     * @return An array containing [barX, barY, barWidth, barHeight] for the bar
     */
    private int[] drawTextAndSetupBar(Graphics2D g2, String text, int x, int y, int scaledWidth, int scaledHeight) {
        // Draw text above the bar
        int textWidth = metrics23.stringWidth(text);
        int textX = x + (scaledWidth - textWidth) / 2;
        int textY = y - 5; // Position text 5 pixels above the box
        g2.setColor(Color.BLACK);
        g2.drawString(text, textX, textY);
        
        // Calculate bar dimensions
        int barWidth = scaledWidth - 40;  // Leave some margin
        int barHeight = 20;
        int barX = x + 20;  // Center the bar
        int barY = y + (scaledHeight - barHeight) / 2;  // Center vertically
        
        return new int[]{barX, barY, barWidth, barHeight};
    }

    /**
     * Calculates scaled dimensions for an image.
     * 
     * @param image The image to calculate dimensions for
     * @return An array containing [originalWidth, originalHeight, scaledWidth, scaledHeight]
     */
    private int[] calculateScaledDimensions(BufferedImage image) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();
        int scaledWidth = originalWidth * 3;
        int scaledHeight = originalHeight * 3;
        return new int[]{originalWidth, originalHeight, scaledWidth, scaledHeight};
    }

    /**
     * Draws a background for a bar with the specified color.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param bar The dimensions of the bar
     * @param color The color to use for the background
     */
    private void drawBarBackground(Graphics2D g2, BarDimensions bar, Color color) {
        g2.setColor(color);
        g2.fillRect(bar.x, bar.y, bar.width, bar.height);
    }

    private void drawProgressBar(Graphics2D g2, int x, int y, double progress) {
        // Draw background image
        int[] dimensions = drawScaledBackground(g2, orderBackground, x, y);
        int scaledWidth = dimensions[0];
        int scaledHeight = dimensions[1];
        
        // Draw cooking stage text above the bar
        g2.setFont(font23);
        String stageText;
        if (progress >= 1.0) {
            stageText = "Done";
        } else if (progress < 0.5) {
            stageText = "Raw";
        } else if (progress < 0.8) {
            stageText = "Baking";
        } else {
            stageText = "Almost Done";
        }
        
        BarDimensions bar = extractBarDimensions(drawTextAndSetupBar(g2, stageText, x, y, scaledWidth, scaledHeight));
        
        // Background for progress bar
        drawBarBackground(g2, bar, new Color(0, 0, 0, 180));  // Semi-transparent black
        
        // Progress
        // Change color based on progress
        if (progress < 0.5) {
            g2.setColor(Color.RED); // Red for first half
        } else if (progress < 0.8) {
            g2.setColor(Color.YELLOW); // Yellow for middle
        } else {
            g2.setColor(Color.GREEN); // Green for final stage
        }
        int progressWidth = (int)(bar.width * progress);
        g2.fillRect(bar.x, bar.y, progressWidth, bar.height);
        
        // Frame
        g2.setColor(Color.WHITE);
        g2.drawRect(bar.x, bar.y, bar.width, bar.height);
    }

    /**
     * Draws the karma bar.
     * 
     * @param g2 The Graphics2D object to draw on
     */
    public void drawKarmaBar(Graphics2D g2) {
        float karma = gameLogic.getKarma();
        float progress = karma / 10.0f; // Convert karma to 0-1 range

        // Position the karma bar at the bottom middle of the screen
        int[] dimensions = calculateScaledDimensions(threeTileBackground);
        int scaledWidth = dimensions[2];
        int scaledHeight = dimensions[3];

        int x = (config.getScreenWidth() - scaledWidth) / 2;
        int y = config.getScreenHeight() - scaledHeight - 15;

        // Draw karma text above the box
        g2.setFont(font23);
        String karmaText = String.format("Karma: %.1f", karma);
        int textWidth = metrics23.stringWidth(karmaText);
        int textX = x + (scaledWidth - textWidth) / 2;
        int textY = y - 5; // Position text 5 pixels above the box
        g2.setColor(Color.BLACK);
        g2.drawString(karmaText, textX, textY);

        // Draw background
        g2.drawImage(threeTileBackground, x, y, scaledWidth, scaledHeight, null);

        // Draw karma bar
        int barWidth = scaledWidth - 40;
        int barHeight = 20;
        int barX = x + 20;
        int barY = y + (scaledHeight - barHeight) / 2;

        // Background
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Progress
        Color karmaColor;
        if (progress < 0.3f) {
            karmaColor = Color.RED;
        } else if (progress < 0.7f) {
            karmaColor = Color.YELLOW;
        } else {
            karmaColor = Color.GREEN;
        }
        g2.setColor(karmaColor);
        int progressWidth = (int)(barWidth * progress);
        g2.fillRect(barX, barY, progressWidth, barHeight);

        // Border
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }

    /**
     * Draws the money balance.
     * 
     * @param g2 The Graphics2D object to draw on
     */
    public void drawMoney(Graphics2D g2) {
        String balanceText = "Balance: $" + gameLogic.getMoney();
        
        // Create Rectangle for the balance display
        int[] dimensions = calculateScaledDimensions(threeTileBackground);
        int scaledWidth = dimensions[2];
        int scaledHeight = dimensions[3];
        
        Rectangle moneyRect = new Rectangle(15, 15, scaledWidth, scaledHeight);
        
        drawBackgroundWithText(g2, threeTileBackground, balanceText, moneyRect.x, moneyRect.y);
    }

    /**
     * Creates a cached background image for the order popup.
     * This should be called when the order list changes.
     */
    private void createCachedOrderBackground(int orderCount) {
        int originalWidth = orderQueueBackground.getWidth();
        int originalHeight = orderQueueBackground.getHeight();
        int borderHeight = 3;
        int totalHeight = Math.max(50, orderCount * 38) + (borderHeight * 2);
        int contentHeight = totalHeight - (borderHeight * 2);
        
        // Create a new image with the required dimensions
        cachedOrderQueueBackground = new BufferedImage(
            originalWidth * 3,
            totalHeight * 3,
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2 = cachedOrderQueueBackground.createGraphics();
        
        // Draw top border
        Rectangle source = new Rectangle(0, 0, originalWidth, borderHeight);
        Rectangle destination = new Rectangle(0, 0, originalWidth * 3, borderHeight * 3);
        drawImageSection(g2, orderQueueBackground, source, destination);
        
        // Draw middle sections
        source = new Rectangle(0, borderHeight, originalWidth, borderHeight);
        for (int i = 0; i < contentHeight; i += borderHeight) {
            destination = new Rectangle(0, (borderHeight * 3) + i, originalWidth * 3, borderHeight * 3);
            drawImageSection(g2, orderQueueBackground, source, destination);
        }
        
        // Draw bottom border
        source = new Rectangle(0, originalHeight - borderHeight, originalWidth, borderHeight);
        destination = new Rectangle(0, (borderHeight * 3) + contentHeight, originalWidth * 3, borderHeight * 3);
        drawImageSection(g2, orderQueueBackground, source, destination);
        
        g2.dispose();
    }

    /**
     * Draws the order list.
     * 
     * @param g2 The Graphics2D object to draw on
     */
    public void drawOrders(Graphics2D g2) {
        if (!gameLogic.isOrderPopupVisible())
            return;
    
        List<String> orders = gameLogic.getOrders();
        List<Long> timers = gameLogic.getOrderTimers();
        int orderCount = orders.size();
        
        // Position in the top right corner
        int x = config.getScreenWidth() - (orderQueueBackground.getWidth() * 3) - 10;
        int y = 10; // Moved up to the top of the screen
    
        // Create or update cached background if needed
        if (cachedOrderQueueBackground == null || orderCount != lastOrderCount) {
            createCachedOrderBackground(orderCount);
            lastOrderCount = orderCount;
        }
        
        // Draw the cached background
        g2.drawImage(cachedOrderQueueBackground, x, y, null);
    
        // Draw title - use popup message if available, otherwise show "Ordrer"
        String title = "Orders:";
        String popupMessage = gameLogic.getPopupMessage();
        if (popupMessage != null) {
            title = popupMessage;
        }
        drawCenteredText(g2, title, x, y + 28, orderQueueBackground.getWidth() * 3);
    
        // Draw orders with timers
        g2.setFont(font20);
        int textY = y + 50;
        for (int i = 0; i < orderCount; i++) {
            String orderType = orders.get(i);
            long remainingTime = timers.get(i);
            String timerText = String.format("%d", (remainingTime + 999) / 1000); // Round up to nearest second
            
            // Draw order type in black
            g2.setColor(Color.BLACK);
            g2.drawString(orderType, x + 20, textY);
            
            // Draw timer in red if less than 3 seconds remaining, otherwise black
            if (remainingTime < 3000) {
                g2.setColor(Color.RED);
            } else {
                g2.setColor(Color.BLACK);
            }
            
            // Draw timer on the right side
            int timerX = x + (orderQueueBackground.getWidth() * 3) - 80;
            g2.drawString(timerText + "s", timerX, textY);
            
            textY += 30;
        }
    }

    /**
     * Draws the progress bar for pizza preparation.
     * 
     * @param g2 The Graphics2D object to draw on
     */
    public void drawPizzaProgress(Graphics2D g2) {
        // Position for the bar (bottom-left corner, original position)
        int originalHeight = orderBackground.getHeight();
        int scaledHeight = originalHeight * 3;
        int x = 20; // 20 pixels from the left edge
        int y = config.getScreenHeight() - scaledHeight - 15; // Relative to bottom edge

        if (gameLogic.isBakingMinigameActive()) {
            // Draw the minigame bar instead
            double progress = gameLogic.getMinigameProgress();
            drawMinigameBar(g2, x, y, progress);
        } else if (gameLogic.isPizzaCooking() || gameLogic.getPizzaManager().isReadyToTakeOut()) {
            // Draw the normal cooking progress bar
            double progress = gameLogic.getPizzaCookingProgress();
            drawProgressBar(g2, x, y, progress);
        }
    }
    
    /**
     * Draws the day counter.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param dayCounter The current day
     * @param rentDue If rent is due
     */
    public void drawDayCounter(Graphics2D g2, int dayCounter, boolean rentDue) {
        String dayText = "Day: " + dayCounter;
        
        int originalWidth = threeTileBackground.getWidth();
        int originalHeight = threeTileBackground.getHeight();
        int scaledWidth = originalWidth * 3;
        int scaledHeight = originalHeight * 3;

        int x = config.getScreenWidth() - scaledWidth - 15;
        int y = config.getScreenHeight() - scaledHeight - 15;

        // Create Rectangle for background
        Rectangle backgroundRect = new Rectangle(x, y, scaledWidth, scaledHeight);

        // Draw background and day text
        drawBackgroundWithText(g2, threeTileBackground, dayText, x, y);

        // If rent is due, draw extra text in red
        if (rentDue) {
            String rentText = "Rent is due!";
            g2.setFont(font23);
            g2.setColor(Color.RED);
            
            int rentTextWidth = metrics23.stringWidth(rentText);
            int rentTextX = backgroundRect.x + (backgroundRect.width - rentTextWidth) / 2;
            int textHeight = metrics23.getHeight();
            int textY = backgroundRect.y + (backgroundRect.height - textHeight) / 2 + metrics23.getAscent();
            
            g2.drawString(rentText, rentTextX, textY + 25);
        }
    }

    /**
     * Generic method for drawing interaction prompts
     * 
     * @param g2 Graphics2D object to draw on
     * @param promptBox Rectangle defining the prompt area
     * @param promptText The text to display in the prompt
     */
    private void drawPrompt(Graphics2D g2, Rectangle promptBox, String promptText) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(promptBox.x, promptBox.y, promptBox.width, promptBox.height, 10, 10);

        g2.setColor(Color.WHITE);
        g2.setFont(promptFont);
        
        int textWidth = promptMetrics.stringWidth(promptText);
        int textX = promptBox.x + (promptBox.width - textWidth) / 2;
        
        g2.drawString(promptText, textX, promptBox.y + 20);
    }

    /**
     * Draws the store interaction prompt
     */
    private void drawStorePrompt(Graphics2D g2, Rectangle promptBox) {
        drawPrompt(g2, promptBox, "E - Enter Store");
    }

    /**
     * Draws the oven interaction prompt
     */
    private void drawOvenPrompt(Graphics2D g2, Rectangle promptBox) {
        String promptText = getOvenPromptText();
        if (promptText != null) {
            drawPrompt(g2, promptBox, promptText);
        }
    }

    /**
     * Gets the appropriate text for the oven prompt based on game state
     */
    private String getOvenPromptText() {
        if (gameLogic.isBakingMinigameActive()) {
            return "Press E NOW!";
        }
        if (gameLogic.getPizzaManager().isReadyToTakeOut()) {
            return "Take out Pizza";
        }
        if (gameLogic.getPizzaManager().hasPizzasInQueue() && !gameLogic.getPizzaManager().isCooking()) {
            return "Start Cooking";
        }
        return null;
    }

    /**
     * Draws the interaction prompt.
     * 
     * @param g2 The Graphics2D object to draw on
     */
    public void drawInteractionPrompt(Graphics2D g2) {
        if (!gameLogic.isInteractionPromptVisible() || !gameLogic.isPlayerNearInteractableTile()) return;

        int boxHeight = 30;
        String promptText = gameLogic.getInteractionPromptText();
        int textWidth = promptMetrics.stringWidth("E - " + promptText);
        int boxWidth = Math.max(120, textWidth + 40); // Minimum 120px, or text width + padding

        // Get the tile type and show appropriate prompt
        int playerCol = gameLogic.getPlayer().getX() / config.getTileSize();
        int playerRow = gameLogic.getPlayer().getY() / config.getTileSize();
        String tileType = gameLogic.getTileManager().getTileType(playerCol, playerRow);

        Rectangle promptBox;
        if ("Store".equals(tileType)) {
            promptBox = new Rectangle(325, 100, boxWidth, boxHeight);
            drawStorePrompt(g2, promptBox);
        } else {
            // Use fixed position for oven prompt
            promptBox = new Rectangle(13, 100, boxWidth, boxHeight);
            drawOvenPrompt(g2, promptBox);
        }
    }

    /**
     * Draws pizzas on the counter.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param tileSize The size of a tile
     */
    public void drawPizzasOnCounter(Graphics2D g2, int tileSize) {
        List<PizzaOnCounter> pizzas = gameLogic.getPizzasOnCounter();
        for (PizzaOnCounter pizza : pizzas) {
            g2.drawImage(pizza.getImage(), pizza.getX(), pizza.getY(), tileSize, tileSize, null);
        }
    }

    /**
     * Draws the store sign above the store.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param x The x-position of the sign
     * @param y The y-position of the sign
     */
    public void drawStoreSign(Graphics2D g2, int x, int y) {
        if (storeSign == null) {
            LOGGER.warning("Store sign image is null");
            return;
        }

        int originalWidth = storeSign.getWidth();
        int originalHeight = storeSign.getHeight();
        int scaledWidth = originalWidth * 3;
        int scaledHeight = originalHeight * 3;

        // Draw background image
        g2.drawImage(storeSign, x, y, scaledWidth, scaledHeight, null);

        drawCenteredTextInArea(g2, "STORE:", x, y, scaledWidth, scaledHeight);
    }

    /**
     * Draws a background image with scaled dimensions.
     * 
     * @param g2 The Graphics2D object to draw on
     * @param background The background image to draw
     * @param x The x-position of the background
     * @param y The y-position of the background
     * @return An array containing [scaledWidth, scaledHeight]
     */
    private int[] drawScaledBackground(Graphics2D g2, BufferedImage background, int x, int y) {
        int[] dimensions = calculateScaledDimensions(background);
        int scaledWidth = dimensions[2];
        int scaledHeight = dimensions[3];
        g2.drawImage(background, x, y, scaledWidth, scaledHeight, null);
        return new int[]{scaledWidth, scaledHeight};
    }

    /**
     * Extracts bar dimensions from the drawTextAndSetupBar result.
     *
     */
    private record BarDimensions(int x, int y, int width, int height) {}

    private BarDimensions extractBarDimensions(int[] barDimensions) {
        return new BarDimensions(
            barDimensions[0], // x
            barDimensions[1], // y
            barDimensions[2], // width
            barDimensions[3]  // height
        );
    }

    private void drawMinigameBar(Graphics2D g2, int x, int y, double progress) {
        // Draw background image and get dimensions
        int[] dimensions = drawScaledBackground(g2, orderBackground, x, y);
        int scaledWidth = dimensions[0];
        int scaledHeight = dimensions[1];

        // Draw text above the bar
        g2.setFont(font23); // Use pre-loaded font
        String stageText = "Hit E!";
        
        BarDimensions bar = extractBarDimensions(drawTextAndSetupBar(g2, stageText, x, y, scaledWidth, scaledHeight));

        // Draw background for the bar
        drawBarBackground(g2, bar, new Color(50, 50, 50, 200)); // Darker background

        // Draw the success zone highlight using current values from GameLogic
        double successStart = gameLogic.getMinigameSuccessStart();
        double successEnd = gameLogic.getMinigameSuccessEnd();
        int successZoneX = bar.x + (int) (bar.width * successStart);
        int successZoneWidth = (int) (bar.width * (successEnd - successStart));
        g2.setColor(new Color(0, 255, 0, 100)); // Semi-transparent green
        g2.fillRect(successZoneX, bar.y, successZoneWidth, bar.height);

        // Draw the moving progress indicator (e.g., a small vertical line)
        int indicatorX = bar.x + (int) (bar.width * progress);
        g2.setColor(Color.WHITE);
        g2.fillRect(indicatorX - 1, bar.y - 2, 3, bar.height + 4); // Small white line
    }

    public void render(Graphics2D g2) {
        // Draw money
        drawMoney(g2);

        // Draw karma bar
        drawKarmaBar(g2);

        // Draw orders if visible
        if (gameLogic.isOrderPopupVisible()) {
            drawOrders(g2);
        }

        // Draw interaction prompt if visible
        if (gameLogic.isInteractionPromptVisible()) {
            drawInteractionPrompt(g2);
        }

        // Draw pizzas on counter
        drawPizzasOnCounter(g2, config.getTileSize());
    }
} 