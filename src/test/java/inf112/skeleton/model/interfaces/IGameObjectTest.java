package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IGameObjectTest {
    private IGameObject gameObject;
    private BufferedImage mockImage;

    @BeforeEach
    void setUp() {
        gameObject = mock(IGameObject.class);
        mockImage = mock(BufferedImage.class);
        
        // Set up default mock behavior
        when(gameObject.getImage()).thenReturn(mockImage);
        when(gameObject.getX()).thenReturn(100);
        when(gameObject.getY()).thenReturn(200);
        when(gameObject.isSolid()).thenReturn(true);
        when(gameObject.isInteractable()).thenReturn(true);
        when(gameObject.getType()).thenReturn("wall");
    }

    @Test
    void shouldGetImage() {
        // Test that getImage returns the correct image
        BufferedImage image = gameObject.getImage();
        assertSame(mockImage, image);
    }

    @Test
    void shouldHandlePosition() {
        // Test initial position
        assertEquals(100, gameObject.getX());
        assertEquals(200, gameObject.getY());
    }

    @Test
    void shouldHandleSolidState() {
        // Test solid state
        assertTrue(gameObject.isSolid());
        
        // Test non-solid state
        when(gameObject.isSolid()).thenReturn(false);
        assertFalse(gameObject.isSolid());
    }

    @Test
    void shouldHandleInteractableState() {
        // Test interactable state
        assertTrue(gameObject.isInteractable());
        
        // Test non-interactable state
        when(gameObject.isInteractable()).thenReturn(false);
        assertFalse(gameObject.isInteractable());
    }

    @Test
    void shouldHandleType() {
        // Test object type
        assertEquals("wall", gameObject.getType());
    }
} 