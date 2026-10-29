package inf112.skeleton.model.factories;

import java.security.SecureRandom;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.interfaces.managers.IOrderManager;

/**
 * Factory class for creating NPCs with different skins.
 * Handles the creation and configuration of NPCs with various appearances.
 */
public class NPCFactory {
    private static final SecureRandom random = new SecureRandom();
    private static double robberSpawnChance = 0.05; // Default 5% chance, can be modified by upgrades
    private static double vipSpawnChance = 0.05; // 5% chance of spawning a VIP
    private static final long ROBBER_START_TIME = 60_000; // 1 minute in milliseconds
    private static long gameStartTime = System.currentTimeMillis();
    
    private NPCFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Creates a new NPC with a random skin.
     * 
     * @param orderManager The OrderManager instance
     * @param startX Starting X position
     * @param startY Starting Y position
     * @param gameLogic The GameLogic instance
     * @return A new NPC instance with a random skin
     */
    public static NPC createNPC(IOrderManager orderManager, int startX, int startY, GameLogic gameLogic) {
        double randomValue = random.nextDouble();
        String skin;
        
        // Only allow robber spawns after 1 minute has passed
        if (randomValue < robberSpawnChance && System.currentTimeMillis() - gameStartTime >= ROBBER_START_TIME) {
            skin = "NpcRobber";
        } else if (randomValue < robberSpawnChance + vipSpawnChance) {
            skin = "NpcVIP";
        } else {
            skin = "Npc";
        }
        
        return new NPC(orderManager, startX, startY, skin, gameLogic);
    }

    /**
     * Resets the game start time when a new game begins
     */
    public static void resetGameStartTime() {
        gameStartTime = System.currentTimeMillis();
    }

    /**
     * Returns whether robbers can start appearing
     */
    public static boolean canRobbersAppear() {
        return System.currentTimeMillis() - gameStartTime >= ROBBER_START_TIME;
    }

    /**
     * Sets the VIP spawn chance to a new value
     * @param chance The new spawn chance (0.0 to 1.0)
     */
    public static void setVipSpawnChance(double chance) {
        if (chance >= 0.0 && chance <= 1.0) {
            vipSpawnChance = chance;
        }
    }

    /**
     * Sets the robber spawn chance to a new value
     * @param chance The new spawn chance (0.0 to 1.0)
     */
    public static void setRobberSpawnChance(double chance) {
        if (chance >= 0.0 && chance <= 1.0) {
            robberSpawnChance = chance;
        }
    }

    /**
     * Resets all spawn chances to their default values.
     */
    public static void resetSpawnChances() {
        robberSpawnChance = 0.05;
        vipSpawnChance = 0.05;
    }

}