package inf112.skeleton.model.interfaces;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IEntityTest {
    private IEntity entity;

    @BeforeEach
    void setUp() {
        entity = mock(IEntity.class);
        
        // Set up default mock behavior
        when(entity.getDirection()).thenReturn("right");
        when(entity.getX()).thenReturn(100);
        when(entity.getY()).thenReturn(200);
        when(entity.getSpeed()).thenReturn(5);
        when(entity.getSpriteCounter()).thenReturn(0);
        when(entity.getSpriteNum()).thenReturn(1);
        when(entity.getAnimationState()).thenReturn(1);
        when(entity.isVisible()).thenReturn(true);
        when(entity.getWidth()).thenReturn(32);
        when(entity.getHeight()).thenReturn(32);
    }

    @Test
    void shouldUpdateEntityState() {
        // Test that update method is called
        entity.update();
        verify(entity).update();
    }

    @Test
    void shouldHandleDirection() {
        // Test initial direction
        assertEquals("right", entity.getDirection());
        
        // Test setting new direction
        entity.setDirection("left");
        verify(entity).setDirection("left");
    }

    @Test
    void shouldHandlePosition() {
        // Test initial position
        assertEquals(100, entity.getX());
        assertEquals(200, entity.getY());
        
        // Test setting new position
        entity.setX(150);
        entity.setY(250);
        verify(entity).setX(150);
        verify(entity).setY(250);
    }

    @Test
    void shouldHandleSpeed() {
        // Test initial speed
        assertEquals(5, entity.getSpeed());
        
        // Test setting new speed
        entity.setSpeed(10);
        verify(entity).setSpeed(10);
    }

    @Test
    void shouldHandleSpriteProperties() {
        // Test sprite counter
        assertEquals(0, entity.getSpriteCounter());
        entity.setSpriteCounter(1);
        verify(entity).setSpriteCounter(1);
        
        // Test sprite number
        assertEquals(1, entity.getSpriteNum());
        entity.setSpriteNum(2);
        verify(entity).setSpriteNum(2);
        
        // Test animation state
        assertEquals(1, entity.getAnimationState());
    }

    @Test
    void shouldHandleVisibility() {
        // Test initial visibility
        assertTrue(entity.isVisible());
    }

    @Test
    void shouldHandleDimensions() {
        // Test initial dimensions
        assertEquals(32, entity.getWidth());
        assertEquals(32, entity.getHeight());
        
        // Test setting new dimensions
        entity.setWidth(64);
        entity.setHeight(64);
        verify(entity).setWidth(64);
        verify(entity).setHeight(64);
    }
} 