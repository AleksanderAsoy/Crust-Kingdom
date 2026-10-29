package inf112.skeleton.model.utils;

import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;

/**
 * Utility class for test-related functionality.
 */
public class TestUtils {
    
    /**
     * Creates a mock player with the specified initial money.
     * 
     * @param config The game configuration
     * @param initialMoney The initial money amount
     * @return A mock player instance
     */
    public static Player createMockPlayer(GameConfig config, int initialMoney) {
        return new Player(config, null) {
            private int money = initialMoney;

            @Override
            public int getMoney() {
                return money;
            }

            @Override
            public void spendMoney(int amount) {
                if (money >= amount) {
                    money -= amount;
                }
            }

            @Override
            public void addMoney(int amount) {
                money += amount;
            }
        };
    }
} 