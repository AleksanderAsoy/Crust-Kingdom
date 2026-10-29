package inf112.skeleton.view.sprites;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.view.ui.ImageLoader;

@ExtendWith(MockitoExtension.class)
class NPCSpriteTest {
    
    @Mock
    private NPC npc;
    
    @Mock
    private BufferedImage mockImage;
    
    private NPCSprite npcSprite;
    
    @BeforeEach
    void setUp() {
        // Mock ImageLoader to return test image
        try (var mockedImageLoader = mockStatic(ImageLoader.class)) {
            mockedImageLoader.when(() -> ImageLoader.loadImage(anyString())).thenReturn(mockImage);
            
            // Mock npc.getSkin() to return "Npc"
            when(npc.getSkin()).thenReturn("Npc");
            
            // Create NPCSprite
            npcSprite = new NPCSprite(npc);
        }
    }
    
    @Test
    void shouldInitializeWithCorrectNPC() {
        assertNotNull(npcSprite, "NPCSprite should be created");
        assertEquals("Npc", npcSprite.getEntityType(), "NPC type should be set correctly");
    }
    
    @Test
    void shouldReflectNPCVisibility() {
        when(npc.isVisible()).thenReturn(false);
        assertFalse(npcSprite.isVisible(), "NPCSprite should reflect NPC visibility");
        
        when(npc.isVisible()).thenReturn(true);
        assertTrue(npcSprite.isVisible(), "NPCSprite should reflect NPC visibility");
    }
    
    @Test
    void shouldHandleDifferentNPCTypes() {
        // Test for regular NPC
        when(npc.getSkin()).thenReturn("Npc");
        NPCSprite regularSprite = new NPCSprite(npc);
        assertNotNull(regularSprite.getCurrentSprite(), "Regular NPC sprite should be loaded");
        
        // Test for robber NPC
        when(npc.getSkin()).thenReturn("NpcRobber");
        NPCSprite robberSprite = new NPCSprite(npc);
        assertNotNull(robberSprite.getCurrentSprite(), "Robber NPC sprite should be loaded");
    }
    
    @Test
    void shouldReturnCorrectPosition() {
        when(npc.getX()).thenReturn(100);
        when(npc.getY()).thenReturn(200);
        
        assertEquals(100, npcSprite.getX(), "X position should match NPC");
        assertEquals(200, npcSprite.getY(), "Y position should match NPC");
    }
    
    @Test
    void shouldHandleAnimationStates() {
        when(npc.getDirection()).thenReturn("down");
        when(npc.getAnimationState()).thenReturn(1);
        BufferedImage sprite1 = npcSprite.getCurrentSprite();
        
        when(npc.getAnimationState()).thenReturn(2);
        BufferedImage sprite2 = npcSprite.getCurrentSprite();
        
        assertNotNull(sprite1, "First animation state should return a sprite");
        assertNotNull(sprite2, "Second animation state should return a sprite");
    }
} 