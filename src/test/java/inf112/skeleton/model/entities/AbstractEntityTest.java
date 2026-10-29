package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for AbstractEntity.
 * Uses a concrete implementation for testing.
 */
class AbstractEntityTest {
    private TestEntity entity;

    /**
     * Concrete implementation of AbstractEntity for testing.
     */
    private static class TestEntity extends AbstractEntity {
        public TestEntity(int x, int y, int speed, String direction, int width, int height) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.direction = direction;
            this.width = width;
            this.height = height;
        }

        @Override
        public void update() {
            // Empty implementation for testing
        }
    }

    @BeforeEach
    void setUp() {
        entity = new TestEntity(100, 200, 5, "right", 32, 32);
    }

    @Test
    void testInitialState() {
        assertEquals(100, entity.getX());
        assertEquals(200, entity.getY());
        assertEquals(5, entity.getSpeed());
        assertEquals("right", entity.getDirection());
        assertEquals(32, entity.getWidth());
        assertEquals(32, entity.getHeight());
        assertEquals(0, entity.getSpriteCounter());
        assertEquals(1, entity.getSpriteNum());
        assertTrue(entity.isVisible());
    }

    @Test
    void testPositionSetters() {
        entity.setX(150);
        assertEquals(150, entity.getX());
        
        entity.setY(250);
        assertEquals(250, entity.getY());
    }

    @Test
    void testSpeedSetter() {
        entity.setSpeed(10);
        assertEquals(10, entity.getSpeed());
    }

    @Test
    void testDirectionSetter() {
        entity.setDirection("left");
        assertEquals("left", entity.getDirection());
    }

    @Test
    void testSizeSetters() {
        entity.setWidth(64);
        assertEquals(64, entity.getWidth());
        
        entity.setHeight(64);
        assertEquals(64, entity.getHeight());
    }

    @Test
    void testSpriteCounter() {
        entity.setSpriteCounter(10);
        assertEquals(10, entity.getSpriteCounter());
    }

    @Test
    void testSpriteNum() {
        entity.setSpriteNum(2);
        assertEquals(2, entity.getSpriteNum());
    }

    @Test
    void testAnimationState() {
        entity.setSpriteNum(2);
        assertEquals(2, entity.getAnimationState());
    }

    @Test
    void testUpdateAnimation() {
        // Test sprite counter increment
        for (int i = 0; i < 19; i++) {
            entity.updateAnimation();
        }
        assertEquals(19, entity.getSpriteCounter());
        assertEquals(1, entity.getSpriteNum());

        // Test sprite counter at MAX_SPRITE_COUNTER
        entity.updateAnimation();
        assertEquals(20, entity.getSpriteCounter());
        assertEquals(1, entity.getSpriteNum());

        // Test sprite counter reset and sprite num toggle
        entity.updateAnimation();
        assertEquals(0, entity.getSpriteCounter());
        assertEquals(2, entity.getSpriteNum());

        // Test sprite num toggle back
        for (int i = 0; i < 19; i++) {
            entity.updateAnimation();
        }
        assertEquals(19, entity.getSpriteCounter());
        assertEquals(2, entity.getSpriteNum());

        entity.updateAnimation();
        assertEquals(20, entity.getSpriteCounter());
        assertEquals(2, entity.getSpriteNum());

        entity.updateAnimation();
        assertEquals(0, entity.getSpriteCounter());
        assertEquals(1, entity.getSpriteNum());
    }
} 