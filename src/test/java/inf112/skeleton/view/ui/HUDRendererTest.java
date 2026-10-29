package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import inf112.skeleton.model.entities.PizzaOnCounter;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.managers.TileManager;
import inf112.skeleton.view.renderers.HUDRenderer;

/**
 * Test suite for HUDRenderer.
 * Tests initialization, method calls, and verifies correct rendering behavior.
 */
@ExtendWith(MockitoExtension.class)
class HUDRendererTest {

    @Mock
    private GameLogic gameLogic;

    @Mock
    private GameConfig config;

    @Mock
    private Graphics2D graphics;

    @Mock
    private FontMetrics fontMetrics;

    @Mock
    private Player player;

    private HUDRenderer hudRenderer;
    private MockedStatic<inf112.skeleton.view.ui.ImageLoader> mockedImageLoader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Use lenient stubbing to avoid unnecessary stubbing errors
        lenient().when(config.getScreenWidth()).thenReturn(800);
        lenient().when(config.getScreenHeight()).thenReturn(600);
        lenient().when(config.getTileSize()).thenReturn(48);
        
        // Set up player mock
        player = mock(Player.class);
        lenient().when(player.getX()).thenReturn(100);
        lenient().when(player.getY()).thenReturn(100);
        lenient().when(gameLogic.getPlayer()).thenReturn(player);
        
        // Set up graphics mock
        lenient().when(graphics.getFontMetrics(any(Font.class))).thenReturn(fontMetrics);
        lenient().when(graphics.getFontMetrics()).thenReturn(fontMetrics);
        
        // Set up font metrics mock
        lenient().when(fontMetrics.stringWidth(anyString())).thenReturn(100);
        lenient().when(fontMetrics.getHeight()).thenReturn(20);
        lenient().when(fontMetrics.getAscent()).thenReturn(15);
        
        // Mock all images
        BufferedImage mockImage = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        mockedImageLoader = mockStatic(inf112.skeleton.view.ui.ImageLoader.class);
        mockedImageLoader.when(() -> inf112.skeleton.view.ui.ImageLoader.loadImage("images/4_Tile_Sign.png")).thenReturn(mockImage);
        mockedImageLoader.when(() -> inf112.skeleton.view.ui.ImageLoader.loadImage("images/3_Tile_Sign.png")).thenReturn(mockImage);
        mockedImageLoader.when(() -> inf112.skeleton.view.ui.ImageLoader.loadImage("images/OrderQueue.png")).thenReturn(mockImage);
        
