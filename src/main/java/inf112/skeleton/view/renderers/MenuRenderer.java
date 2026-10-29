package inf112.skeleton.view.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;

/**
 * Renders the menu screen.
 */
public class MenuRenderer {
    private static final Logger logger = Logger.getLogger(MenuRenderer.class.getName());
    private static final String MENU_FONT_PATH = "fonts/StardewValley.ttf"; // Font path
    private static final String FALLBACK_FONT = "Futura"; // Fallback font name
    private Font menuFont;

    private final GameLogic gameLogic;
    private final GameConfig config;
    
    // Scrolling variables
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private static final int SCROLL_SPEED = 20;
    private static final int VISIBLE_LINES = 15; // Number of lines visible at once

    /**
     * Constructor for MenuRenderer.
     * @param gameLogic The game logic
     * @param config The game config
     */
    public MenuRenderer(GameLogic gameLogic, GameConfig config) {
        this.gameLogic = gameLogic;
        this.config = config;
        loadFont();
    }

    /**
     * Loads the custom font for the menu.
     */
    private void loadFont() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(MENU_FONT_PATH);
            if (is == null) {
                logger.log(Level.WARNING, "Font not found: {0}", MENU_FONT_PATH);
                menuFont = new Font(FALLBACK_FONT, Font.PLAIN, 30); // Fallback font
            } else {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                menuFont = font.deriveFont(30f); // Set default font size
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e, () -> "Error loading font: " + e.getMessage());
            menuFont = new Font(FALLBACK_FONT, Font.PLAIN, 30); // Fallback font
        }
    }

    /**
     * Draws the menu screen.
     * @param g2 The Graphics2D object to draw on
     * @param titleFontSize The font size of the title
     * @param optionFontSize The font size of the options
     */
    public void drawMenu(Graphics2D g2, float titleFontSize, float optionFontSize) {
        drawMenuScreen(g2, "CRUST KINGDOM", gameLogic.getMenuOptions(), titleFontSize, optionFontSize);
    }

    /**
     * Draws the pause screen.
     * @param g2 The Graphics2D object to draw on
     */
    public void drawPauseScreen(Graphics2D g2) {
        drawMenuScreen(g2, "PAUSE", gameLogic.getPauseOptions(), 70f, 30f);
    }

    /**
     * Draws the game over screen.
     * @param g2 The Graphics2D object to draw on
     */
    public void drawGameOver(Graphics2D g2) {
        drawMenuScreen(g2, "GAME OVER", gameLogic.getGameOverOptions(), 70f, 30f);
    }

    private void drawMenuScreen(Graphics2D g2, String title, String[] options, float titleFontSize,
            float optionFontSize) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, config.getScreenWidth(), config.getScreenHeight());

        // Draw title
        g2.setColor(Color.WHITE);
        g2.setFont(menuFont.deriveFont(titleFontSize));

        int titleWidth = g2.getFontMetrics().stringWidth(title);
        int titleX = (config.getScreenWidth() - titleWidth) / 2; // Center title horizontally
        int titleY = config.getScreenHeight() / 4; // Place title near the top

        g2.drawString(title, titleX, titleY);

        // Set font for options
        g2.setFont(menuFont.deriveFont(optionFontSize));

        // Get selected menu option from GameState
        int selectedIndex = gameLogic.getGameState().getMenuState().getCurrentSelection();

        // Calculate total height of the options block
        int optionHeight = g2.getFontMetrics().getHeight();
        int totalOptionsHeight = options.length * optionHeight + (options.length - 1) * 20; // 20px spacing

        // Start Y position to center the entire options block
        int startY = (config.getScreenHeight() / 2) - (totalOptionsHeight / 2);

        // Draw each option, highlighting the selected one
        for (int i = 0; i < options.length; i++) {
            int optionWidth = g2.getFontMetrics().stringWidth(options[i]);
            int optionX = (config.getScreenWidth() - optionWidth) / 2; // Center horizontally
            int optionY = startY + (i * (optionHeight + 20)); // Position each option

            // Highlight the selected option
            if (i == selectedIndex) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> " + options[i], optionX - 20, optionY); // Add ">" for selection
            } else {
                g2.setColor(Color.WHITE);
                g2.drawString(options[i], optionX, optionY);
            }
        }
    }

    /**
     * Draws a static screen.
     * @param g2 The Graphics2D object to draw on
     * @param title The title of the screen
     * @param content The content of the screen
     */
    public void drawStaticScreen(Graphics2D g2, String title, String[] content) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, config.getScreenWidth(), config.getScreenHeight());

        // Draw title
        g2.setColor(Color.WHITE);
        g2.setFont(menuFont.deriveFont(70f));
        int titleX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 100);

        // Calculate total content height
        g2.setFont(menuFont.deriveFont(25f));
        int lineSpacing = 35;
        int totalContentHeight = content.length * lineSpacing;
        int visibleHeight = VISIBLE_LINES * lineSpacing;
        
        // Calculate max scroll offset
        maxScrollOffset = Math.max(0, totalContentHeight - visibleHeight);
        
        // Draw content with scrolling
        int textY = 180 - scrollOffset;
        for (String line : content) {
            // Only draw lines that are visible on screen
            if (textY >= 150 && textY <= config.getScreenHeight() - 50) {
                int textX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(line) / 2;
                g2.drawString(line, textX, textY);
            }
            textY += lineSpacing;
        }
        
        // Draw scroll instructions
        g2.setFont(menuFont.deriveFont(20f));
        String scrollInstructions = "Use W/S to scroll, ESC to go back";
        int instructionsX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(scrollInstructions) / 2;
        g2.drawString(scrollInstructions, instructionsX, config.getScreenHeight() - 30);
    }

    /**
     * Draws the settings screen.
     * @param g2 The Graphics2D object to draw on
     */
    public void drawSettingsScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, config.getScreenWidth(), config.getScreenHeight());

        // Draw title
        g2.setColor(Color.WHITE);
        g2.setFont(menuFont.deriveFont(70f));
        String title = "SETTINGS";
        int titleX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(title) / 2;
        g2.drawString(title, titleX, 150);

        // Draw sound settings
        g2.setFont(menuFont.deriveFont(30f));
        int startY = 250;
        int spacing = 50;

        // Sound Enabled checkbox
        String soundEnabledText = "Sound: " + (gameLogic.isSoundEnabled() ? "[X]" : "[ ]");
        int soundX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(soundEnabledText) / 2;
        g2.drawString(soundEnabledText, soundX, startY);

        // Volume slider
        int sliderY = startY + spacing;
        int sliderWidth = 300;
        int sliderHeight = 20;
        int sliderX = config.getScreenWidth() / 2 - sliderWidth / 2;

        // Draw slider background
        g2.setColor(Color.GRAY);
        g2.fillRect(sliderX, sliderY, sliderWidth, sliderHeight);

        // Draw slider fill
        g2.setColor(Color.WHITE);
        int fillWidth = (int)(sliderWidth * gameLogic.getSoundVolume());
        g2.fillRect(sliderX, sliderY, fillWidth, sliderHeight);

        // Draw slider border
        g2.setColor(Color.WHITE);
        g2.drawRect(sliderX, sliderY, sliderWidth, sliderHeight);

        // Draw volume percentage
        String volumeText = (int)(gameLogic.getSoundVolume() * 100) + "%";
        int volumeX = sliderX + sliderWidth + 20;
        g2.drawString(volumeText, volumeX, sliderY + 15);

        // Draw instructions
        g2.setFont(menuFont.deriveFont(20f));
        String instructions = "Use LEFT/RIGHT to adjust volume, ENTER to toggle sound, ESC to go back";
        int instructionsX = config.getScreenWidth() / 2 - g2.getFontMetrics().stringWidth(instructions) / 2;
        g2.drawString(instructions, instructionsX, startY + spacing * 2);
    }

    /**
     * Scrolls the content up or down
     * @param up true to scroll up, false to scroll down
     */
    public void scroll(boolean up) {
        if (up) {
            scrollOffset = Math.max(0, scrollOffset - SCROLL_SPEED);
        } else {
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + SCROLL_SPEED);
        }
    }
    
    /**
    * Resets the scroll position
     */
    public void resetScroll() {
        scrollOffset = 0;
    }
}
