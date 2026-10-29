package inf112.skeleton.model.game;

import java.io.Serial;
import java.io.Serializable;

/**
 * Holds the game configuration and constants.
 * This includes screen dimensions, tile sizes, and other game constants.
 */
public class GameConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int ORIGINAL_TILE_SIZE = 16;
    private static final int SCALE = 3;
    private static final float HUD_FONT_SIZE = 20.0f;
    private final int tileSize;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private final int screenWidth;
    private final int screenHeight;

    /**
     * Creates a new GameConfig with default settings.
     * Initializes tile size, screen dimensions, and other game constants.
     */
    public GameConfig() {
        this.tileSize = ORIGINAL_TILE_SIZE * SCALE;
        this.screenWidth = tileSize * MAX_SCREEN_COL;
        this.screenHeight = tileSize * MAX_SCREEN_ROW;
    }

    /**
     * Gets the size of each tile in pixels.
     * 
     * @return The tile size in pixels
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Gets the maximum number of columns in the screen.
     * 
     * @return The maximum number of screen columns
     */
    public int getMaxScreenCol() {
        return MAX_SCREEN_COL;
    }

    /**
     * Gets the maximum number of rows in the screen.
     * 
     * @return The maximum number of screen rows
     */
    public int getMaxScreenRow() {
        return MAX_SCREEN_ROW;
    }

    /**
     * Gets the total width of the screen in pixels.
     * 
     * @return The screen width in pixels
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Gets the total height of the screen in pixels.
     * 
     * @return The screen height in pixels
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Gets the font size for HUD elements.
     * @return the font size for HUD elements
     */
    public float getHudFontSize() {
        return HUD_FONT_SIZE;
    }
} 