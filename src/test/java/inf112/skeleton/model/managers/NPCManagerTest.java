package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;

class NPCManagerTest {
    @Mock
    private GameConfig config;
    @Mock
    private GameLogic gameLogic;
    @Mock
    private OrderManager orderManager;

    private NPCManager npcManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(config.getScreenWidth()).thenReturn(800);
        when(config.getScreenHeight()).thenReturn(600);
        when(config.getTileSize()).thenReturn(48);
        
        npcManager = new NPCManager(config, orderManager, gameLogic);
    }

    @Test
    void shouldStartWithEmptyNPCList() {
        assertTrue(npcManager.getNPCs().isEmpty());
    }

    @Test
    void shouldNotExceedMaxNPCs() {
        // Simulate many updates to test maximum number of NPCs
        for (int i = 0; i < 20; i++) {
            npcManager.update();
        }
        assertTrue(npcManager.getNPCs().size() <= 5);
    }

    @Test
    void shouldClearAllNPCs() {
        // Add some NPCs first
        for (int i = 0; i < 3; i++) {
            npcManager.update();
        }
        
        npcManager.clearNPCs();
        assertTrue(npcManager.getNPCs().isEmpty());
    }

    @Test
    void shouldUpdateAllNPCs() {
        // Add an NPC
        npcManager.update();
        assertFalse(npcManager.getNPCs().isEmpty());
        
        // Check that update doesn't throw exceptions
        assertDoesNotThrow(() -> npcManager.update());
    }
} 