        // Standard setup for tests
        hudRenderer = new HUDRenderer(config, gameLogic);
    }

    @AfterEach
    void tearDown() {
        if (mockedImageLoader != null) {
            mockedImageLoader.close();
        }
    }

    @Test
    void shouldCreateHUDRenderer() {
        // Arrange & Act & Assert
        assertDoesNotThrow(() -> new HUDRenderer(config, gameLogic));
    }

    @Test
    void shouldDrawMoneyWithCorrectValue() {
        // Arrange
        int expectedMoney = 100;
        when(gameLogic.getMoney()).thenReturn(expectedMoney);

        // Act
        hudRenderer.drawMoney(graphics);

        // Assert
        verify(graphics).setColor(any(Color.class));
        verify(graphics).setFont(any(Font.class));
        verify(graphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), anyInt(), anyInt(), isNull());

        // Capture the text being drawn
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(graphics).drawString(textCaptor.capture(), anyInt(), anyInt());

        // Verify that the text contains the money value
        assertTrue(textCaptor.getValue().contains(String.valueOf(expectedMoney)));
    }

    @Test
    void shouldNotDrawOrdersWhenNotVisible() {
        // Arrange
        when(gameLogic.isOrderPopupVisible()).thenReturn(false);

        // Act
        hudRenderer.drawOrders(graphics);

        // Assert
        verify(gameLogic, never()).getOrders();
    }

    @Test
    void shouldDrawOrdersWhenVisible() {
        // Arrange
        when(gameLogic.isOrderPopupVisible()).thenReturn(true);
        List<String> orders = Arrays.asList("Order 1", "Order 2");
        List<Long> timers = Arrays.asList(5000L, 5000L);
        when(gameLogic.getOrders()).thenReturn(orders);
        when(gameLogic.getOrderTimers()).thenReturn(timers);

        // Act
        hudRenderer.drawOrders(graphics);

        // Assert
        verify(graphics, atLeast(2)).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldHandleDrawPizzaProgressCall() {
        when(gameLogic.isPizzaCooking()).thenReturn(true);
        when(gameLogic.getPizzaCookingProgress()).thenReturn(0.5);
        assertDoesNotThrow(() -> hudRenderer.drawPizzaProgress(graphics));
    }

    @Test
    void shouldDrawDayCounter() {
        // Arrange
        int dayCounter = 5;
        boolean rentDue = false;

        // Act & Assert
        assertDoesNotThrow(() -> hudRenderer.drawDayCounter(graphics, dayCounter, rentDue));

        // Verify that text was drawn
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(graphics).drawString(textCaptor.capture(), anyInt(), anyInt());

        // Verify that the text contains the day counter
        assertTrue(textCaptor.getValue().contains(String.valueOf(dayCounter)));
    }

    @Test
    void shouldDrawDayCounterWithRentDue() {
        // Arrange
        int dayCounter = 5;
        boolean rentDue = true;

        // Act & Assert
        assertDoesNotThrow(() -> hudRenderer.drawDayCounter(graphics, dayCounter, rentDue));

        // Verify that text was drawn at least twice (day counter and rent message)
        verify(graphics, atLeast(2)).drawString(anyString(), anyInt(), anyInt());

        // Verify that red color was used for rent message
        verify(graphics).setColor(Color.RED); // Pass the value directly without using eq()
    }

    @Test
    void shouldNotDrawInteractionPromptWhenNotVisible() {
        // Arrange
        when(gameLogic.isInteractionPromptVisible()).thenReturn(false);

        // Act
        hudRenderer.drawInteractionPrompt(graphics);

        // Assert
        verify(graphics, never()).fillRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void shouldNotDrawInteractionPromptWhenNotNearInteractable() {
        // Arrange
        when(gameLogic.isInteractionPromptVisible()).thenReturn(true);
        when(gameLogic.isPlayerNearInteractableTile()).thenReturn(false);

        // Act
        hudRenderer.drawInteractionPrompt(graphics);

        // Assert
        verify(graphics, never()).fillRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void shouldDrawInteractionPromptWhenVisibleAndNearInteractable() {
        // Arrange
        when(gameLogic.isInteractionPromptVisible()).thenReturn(true);
        when(gameLogic.isPlayerNearInteractableTile()).thenReturn(true);
        when(gameLogic.getPlayer()).thenReturn(player);
        when(gameLogic.getTileManager()).thenReturn(mock(TileManager.class));
        when(gameLogic.getTileManager().getTileType(anyInt(), anyInt())).thenReturn("Store");

        // Act
        hudRenderer.drawInteractionPrompt(graphics);

        // Assert
        verify(graphics).fillRoundRect(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt());
        verify(graphics).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldDrawPizzasOnCounterWhenPizzasExist() {
        // Arrange
        List<PizzaOnCounter> pizzas = Collections.singletonList(
                mock(PizzaOnCounter.class)
        );
        when(gameLogic.getPizzasOnCounter()).thenReturn(pizzas);
        when(pizzas.get(0).getImage()).thenReturn(mock(BufferedImage.class));
        
        // Act
        hudRenderer.drawPizzasOnCounter(graphics, 32);
        
        // Assert
        verify(graphics).drawImage(any(BufferedImage.class), anyInt(), anyInt(), eq(32), eq(32), isNull());
    }
    
    @Test
    void shouldHandleEmptyPizzaList() {
        // Arrange
        when(gameLogic.getPizzasOnCounter()).thenReturn(List.of());
        
        // Act & Assert
        assertDoesNotThrow(() -> hudRenderer.drawPizzasOnCounter(graphics, 32));
        verify(graphics, never()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    void shouldDrawStoreSign() {
        // Arrange
        int x = 100;
        int y = 100;

        // Act
        hudRenderer.drawStoreSign(graphics, x, y);

        // Assert
        verify(graphics).drawImage(any(BufferedImage.class), eq(x), eq(y), eq(144), eq(144), any());
        verify(graphics).setFont(any(Font.class));
        verify(graphics).setColor(Color.DARK_GRAY);
        verify(graphics).drawString(eq("STORE:"), anyInt(), anyInt());
    }

    @Test
    void shouldDrawKarmaBar() {
        when(gameLogic.getKarma()).thenReturn(0.5f);
        assertDoesNotThrow(() -> hudRenderer.drawKarmaBar(graphics));
        verify(graphics, atLeastOnce()).setColor(any(Color.class));
        verify(graphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void shouldHandleRenderCall() {
        assertDoesNotThrow(() -> hudRenderer.render(graphics));
        verify(graphics, atLeastOnce()).drawImage(any(BufferedImage.class), anyInt(), anyInt(), anyInt(), anyInt(), isNull());
    }

    @Test
    void shouldHandleFontLoadingFailure() {
        // Arrange
        mockedImageLoader.when(() -> inf112.skeleton.view.ui.ImageLoader.loadImage(anyString())).thenReturn(null);
        
        // Act & Assert
        assertDoesNotThrow(() -> new HUDRenderer(config, gameLogic));
    }
}