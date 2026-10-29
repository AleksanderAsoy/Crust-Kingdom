package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.controller.GameController;
import inf112.skeleton.controller.KeyHandler;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;

/**
 * Test suite for the GamePanel class which handles the game's main window and
 * rendering.
 * <p>
 * This test class is organized in three main categories:
 * 1. Initialization - tests for correct panel setup
 * 2. Rendering - tests for screen size and buffering
 * 3. Game Loop - tests for the game's main loop and thread handling
 * <p>
 * Note: We deliberately avoid testing Swing's native rendering functionality
 * as this is difficult to mock reliably and is already well tested by the
 * Swing framework.
 */
class GamePanelTest {
    @Mock
    private GameConfig config;
    @Mock
    private RenderManager renderManager;
    @Mock
    private GameLogic gameLogic;
    @Mock
    private Player player;
    @Mock
    private GameController gameController;
    @Mock
    private KeyHandler keyHandler;
    private GamePanel gamePanel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(renderManager.getGameLogic()).thenReturn(gameLogic);
        when(gameLogic.getPlayer()).thenReturn(player);
        when(config.getScreenWidth()).thenReturn(800);
        when(config.getScreenHeight()).thenReturn(600);
        when(renderManager.getGameController()).thenReturn(gameController);
        when(gameController.keyHandler()).thenReturn(keyHandler);
        gamePanel = new GamePanel(config, renderManager);
    }

    /**
     * Tests related to GamePanel initialization.
     * Verifies that the panel is set up with correct basic properties.
     */
    @Nested
    @DisplayName("Initialization tests")
    class InitializationTests {
        /**
         * Checks that the panel is initialized with the correct background color.
         * This is important for the game's visual style and consistency.
         */
        @Test
        void shouldInitializeWithCorrectBackground() {
            assertEquals(Color.pink, gamePanel.getBackground(), "Panel should have pink background");
        }

        /**
         * Verifies that the panel is focusable.
         * This is critical for the panel to receive keyboard and mouse events.
         */
        @Test
        void shouldBeFocusable() {
            assertTrue(gamePanel.isFocusable(), "Panel should be focusable for keyboard input");
        }

        @Test
        void shouldInitializeWithRenderManager() {
            assertNotNull(gamePanel);
        }

        @Test
        void shouldBeDoubleBuffered() {
            assertTrue(gamePanel.isDoubleBuffered());
        }
    }

    /**
     * Tests related to rendering setup in GamePanel.
     * Focuses on screen size and buffering configuration.
     */
    @Nested
    @DisplayName("Rendering tests")
    class RenderingTests {
        @Test
        void shouldInitializeWithCorrectSize() {
            // GameConfig uses: tileSize (16 * 3 = 48) * MAX_SCREEN_COL (16) = 768
            // and tileSize (48) * MAX_SCREEN_ROW (12) = 576
            when(config.getScreenWidth()).thenReturn(768);
            when(config.getScreenHeight()).thenReturn(576);
            
            // Create a new GamePanel with updated config
            gamePanel = new GamePanel(config, renderManager);
            
            assertEquals(768, gamePanel.getPreferredSize().width,
                    "Panel width should match config screen width");
            assertEquals(576, gamePanel.getPreferredSize().height,
                    "Panel height should match config screen height");
        }

        @Test
        void shouldGetCurrentStateFromGameLogic() {
            // Create a real Graphics2D object for testing
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // When
            gamePanel.paintComponent(g2d);
            
            // Then
            verify(renderManager).render(any(Graphics2D.class));
            
            // Cleanup
            g2d.dispose();
        }

        @Test
        void shouldDisposeGraphicsAfterRendering() {
            // Create a real Graphics2D object that we can verify gets disposed
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = spy(image.createGraphics());
            
            // When
            gamePanel.paintComponent(g2d);
            
            // Then
            verify(g2d).dispose();
        }
    }

    /**
     * Tests related to the game's main loop.
     * Verifies that the game runs correctly in a separate thread.
     */
    @Nested
    @DisplayName("Game loop tests")
    class GameLoopTests {
        @Test
        void shouldStartGameThread() {
            assertDoesNotThrow(() -> gamePanel.startGameThread(),
                "Starting game thread should not throw an exception");
        }
    }

    /**
     * Tests related to the paintComponent method.
     * Note: We don't test the actual rendering, but verify that the correct methods are called.
     * This is an indirect test of the paintComponent logic.
     */
    @Nested
    @DisplayName("Paint component tests")
    class PaintComponentTests {
        @Test
        void shouldCallRenderManagerWhenPainting() {
            // Create a real Graphics2D object for testing
            BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            
            // When
            gamePanel.paintComponent(g2d);
            
            // Then
            verify(renderManager).render(any(Graphics2D.class));
            
            // Cleanup
            g2d.dispose();
        }

        @Test
        void shouldHandleNullGraphics() {
            // This should not throw an exception
            assertDoesNotThrow(() -> gamePanel.paintComponent(null));
        }
    }
} 