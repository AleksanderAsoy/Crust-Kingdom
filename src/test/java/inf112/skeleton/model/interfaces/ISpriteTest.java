package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ISpriteTest {
    private ISprite sprite;
    private BufferedImage mockImage;

    @BeforeEach
    void setUp() {
        sprite = mock(ISprite.class);
        mockImage = mock(BufferedImage.class);
        
        // Set up default mock behavior
        when(sprite.getCurrentSprite()).thenReturn(mockImage);
        when(sprite.getDirection()).thenReturn("right");
        when(sprite.getX()).thenReturn(100);
        when(sprite.getY()).thenReturn(200);
        when(sprite.isVisible()).thenReturn(true);
    }

    @Test
    void shouldGetCurrentSprite() {
        // Test that getCurrentSprite returns the correct image
        BufferedImage image = sprite.getCurrentSprite();
        assertSame(mockImage, image);
    }

    @Test
    void shouldGetDirection() {
        // Test that getDirection returns the correct direction
        assertEquals("right", sprite.getDirection());
    }

    @Test
    void shouldGetPosition() {
        // Test that getX and getY return the correct coordinates
        assertEquals(100, sprite.getX());
        assertEquals(200, sprite.getY());
    }

    @Test
    void shouldHandleVisibility() {
        // Test that isVisible returns the correct visibility state
        assertTrue(sprite.isVisible());
    }
} 