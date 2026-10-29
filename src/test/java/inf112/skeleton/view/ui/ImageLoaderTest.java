package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Test suite for ImageLoader class.
 * Tests image loading functionality and fallback behavior.
 */
class ImageLoaderTest {
    
    @Test
    void shouldLoadDefaultImageWhenPathNotFound() {
        // Act
        BufferedImage result = ImageLoader.loadImage("non_existent_image.png");
        
        // Assert
        assertNotNull(result, "Should return default image instead of null");
        assertEquals(16, result.getWidth(), "Default image should be 16x16");
        assertEquals(16, result.getHeight(), "Default image should be 16x16");
    }
    
    @Test
    void shouldCreateDefaultImage() {
        // Act
        BufferedImage defaultImage = ImageLoader.getDefaultImage();
        
        // Assert
        assertNotNull(defaultImage, "Default image should not be null");
        assertEquals(16, defaultImage.getWidth(), "Default image should be 16x16");
        assertEquals(16, defaultImage.getHeight(), "Default image should be 16x16");
        assertEquals(BufferedImage.TYPE_INT_RGB, defaultImage.getType(), 
            "Default image should be RGB type");
    }
    
    @Test
    void shouldLoadValidImage() {
        // Act
        BufferedImage result = ImageLoader.loadImage("tiles/Floor.png");
        
        // Assert
        assertNotNull(result, "Should successfully load valid image");
    }
    
    @Test
    void shouldHandleNullPath() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            BufferedImage result = ImageLoader.loadImage(null);
            assertNotNull(result, "Should return default image for null path");
        });
    }
    
    @Test
    void shouldHandleEmptyPath() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            BufferedImage result = ImageLoader.loadImage("");
            assertNotNull(result, "Should return default image for empty path");
        });
    }
} 