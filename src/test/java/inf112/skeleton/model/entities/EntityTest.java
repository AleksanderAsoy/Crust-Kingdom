package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for Entity class.
 */
class EntityTest {
    private TestEntity entity;
    private static final int TEST_X = 100;
    private static final int TEST_Y = 200;
    private static final int TEST_SPEED = 4;
    private static final String TEST_DIRECTION = "right";
    
    // Test class that inherits from Entity
    private static class TestEntity extends AbstractEntity {
        public TestEntity() {
            this.setX(TEST_X);
            this.setY(TEST_Y);
            this.setSpeed(TEST_SPEED);
            this.setDirection(TEST_DIRECTION);
        }

        @Override
        public void update() {
            // Empty implementation for testing
        }
    }
    
    @BeforeEach
    void setUp() {
        entity = new TestEntity();
    }
    
    @Test
    void shouldInitializeWithCorrectPosition() {
        assertEquals(TEST_X, entity.getX(), "X-position should be correctly initialized");
        assertEquals(TEST_Y, entity.getY(), "Y-position should be correctly initialized");
    }
    
    @Test
    void shouldInitializeWithCorrectSpeed() {
        assertEquals(TEST_SPEED, entity.getSpeed(), "Speed should be correctly initialized");
    }
    
    @Test
    void shouldInitializeWithCorrectDirection() {
        assertEquals(TEST_DIRECTION, entity.getDirection(), "Direction should be correctly initialized");
    }
    
    @Test
    void shouldInitializeWithCorrectSpriteCounter() {
        assertEquals(0, entity.getSpriteCounter(), "Sprite counter should start at 0");
    }
    
    @Test
    void shouldInitializeWithCorrectSpriteNumber() {
        assertEquals(1, entity.getSpriteNum(), "Sprite number should start at 1");
    }
} 