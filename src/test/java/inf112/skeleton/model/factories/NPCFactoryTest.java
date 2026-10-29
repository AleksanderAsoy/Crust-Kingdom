package inf112.skeleton.model.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.model.managers.SpotManager;

class NPCFactoryTest {
    @Mock
    private OrderManager orderManager;
    
    @Mock
    private GameLogic gameLogic;
    
    @Spy
    private SpotManager spotManager;
    
    @Mock
    private PizzaManager pizzaManager;
    
    private MockedStatic<SpotManager> mockedSpotManager;
    private MockedStatic<PizzaManager> mockedPizzaManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Reset SpotManager singleton and mock getInstance
        SpotManager.resetInstance();
        mockedSpotManager = mockStatic(SpotManager.class);
        mockedSpotManager.when(SpotManager::getInstance).thenReturn(spotManager);
        when(spotManager.findAndReserveSpot()).thenReturn(0);
        when(spotManager.getSpotCoordinates(0)).thenReturn(new int[]{100, 200});
        
        // Mock PizzaManager
        PizzaManager.resetInstance();
        mockedPizzaManager = mockStatic(PizzaManager.class);
        mockedPizzaManager.when(() -> PizzaManager.getInstance(any(OrderManager.class))).thenReturn(pizzaManager);
        when(orderManager.isOrderQueueFull()).thenReturn(false);

        // Reset game start time
        NPCFactory.resetGameStartTime();
    }

    @AfterEach
    void tearDown() {
        if (mockedSpotManager != null) {
            mockedSpotManager.close();
        }
        if (mockedPizzaManager != null) {
            mockedPizzaManager.close();
        }
    }

    @Test
    void shouldCreateNPCWithValidPosition() {
        // Arrange
        int startX = 100;
        int startY = 200;
        
        // Act
        NPC npc = NPCFactory.createNPC(orderManager, startX, startY, gameLogic);
        
        // Assert
        assertNotNull(npc, "NPC should not be null");
        assertEquals(startX, npc.getX(), "NPC should have correct X position");
        assertEquals(startY, npc.getY(), "NPC should have correct Y position");
    }

    @Test
    void shouldCreateNPCWithValidSkin() {
        // Arrange
        int startX = 100;
        int startY = 200;
        
        // Act
        NPC npc = NPCFactory.createNPC(orderManager, startX, startY, gameLogic);
        
        // Assert
        assertNotNull(npc.getSkin(), "NPC should have a skin");
        assertTrue(npc.getSkin().equals("Npc") || npc.getSkin().equals("NpcVIP"), 
            "NPC should have a valid skin type");
    }

    @Test
    void shouldCreateVIPNPC() {
        // Arrange
        int startX = 100;
        int startY = 200;
        NPCFactory.setVipSpawnChance(1.0); // 100% chance for VIP
        
        // Act
        NPC npc = NPCFactory.createNPC(orderManager, startX, startY, gameLogic);
        
        // Assert
        assertEquals("NpcVIP", npc.getSkin(), "Should create VIP NPC with 100% spawn chance");
    }

    @Test
    void shouldResetGameStartTime() {
        // Act
        NPCFactory.resetGameStartTime();
        
        // Assert
        assertFalse(NPCFactory.canRobbersAppear(), "Robbers should not appear immediately after reset");
    }

    @Test
    void shouldHandleInvalidVipSpawnChance() {
        // Arrange
        double invalidChance = 1.5;
        
        // Act
        NPCFactory.setVipSpawnChance(invalidChance);
        
        // Assert
        // Create NPC to verify spawn chance is capped
        NPC npc = NPCFactory.createNPC(orderManager, 100, 200, gameLogic);
        assertNotNull(npc, "Should still create NPC with invalid spawn chance");
    }

    @Test
    void shouldHandleNullParameters() {
        // Act & Assert
        NPC npc = NPCFactory.createNPC(null, 100, 200, gameLogic);
        assertNotNull(npc, "NPC should be created even with null OrderManager");
        
        npc = NPCFactory.createNPC(orderManager, 100, 200, null);
        assertNotNull(npc, "NPC should be created even with null GameLogic");
    }

    @Test
    void shouldHandleFullOrderQueue() {
        // Arrange
        when(orderManager.isOrderQueueFull()).thenReturn(true);
        
        // Act
        NPC npc = NPCFactory.createNPC(orderManager, 100, 200, gameLogic);
        
        // Assert
        assertNotNull(npc, "NPC should be created even with full order queue");
    }

    @Test
    void shouldHandleInvalidSpotCoordinates() {
        // Arrange
        when(spotManager.getSpotCoordinates(anyInt())).thenReturn(null);
        
        // Act
        NPC npc = NPCFactory.createNPC(orderManager, 100, 200, gameLogic);
        
        // Assert
        assertNotNull(npc, "NPC should be created even with invalid spot coordinates");
    }
} 