package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KarmaManagerTest {
    private KarmaManager karmaManager;
    
    @BeforeEach
    void setUp() {
        karmaManager = new KarmaManager();
    }
    
    @Test
    void shouldStartWithNeutralKarma() {
        assertEquals(5.0f, karmaManager.getCurrentKarma(), 
            "Karma should start at neutral value (5.0)");
    }
    
    @Test
    void shouldIncreaseKarma() {
        float initialKarma = karmaManager.getCurrentKarma();
        karmaManager.increaseKarma();
        assertTrue(karmaManager.getCurrentKarma() > initialKarma,
            "Karma should increase after calling increaseKarma()");
    }
    
    @Test
    void shouldDecreaseKarma() {
        float initialKarma = karmaManager.getCurrentKarma();
        karmaManager.decreaseKarma();
        assertTrue(karmaManager.getCurrentKarma() < initialKarma,
            "Karma should decrease after calling decreaseKarma()");
    }
    
    @Test
    void shouldNotExceedMaxKarma() {
        // Increase karma multiple times to reach max
        for (int i = 0; i < 20; i++) {
            karmaManager.increaseKarma();
        }
        assertTrue(karmaManager.getCurrentKarma() <= 10.0f,
            "Karma should not exceed maximum value of 10.0");
    }
    
    @Test
    void shouldNotGoBelowMinKarma() {
        // Decrease karma multiple times to reach min
        for (int i = 0; i < 20; i++) {
            karmaManager.decreaseKarma();
        }
        assertTrue(karmaManager.getCurrentKarma() >= 0.0f,
            "Karma should not go below minimum value of 0.0");
    }
    
    @Test
    void shouldResetToNeutralKarma() {
        // Change karma value
        karmaManager.increaseKarma();
        karmaManager.increaseKarma();
        
        // Reset
        karmaManager.reset();
        
        assertEquals(5.0f, karmaManager.getCurrentKarma(),
            "Karma should reset to neutral value (5.0)");
    }
} 