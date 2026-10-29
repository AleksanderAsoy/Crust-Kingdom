package inf112.skeleton.model.managers;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.factories.NPCFactory;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.interfaces.managers.IOrderManager;

/**
 * Handles NPCs (Non-Player Characters) in the game.
 * This includes:
 * - Spawning new NPCs
 * - Updating NPC states
 * - Removing inactive NPCs
 */
public class NPCManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(NPCManager.class.getName());
    
    private final transient List<NPC> activeNPCs;
    private long lastNpcSpawnTime = 0;
    private final long npcSpawnInterval;
    private final GameConfig config;
    private final transient IOrderManager orderManager;
    private final GameLogic gameLogic;
    private static final int MAX_ACTIVE_NPCS = 3;

    public NPCManager(GameConfig config, IOrderManager orderManager, GameLogic gameLogic) {
        this.config = config;
        this.orderManager = orderManager;
        this.gameLogic = gameLogic;
        this.npcSpawnInterval = 3000; // 10s between each spawn
        this.activeNPCs = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Updates all NPCs and handles spawning of new ones.
     */
    public void update() {
        synchronized (activeNPCs) {
            updateActiveNPCs();
            spawnNewNPCIfNeeded();
        }
    }

    /**
     * Updates existing active NPCs.
     */
    private void updateActiveNPCs() {
        Iterator<NPC> iterator = activeNPCs.iterator();
        while (iterator.hasNext()) {
            NPC npc = iterator.next();
            if (!npc.isVisible()) {
                npc.cleanup();
                iterator.remove();
                LOGGER.fine("NPC removed from active NPCs");
            } else {
                npc.update();
            }
        }
    }

    /**
     * Spawns a new NPC if enough time has passed since the last spawn.
     */
    private void spawnNewNPCIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNpcSpawnTime > npcSpawnInterval && activeNPCs.size() < MAX_ACTIVE_NPCS) {
            // Calculate starting position for NPC
            int startX = config.getScreenWidth() / 2;
            int startY = config.getScreenHeight() - config.getTileSize();
            
            NPC newNPC = NPCFactory.createNPC(orderManager, startX, startY, gameLogic);
            activeNPCs.add(newNPC);
            lastNpcSpawnTime = currentTime;
            if (LOGGER.isLoggable(java.util.logging.Level.FINE)) {
                LOGGER.fine(String.format("Spawned new NPC. Total active NPCs: %d", activeNPCs.size()));
            }
        }
    }

    /**
     * Removes all NPCs from the game.
     */
    public void clearNPCs() {
        synchronized (activeNPCs) {
            // Clean up each NPC before removing it
            for (NPC npc : activeNPCs) {
                npc.cleanup();
            }
            activeNPCs.clear();
            lastNpcSpawnTime = 0;
            // Reset the SpotManager instance
            SpotManager.resetInstance();
            LOGGER.info("Cleared all NPCs");
        }
    }

    /**
     * Gets the list of active NPCs.
     */
    public List<NPC> getNPCs() {
        return activeNPCs;
    }

}