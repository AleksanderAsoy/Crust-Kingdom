package inf112.skeleton.view.sprites;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inf112.skeleton.model.interfaces.IEntity;
import inf112.skeleton.view.ui.ImageLoader;

@ExtendWith(MockitoExtension.class)
class EntitySpriteTest {
    
    @Mock
    private IEntity entity;
    
    @Mock
    private BufferedImage mockImage;
    
    private EntitySprite entitySprite;
    
    @BeforeEach
    void setUp() {
        // Mock ImageLoader to return test image
        try (var mockedImageLoader = mockStatic(ImageLoader.class)) {
            mockedImageLoader.when(() -> ImageLoader.loadImage(anyString())).thenReturn(mockImage);
            
            // Create EntitySprite
            entitySprite = new EntitySprite(entity, "Chef");
        }
    }
    
    @Test
    void shouldInitializeWithCorrectEntity() {
        assertNotNull(entitySprite, "EntitySprite should be created");
        assertEquals("Chef", entitySprite.getEntityType(), "Entity type should be set correctly");
    }
    
    @Test
    void shouldReturnCorrectSpriteForDirection() {
        // Set up mock for this test
        when(entity.getDirection()).thenReturn("down");
        when(entity.getAnimationState()).thenReturn(1);
        
        // Test for each direction
        String[] directions = {"up", "down", "left", "right", "waiting_at_counter"};
        for (String direction : directions) {
            when(entity.getDirection()).thenReturn(direction);
            when(entity.getAnimationState()).thenReturn(1);
            
            BufferedImage sprite = entitySprite.getCurrentSprite();
            assertNotNull(sprite, "Sprite should not be null for direction: " + direction);
        }
    }
    
    @Test
    void shouldHandleNullDirection() {
        when(entity.getDirection()).thenReturn(null);
        BufferedImage sprite = entitySprite.getCurrentSprite();
        assertNotNull(sprite, "Should return default sprite (down1) for null direction");
    }
    
    @Test
    void shouldReturnCorrectPosition() {
        when(entity.getX()).thenReturn(100);
        when(entity.getY()).thenReturn(200);
        
        assertEquals(100, entitySprite.getX(), "X position should match entity");
        assertEquals(200, entitySprite.getY(), "Y position should match entity");
    }
    
    @Test
    void shouldAlwaysBeVisible() {
        assertTrue(entitySprite.isVisible(), "EntitySprite should always be visible");
    }
    
    @Test
    void shouldLoadSpritesForDifferentEntityTypes() {
        // Test for NPC
        EntitySprite npcSprite = new EntitySprite(entity, "Npc");
        assertNotNull(npcSprite.getCurrentSprite(), "NPC sprite should be loaded");
        
        // Test for Robber
        EntitySprite robberSprite = new EntitySprite(entity, "NpcRobber");
        assertNotNull(robberSprite.getCurrentSprite(), "Robber sprite should be loaded");
    }
} 