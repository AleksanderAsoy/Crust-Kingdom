package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.view.renderers.WorldRenderer;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorldRendererTest {
    
    @Mock private GameConfig config;
    @Mock private GameLogic gameLogic;
    @Mock private Graphics2D g2;
    @Mock private Player player;
    @Mock private NPC npc;
    @Mock private GameObject gameObject;
    
    private WorldRenderer worldRenderer;
    private BufferedImage testImage;
    
    @BeforeEach
    void setUp() {
        // Set up configuration
        when(config.getTileSize()).thenReturn(48);
        when(config.getMaxScreenCol()).thenReturn(2);
        when(config.getMaxScreenRow()).thenReturn(2);
        when(config.getScreenWidth()).thenReturn(96);
        when(config.getScreenHeight()).thenReturn(96);
        
        // Set up test image
        testImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        
        // Set up tile data
        when(gameLogic.getTileNum(anyInt(), anyInt())).thenReturn(0);
        when(gameLogic.getTileImage(anyInt())).thenReturn(testImage);
        
        // Mock OrderManager and its notifications
        OrderManager mockOrderManager = mock(OrderManager.class);
        when(gameLogic.getOrderManager()).thenReturn(mockOrderManager);
        when(mockOrderManager.getOrderNotifications()).thenReturn(Collections.emptyList());
        
        worldRenderer = new WorldRenderer(config, gameLogic);
    }
    
    @Test
    void shouldRenderTiles() {
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that the background cache (96x96) is drawn on the screen at position (0,0)
        verify(g2).drawImage(argThat(image -> image.getWidth(null) == 96 && image.getHeight(null) == 96),
                             eq(0),
                             eq(0),
                             isNull());
    }
    
    @Test
    void shouldRenderGameObjects() {
        // Set up game object
        when(gameObject.getImage()).thenReturn(testImage);
        when(gameObject.getX()).thenReturn(300);
        when(gameObject.getY()).thenReturn(300);
        when(gameLogic.getGameObjects()).thenReturn(Collections.singletonList(gameObject));
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that drawImage was called for the game object
        verify(g2).drawImage(
            eq(testImage),
            eq(300), // x
            eq(300), // y
            eq(48 * 3), // tileSize * DEFAULT_OBJECT_SCALE
            eq(48 * 3), // tileSize * DEFAULT_OBJECT_SCALE
            isNull()
        );
    }
    
    @Test
    void shouldRenderNPCs() {
        // Set up NPC
        when(npc.getSprite()).thenReturn(testImage);
        when(npc.getX()).thenReturn(200);
        when(npc.getY()).thenReturn(200);
        when(gameLogic.getNPCs()).thenReturn(Collections.singletonList(npc));
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that drawImage was called for the NPC
        verify(g2).drawImage(
            eq(testImage),
            eq(200), // x
            eq(200), // y
            eq(48), // tileSize
            eq(48), // tileSize
            isNull()
        );
    }
    
    @Test
    void shouldRenderPlayer() {
        // Set up player
        when(gameLogic.getPlayer()).thenReturn(player);
        when(player.getX()).thenReturn(100);
        when(player.getY()).thenReturn(100);
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that drawImage was called for the player
        verify(g2).drawImage(
            any(BufferedImage.class), // sprite is loaded dynamically, so we can't check the exact image
            eq(100), // x
            eq(100), // y
            eq(48), // tileSize
            eq(48), // tileSize
            isNull()
        );
    }

    @Test
    void shouldCreateBackgroundCache() {
        // Act
        worldRenderer.createBackgroundCache();
        
        // Assert - check that the background cache is created with the correct size
        // For a 2x2 grid, we expect getTileNum and getTileImage to be called for each tile
        verify(gameLogic, atLeast(4)).getTileNum(anyInt(), anyInt());
        verify(gameLogic, atLeast(4)).getTileImage(anyInt());
    }

    @Test
    void shouldDrawCollisionMaskWhenEnabled() {
        // Set up game object with collision mask
        when(gameObject.getImage()).thenReturn(testImage);
        when(gameObject.getX()).thenReturn(300);
        when(gameObject.getY()).thenReturn(300);
        when(gameLogic.getGameObjects()).thenReturn(Collections.singletonList(gameObject));
        boolean[][] collisionMask = new boolean[][]{{true, false}, {false, true}};
        when(gameObject.getCollisionMask()).thenReturn(collisionMask);
        
        // Enable collision masks
        worldRenderer.toggleCollisionMasks();
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that the color was changed and the collision mask was drawn
        verify(g2).setColor(any(Color.class));
        verify(g2, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(g2).setColor(any(Color.class)); // Check that original color was restored
    }

    @Test
    void shouldNotDrawCollisionMaskWhenDisabled() {
        // Set up game object with collision mask
        when(gameObject.getImage()).thenReturn(testImage);
        when(gameObject.getX()).thenReturn(300);
        when(gameObject.getY()).thenReturn(300);
        when(gameLogic.getGameObjects()).thenReturn(Collections.singletonList(gameObject));
        boolean[][] collisionMask = new boolean[][]{{true, false}, {false, true}};
        when(gameObject.getCollisionMask()).thenReturn(collisionMask);
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that the collision mask was not drawn
        verify(g2, never()).setColor(any(Color.class));
        verify(g2, never()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void shouldInvalidateBackgroundCache() {
        // Act
        worldRenderer.invalidateBackgroundCache();
        
        // Assert - check that the background is redrawn on the next render
        worldRenderer.render(g2);
        verify(gameLogic, times(4)).getTileNum(anyInt(), anyInt());
        verify(gameLogic, times(4)).getTileImage(anyInt());
    }

    @Test
    void shouldToggleCollisionMasks() {
        // Act & Assert - check that toggling works
        assertFalse(worldRenderer.isCollisionMasksVisible());
        worldRenderer.toggleCollisionMasks();
        assertTrue(worldRenderer.isCollisionMasksVisible());
        worldRenderer.toggleCollisionMasks();
        assertFalse(worldRenderer.isCollisionMasksVisible());
    }

    @Test
    void shouldHandleNullGameObjectImage() {
        // Set up game object without image
        when(gameObject.getImage()).thenReturn(null);
        when(gameLogic.getGameObjects()).thenReturn(Collections.singletonList(gameObject));
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that drawImage was not called for null image
        verify(g2, never()).drawImage(
            isNull(),
            anyInt(),
            anyInt(),
            anyInt(),
            anyInt(),
            isNull()
        );
    }

    @Test
    void shouldHandleNullNPCImage() {
        // Set up NPC without sprite
        when(npc.getSprite()).thenReturn(null);
        when(gameLogic.getNPCs()).thenReturn(Collections.singletonList(npc));
        
        // Act
        worldRenderer.render(g2);
        
        // Assert - check that drawImage was not called for null sprite
        verify(g2, never()).drawImage(
            isNull(),
            eq(200),
            eq(200),
            eq(48),
            eq(48),
            isNull()
        );
    }
}