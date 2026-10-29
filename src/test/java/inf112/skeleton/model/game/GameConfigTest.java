package inf112.skeleton.model.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test suite for GameConfig class which handles the game's configuration.
 * <p>
 * This test class verifies:
 * 1. Basic configuration values (tile size, scale, etc.)
 * 2. Calculated values (screen size, scaled tile size)
 * 3. Consistency between values
 */
class GameConfigTest {
    private GameConfig config;

    @BeforeEach
    void setUp() {
        config = new GameConfig();
    }

    /**
     * Tests for basic configuration values.
     * Verifies that the basic settings are correct.
     */
    @Nested
    class BasicConfigurationTests {
        /**
         * Verifies that the tile size is correctly calculated.
         * Tile size = originalTileSize(16) * scale(3) = 48
         */
        @Test
        void shouldHaveCorrectTileSize() {
            int expectedTileSize = 16 * 3; // originalTileSize * scale
            assertEquals(expectedTileSize, config.getTileSize(),
                    "Tile size should be originalTileSize(16) * scale(3)");
        }

        /**
         * Verifies that the number of columns and rows is correctly set.
         */
        @Test
        void shouldHaveCorrectGridDimensions() {
            assertEquals(16, config.getMaxScreenCol(), "Should have 16 columns");
            assertEquals(12, config.getMaxScreenRow(), "Should have 12 rows");
        }
    }

    /**
     * Tests for calculated screen values.
     * Verifies that the screen size is correctly calculated based on the base values.
     */
    @Nested
    class ScreenDimensionTests {
        /**
         * Verifies that the screen width is correctly calculated.
         * Width = tileSize(48) * maxScreenCol(16) = 768
         */
        @Test
        void shouldCalculateCorrectScreenWidth() {
            int expectedWidth = config.getTileSize() * config.getMaxScreenCol(); // 48 * 16
            assertEquals(expectedWidth, config.getScreenWidth(),
                    "Screen width should be tileSize * maxScreenCol");
        }

        /**
         * Verifies that the screen height is correctly calculated.
         * Height = tileSize(48) * maxScreenRow(12) = 576
         */
        @Test
        void shouldCalculateCorrectScreenHeight() {
            int expectedHeight = config.getTileSize() * config.getMaxScreenRow(); // 48 * 12
            assertEquals(expectedHeight, config.getScreenHeight(),
                    "Screen height should be tileSize * maxScreenRow");
        }

        /**
         * Verifies that the screen's aspect ratio is correct.
         * The ratio should be 4:3 (16:12 tiles)
         */
        @Test
        void shouldHaveCorrectAspectRatio() {
            float aspectRatio = (float) config.getScreenWidth() / config.getScreenHeight();
            assertEquals(16f / 12f, aspectRatio, 0.001f,
                    "Screen should have 4:3 aspect ratio");
        }
    }

    /**
     * Tests for consistency between configuration values.
     * Verifies that the values work logically together.
     */
    @Nested
    class ConsistencyTests {
        /**
         * Verifies that the screen size is divisible by the tile size.
         * This is important for tiles to render without artifacts.
         */
        @Test
        void shouldHaveScreenSizeDivisibleByTileSize() {
            // Using assertEquals to check that the remainder is 0
            assertEquals(0, config.getScreenWidth() % config.getTileSize(),
                    "Screen width should be divisible by tile size");
            assertEquals(0, config.getScreenHeight() % config.getTileSize(),
                    "Screen height should be divisible by tile size");
        }

        /**
         * Verifies that the number of tiles fits exactly on the screen.
         */
        @Test
        void shouldFitExactNumberOfTiles() {
            int horizontalTiles = config.getScreenWidth() / config.getTileSize();
            int verticalTiles = config.getScreenHeight() / config.getTileSize();

            assertEquals(config.getMaxScreenCol(), horizontalTiles,
                    "Horizontal tiles should match maxScreenCol");
            assertEquals(config.getMaxScreenRow(), verticalTiles,
                    "Vertical tiles should match maxScreenRow");
        }

        /**
         * Verifies that the tile size is large enough for playable resolution.
         */
        @Test
        void shouldHavePlayableTileSize() {
            assertTrue(config.getTileSize() >= 32,
                    "Tile size should be at least 32 pixels for playability");
        }
    }
}