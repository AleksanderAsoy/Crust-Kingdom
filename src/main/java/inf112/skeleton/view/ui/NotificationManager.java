package inf112.skeleton.view.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages popup notifications in the game.
 */
public class NotificationManager {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static final String MENU_FONT_PATH = "fonts/StardewValley.ttf";
    private static final String FALLBACK_FONT = "Futura";
    
    private String currentMessage;
    private long messageStartTime;
    private static final long MESSAGE_DURATION = 3000; // 3 seconds
    private Font notificationFont;
    private static final Color NOTIFICATION_COLOR = new Color(0, 0, 0);
    private BufferedImage backgroundImage;

    public NotificationManager() {
        this.currentMessage = null;
        this.messageStartTime = 0;
        loadFont();
        loadBackground();
    }

    private void loadFont() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(MENU_FONT_PATH);
            if (is == null) {
                LOGGER.warning("Font not found: " + MENU_FONT_PATH);
                notificationFont = new Font(FALLBACK_FONT, Font.BOLD, 24);
            } else {
                Font font = Font.createFont(Font.TRUETYPE_FONT, is);
                notificationFont = font.deriveFont(24f);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading font", e);
            notificationFont = new Font(FALLBACK_FONT, Font.BOLD, 24);
        }
    }

    private void loadBackground() {
        try {
            backgroundImage = ImageLoader.loadImage("images/4_Tile_Sign.png");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading notification background", e);
        }
    }

    /**
     * Shows a message.
     * @param message The message to show
     */
    public void showMessage(String message) {
        this.currentMessage = message;
        this.messageStartTime = System.currentTimeMillis();
    }

    /**
     * Gets the current message.
     * @return The current message, or null if no message is being shown
     */
    public String getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Draws the message.
     * @param g2 The Graphics2D object to draw on
     * @param screenWidth The width of the screen
     * @param screenHeight The height of the screen
     */
    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        if (currentMessage == null || System.currentTimeMillis() - messageStartTime > MESSAGE_DURATION) {
            return;
        }

        // Enable antialiasing for smoother text
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw semi-transparent dark overlay
        g2.setColor(new Color(0, 0, 0, 128)); // 128 is 50% opacity
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Set up the font and get text dimensions
        g2.setFont(notificationFont);
        Rectangle2D textBounds = g2.getFontMetrics().getStringBounds(currentMessage, g2);

        // Calculate background dimensions (4x the original size for larger appearance)
        int originalWidth = backgroundImage.getWidth();
        int originalHeight = backgroundImage.getHeight();
        int scaledWidth = originalWidth * 4;  // Increased from 3x to 4x
        int scaledHeight = originalHeight * 4; // Increased from 3x to 4x

        // Calculate center position for the background
        int backgroundX = (screenWidth - scaledWidth) / 2;
        int backgroundY = (screenHeight - scaledHeight) / 2;

        // Draw background image
        g2.drawImage(backgroundImage, backgroundX, backgroundY, scaledWidth, scaledHeight, null);

        // Draw the text centered within the sign
        g2.setColor(NOTIFICATION_COLOR);
        int textX = backgroundX + (scaledWidth - (int) textBounds.getWidth()) / 2;
        int textY = backgroundY + (scaledHeight - (int) textBounds.getHeight()) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(currentMessage, textX, textY);
    }
} 