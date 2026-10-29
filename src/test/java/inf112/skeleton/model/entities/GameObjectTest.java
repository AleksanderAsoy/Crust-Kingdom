package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.interfaces.IEntity;

/**
 * Test suite for GameObject class.
 * <p>
 * Tests:
 * 1. Correct initialization of objects with different properties
 * 2. Interaction handling
 */
class GameObjectTest {
    private static final int X_POS = 100;
    private static final int Y_POS = 200;
    private BufferedImage mockImage;
    
    @BeforeEach
    void setUp() {
        mockImage = mock(BufferedImage.class);
    }

    /**
     * Tests that a GameObject initializes correctly with all properties.
     */
    @Test
    void shouldInitializeWithCorrectProperties() {
        // Create a game object with specific properties
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, true, true, "oven");
        
        // Verify that all properties are set correctly
        assertEquals(X_POS, gameObject.getX(), "X-position should be correct");
        assertEquals(Y_POS, gameObject.getY(), "Y-position should be correct");
        assertSame(mockImage, gameObject.getImage(), "Image should be the same as the one passed in");
        assertTrue(gameObject.isSolid(), "Object should be solid");
        assertTrue(gameObject.isInteractable(), "Object should be interactive");
        assertEquals("oven", gameObject.getType(), "Object type should be correct");
    }

    /**
     * Tests that a non-solid, non-interactive object initializes correctly.
     */
    @Test
    void shouldInitializeNonSolidNonInteractableObject() {
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, false, false, "decoration");
        
        assertFalse(gameObject.isSolid(), "Object should not be solid");
        assertFalse(gameObject.isInteractable(), "Object should not be interactive");
        assertEquals("decoration", gameObject.getType(), "Object type should be correct");
    }

    /**
     * Tests that an object can be created without an image.
     */
    @Test
    void shouldHandleNullImage() {
        GameObject gameObject = new GameObject(null, X_POS, Y_POS, true, true, "table");
        assertNull(gameObject.getImage(), "Image reference should be null");
        assertEquals("table", gameObject.getType(), "Object type should be correct even if the image is null");
    }

    /**
     * Tests that the collision mask is returned correctly.
     */
    @Test
    void shouldReturnCollisionMask() {
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, true, true, "table");
        boolean[][] mask = gameObject.getCollisionMask();
        assertNotNull(mask, "Collision mask should not be null");
    }

    /**
     * Tests collision check at specific coordinates.
     */
    @Test
    void shouldCheckCollisionAtSpecificCoordinates() {
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, true, true, "table");
        
        // Test coordinates within the object's boundaries
        boolean collisionInside = gameObject.hasCollisionAt(X_POS + 10, Y_POS + 10);
        assertFalse(collisionInside, "Should not have collision for coordinates within boundaries");
        
        // Test coordinates outside the object's boundaries
        boolean collisionOutside = gameObject.hasCollisionAt(X_POS - 100, Y_POS - 100);
        assertFalse(collisionOutside, "Should not have collision for coordinates outside boundaries");
    }

    /**
     * Tests collision check with an Entity.
     */
    @Test
    void shouldCheckCollisionWithEntity() {
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, true, true, "table");
        IEntity mockEntity = mock(IEntity.class);
        
        // Set up mock-entity with positions that overlap with GameObject
        when(mockEntity.getX()).thenReturn(X_POS);
        when(mockEntity.getY()).thenReturn(Y_POS);
        when(mockEntity.getWidth()).thenReturn(32);
        when(mockEntity.getHeight()).thenReturn(32);
        
        boolean collision = gameObject.collidesWith(mockEntity);
        assertFalse(collision, "Should not have collision with entity at the same position");
        
        // Test with entity outside GameObject
        when(mockEntity.getX()).thenReturn(X_POS + 100);
        when(mockEntity.getY()).thenReturn(Y_POS + 100);
        collision = gameObject.collidesWith(mockEntity);
        assertFalse(collision, "Should not have collision with entity outside GameObject");
    }

    /**
     * Tests collision check with non-solid GameObject.
     */
    @Test
    void shouldNotCollideWhenNotSolid() {
        GameObject gameObject = new GameObject(mockImage, X_POS, Y_POS, false, true, "table");
        IEntity mockEntity = mock(IEntity.class);
        
        when(mockEntity.getX()).thenReturn(X_POS);
        when(mockEntity.getY()).thenReturn(Y_POS);
        when(mockEntity.getWidth()).thenReturn(32);
        when(mockEntity.getHeight()).thenReturn(32);
        
        boolean collision = gameObject.collidesWith(mockEntity);
        assertFalse(collision, "Non-solid objects should not have collision");
    }
} 