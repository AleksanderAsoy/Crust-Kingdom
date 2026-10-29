package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.view.renderers.MenuRenderer;

/**
 * Test suite for MenuRenderer class.
 * Tests rendering logic without verifying actual graphical output.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MenuRendererTest {

    @Mock
    private GameLogic gameLogic;

    @Mock
    private Graphics2D graphics;

    @Mock
    private GameConfig config;

    private MenuRenderer menuRenderer;

    @BeforeEach
    void setUp() {
        // Mock FontMetrics
        FontMetrics fontMetrics = mock(FontMetrics.class);
        Rectangle rect = new Rectangle(0, 0, 100, 20);

        when(fontMetrics.stringWidth(anyString())).thenReturn(100);
        when(fontMetrics.getStringBounds(anyString(), any(Graphics2D.class))).thenReturn(rect);
        when(graphics.getFontMetrics()).thenReturn(fontMetrics);
        when(graphics.getFontMetrics(any(Font.class))).thenReturn(fontMetrics);

        // Mock GameLogic responses
        String[] menuOptions = { "Start" };
        String[] pauseOptions = { "Resume" };
        String[] gameOverOptions = { "Start New Game" };

        when(gameLogic.getMenuOptions()).thenReturn(menuOptions);
        when(gameLogic.getPauseOptions()).thenReturn(pauseOptions);
        when(gameLogic.getGameOverOptions()).thenReturn(gameOverOptions);
        when(gameLogic.getCurrentMenuSelection()).thenReturn(0);
        
        // Mock GameState og MenuState
        var gameState = mock(inf112.skeleton.model.game.GameState.class);
        var menuState = mock(inf112.skeleton.model.game.MenuState.class);
        when(gameLogic.getGameState()).thenReturn(gameState);
        when(gameState.getMenuState()).thenReturn(menuState);
        when(menuState.getCurrentSelection()).thenReturn(0);

        // Mock GameConfig responses
        when(config.getScreenWidth()).thenReturn(800);
        when(config.getScreenHeight()).thenReturn(600);

        // Mock Font
        Font menuFont = new Font("Arial", Font.BOLD, 30);
        when(graphics.getFont()).thenReturn(menuFont);

        // Initialize MenuRenderer with gameLogic and config only
        menuRenderer = new MenuRenderer(gameLogic, config);
    }

    @Test
    void shouldDrawMainMenu() {
        assertDoesNotThrow(() -> menuRenderer.drawMenu(graphics, 50f, 30f));
    }

    @Test
    void shouldDrawPauseScreen() {
        assertDoesNotThrow(() -> menuRenderer.drawPauseScreen(graphics));
    }

    @Test
    void shouldDrawGameOver() {
        assertDoesNotThrow(() -> menuRenderer.drawGameOver(graphics));
    }

    @Test
    void shouldDrawStaticScreen() {
        String title = "Test Title";
        String[] content = {"Line 1", "Line 2", "Line 3"};
        
        assertDoesNotThrow(() -> menuRenderer.drawStaticScreen(graphics, title, content));
        
        // Verify correct use of graphics
        verify(graphics, atLeastOnce()).setColor(any(Color.class));
        verify(graphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, atLeastOnce()).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldDrawSettingsScreen() {
        assertDoesNotThrow(() -> menuRenderer.drawSettingsScreen(graphics));
        
        // Verify correct use of graphics
        verify(graphics, atLeastOnce()).setColor(any(Color.class));
        verify(graphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics, atLeastOnce()).setFont(any(Font.class));
        verify(graphics, atLeast(2)).drawString(anyString(), anyInt(), anyInt());
    }
}