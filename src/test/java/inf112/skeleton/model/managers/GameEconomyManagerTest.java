package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.game.GameLogic;

/**
 * Test suite for GameEconomyManager class.
 * Tests economy, days and rent system.
 */
class GameEconomyManagerTest {
    
    @Mock
    private GameLogic gameLogic;
    
    private GameEconomyManager economyManager;
    private long currentTime;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currentTime = System.currentTimeMillis();
        economyManager = new GameEconomyManager(gameLogic) {
            @Override
            protected long getCurrentTimeMillis() {
                return currentTime;
            }
        };
        // Set the initial day timer to match our current time
        economyManager.setDayTimer(currentTime);
    }
    
    @Test
    void shouldInitializeWithCorrectValues() {
        assertEquals(0, economyManager.getMoney());
        assertEquals(0, economyManager.getDayCounter());
    }
    
    @Test
    void shouldAddMoneyCorrectly() {
        economyManager.addMoney(100);
        assertEquals(100, economyManager.getMoney());
    }
    
    @Test
    void shouldSpendMoneyCorrectly() {
        economyManager.addMoney(100);
        economyManager.spendMoney(50);
        assertEquals(50, economyManager.getMoney());
    }
    
    @Test
    void shouldUpdateDayCounter() {
        // Simulate 10 seconds passing
        currentTime += 10_000;
        economyManager.updateEconomy();
        assertEquals(1, economyManager.getDayCounter());
    }
    
    @Test
    void shouldDeductRentOnDay10() {
        // Add enough money to pay rent
        economyManager.addMoney(20);
        
        // Simulate 10 days passing
        for (int i = 0; i < 10; i++) {
            currentTime += 10_000;
            economyManager.updateEconomy();
        }
        
        // Check that rent was deducted
        assertEquals(10, economyManager.getMoney(), "Money should be 10 after paying rent"); // 20 - 10 for rent
    }
    
    @Test
    void shouldHandleBankruptcyOnRentDay() {
        // Start with 0 money
        assertEquals(0, economyManager.getMoney());
        
        // Simulate 10 days passing
        for (int i = 0; i < 10; i++) {
            currentTime += 10_000;
            economyManager.updateEconomy();
        }
        
        // Verify that handlePlayerBankruptcy was called
        verify(gameLogic, times(1)).handlePlayerBankruptcy();
    }
    
    @Test
    void shouldResetEconomyCorrectly() {
        // Add some money and increment days
        economyManager.addMoney(100);
        currentTime += 10_000;
        economyManager.updateEconomy();
        
        // Reset the economy
        economyManager.resetEconomy();
        
        // Verify everything is reset
        assertEquals(0, economyManager.getMoney());
        assertEquals(0, economyManager.getDayCounter());
    }
} 