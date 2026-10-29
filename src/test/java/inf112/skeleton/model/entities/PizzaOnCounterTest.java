package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the PizzaOnCounter class.
 * Tests only public methods and focuses on functional behavior
 * rather than implementation details.
 */
class PizzaOnCounterTest {
    private PizzaOnCounter pizzaOnCounter;
    private static final int TEST_X = 100;
    private static final int TEST_Y = 200;
    private BufferedImage testImage;

    @BeforeEach
    void setUp() {
        testImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        pizzaOnCounter = new PizzaOnCounter(TEST_X, TEST_Y, testImage, 1);
    }

    @Test
    void shouldReturnCorrectPosition() {
        assertEquals(TEST_X, pizzaOnCounter.getX(), "Should return correct X position");
        assertEquals(TEST_Y, pizzaOnCounter.getY(), "Should return correct Y position");
    }

    @Test
    void shouldReturnCorrectImage() {
        assertSame(testImage, pizzaOnCounter.getImage(), "Should return the same image that was set");
    }

    @Test
    void shouldNotBeFinishedImmediately() {
        assertFalse(pizzaOnCounter.isFinished(), "Pizza should not be finished immediately after creation");
    }

    @Test
    void shouldBeFinishedAfterTimeout() {
        // Simulate the timeout directly without using Thread.sleep
        pizzaOnCounter.simulateTimeout(); // This method simulates the timeout happening immediately

        assertTrue(pizzaOnCounter.isFinished(), "Pizza should be finished after timeout period");
    }
}
