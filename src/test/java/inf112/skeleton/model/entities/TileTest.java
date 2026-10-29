package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the Tile class.
 * Tests basic tile properties and behavior.
 */
class TileTest {
    private Tile tile;
    private BufferedImage testImage;
    
    @BeforeEach
    void setUp() {
        tile = new Tile();
        testImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
    }
    
    @Test
    void shouldCreateTileWithDefaultValues() {
        assertNull(tile.getImage(), "New tile should have null image by default");
    }
    
    @Test
    void shouldSetAndGetImage() {
        // Act
        tile.setImage(testImage);
        
        // Assert
        assertSame(testImage, tile.getImage(), "Tile should store and return the same image reference");
    }
    
    @Test
    void shouldCreateMultipleIndependentTiles() {
        // Arrange
        Tile tile1 = new Tile();
        Tile tile2 = new Tile();
        
        // Act
        tile1.setImage(testImage);
        
        // Assert
        assertNull(tile2.getImage(), "Second tile should maintain its own image reference");
        assertSame(testImage, tile1.getImage(), "First tile should have the test image");
    }
} 