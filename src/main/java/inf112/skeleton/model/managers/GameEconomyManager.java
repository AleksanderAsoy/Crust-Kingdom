package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;

import inf112.skeleton.model.game.GameLogic;

/**
 * Handles the game's economy and day system.
 * This includes:
 * - Day counting and time
 * - Money handling with robust validation
 * - Rent system
 */
public class GameEconomyManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int money = 0;
    private int dayCounter = 0; // Start at day 0
    private long dayTimer;
    private static final long DAY_DURATION = 10_000; // One day is 10s
    private static final int MAX_BALANCE = Integer.MAX_VALUE;
    private static final int MIN_BALANCE = 0;
    private static final int RENT_DUE_DATE = 10; // Every 10th day
    private static final int RENT_AMOUNT = 10; // Rent amount
    private final GameLogic gameLogic;

    public GameEconomyManager(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.dayTimer = System.currentTimeMillis();
    }

    /**
     * Updates the day system and checks for rent.
     */
    public void updateEconomy() {
        long currentTime = getCurrentTimeMillis();
        if (currentTime - dayTimer >= DAY_DURATION) {
            dayCounter++;
            dayTimer = currentTime;

            // Check if it's rent day
            if (dayCounter % RENT_DUE_DATE == 0) {
                spendMoney(RENT_AMOUNT); // This will trigger game over if balance goes negative
            }
        }
    }

    /**
     * Protected method to get the current time in milliseconds.
     * Can be overridden in tests for deterministic testing.
     * 
     * @return Current time in milliseconds
     */
    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Sets the day timer to a specific time.
     * Used only in tests to simulate time.
     * 
     * @param time Time in milliseconds
     */
    protected void setDayTimer(long time) {
        this.dayTimer = time;
    }

    /**
     * Adds money to the balance in a safe way.
     * @param amount The amount to add (must be positive)
     * @throws IllegalArgumentException if amount is negative
     */
    public void addMoney(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add a negative amount: " + amount);
        }

        // Check for overflow
        if (MAX_BALANCE - amount < money) {
            money = MAX_BALANCE;
            return;
        }

        money += amount;
    }

    /**
     * Subtracts money from the balance in a safe way.
     * @param amount The amount to subtract (must be positive)
     * @throws IllegalArgumentException if amount is negative
     */
    public void spendMoney(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot subtract from a negative amount: " + amount);
        }

        money -= amount;
        
        // Check for game over condition
        if (money < 0) {
            gameLogic.handlePlayerBankruptcy();
        }
    }

    /**
     * Resets the economy and day system.
     */
    public void resetEconomy() {
        money = MIN_BALANCE;
        dayCounter = 0; // Reset to day 0
        dayTimer = System.currentTimeMillis();
    }

    /**
     * Getter methods
     */
    public int getMoney() {
        return money;
    }

    public int getDayCounter() {
        return dayCounter;
    }
} 