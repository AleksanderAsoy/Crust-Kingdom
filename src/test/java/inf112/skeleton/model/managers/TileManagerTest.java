package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * Test suite for TileManager class.
 * Tests tile management, interactions, and map functionality.
 */
@ExtendWith(MockitoExtension.class)
class TileManagerTest {
    @Mock
    private GameConfig mockConfig;
    
    private TileManager tileManager;

    private static final int TEST_COLS = 16;
    private static final int TEST_ROWS = 12;
    
    @BeforeEach
    void setUp() {
        // Configure mock
        when(mockConfig.getMaxScreenCol()).thenReturn(TEST_COLS);
        when(mockConfig.getMaxScreenRow()).thenReturn(TEST_ROWS);
        
        // Create test image
        BufferedImage testImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        
        // Mock image loading
        try (MockedStatic<ImageLoader> imageLoader = mockStatic(ImageLoader.class)) {
            imageLoader.when(() -> ImageLoader.loadImage(anyString())).thenReturn(testImage);
            
            // Create TileManager instance
            tileManager = new TileManager(mockConfig);
        }
    }
    
    @Test
    void shouldHandleInteractableTiles() {
        // Act
        tileManager.addInteractableTile("test", 5, 5);
        
        // Assert
        assertTrue(tileManager.isTileInteractable(5, 5), "Added tile should be interactable");
        assertEquals("test", tileManager.getTileType(5, 5), "Should return correct tile type");
    }
    
    @Test
    void shouldHandleNonInteractableTiles() {
        // Assert
        assertFalse(tileManager.isTileInteractable(7, 7), "Non-added tile should not be interactable");
        assertNull(tileManager.getTileType(7, 7), "Should return null for non-interactable tile");
    }
    
    @Test
    void shouldHandleMultipleInteractableTiles() {
        // Act
        tileManager.addInteractableTile("type1", 1, 1);
        tileManager.addInteractableTile("type2", 2, 2);
        
        // Assert
        assertTrue(tileManager.isTileInteractable(1, 1), "First tile should be interactable");
        assertTrue(tileManager.isTileInteractable(2, 2), "Second tile should be interactable");
        assertEquals("type1", tileManager.getTileType(1, 1), "Should return correct type for first tile");
        assertEquals("type2", tileManager.getTileType(2, 2), "Should return correct type for second tile");
    }
    
    @Test
    void shouldGetMapTileNum() {
        // Test valid coordinates
        int tileNum = tileManager.getMapTileNum(0, 0);
        assertTrue(tileNum >= 0, "Tile number should be non-negative");
        
        // Test invalid coordinates
        assertEquals(0, tileManager.getMapTileNum(-1, -1), "Should return 0 for invalid coordinates");
        assertEquals(0, tileManager.getMapTileNum(TEST_COLS + 1, TEST_ROWS + 1), 
            "Should return 0 for out-of-bounds coordinates");
    }
    
    @Test
    void shouldGetTileImage() {
        // Test valid tile number
        assertNotNull(tileManager.getTileImage(0), "Should return image for valid tile number");
        
        // Test invalid tile number
        assertNull(tileManager.getTileImage(-1), "Should return null for invalid tile number");
        assertNull(tileManager.getTileImage(100), "Should return null for out-of-bounds tile number");
    }
} 