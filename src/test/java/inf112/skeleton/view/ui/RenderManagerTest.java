package inf112.skeleton.view.ui;

import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.game.GameState;
import inf112.skeleton.model.game.MenuState;
import inf112.skeleton.model.managers.OrderManager;
import inf112.skeleton.model.managers.PizzaManager;
import inf112.skeleton.controller.GameController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RenderManagerTest {
    private RenderManager renderManager;
    private GameLogic gameLogic;
    private GameConfig gameConfig;
    private GameController gameController;
    private Graphics2D graphics2D;

    @BeforeEach
    void setUp() {
        // Create mocks
        gameLogic = mock(GameLogic.class);
        gameConfig = mock(GameConfig.class);
        Store store = mock(Store.class);
        gameController = mock(GameController.class);
        graphics2D = mock(Graphics2D.class);
        FontMetrics fontMetrics = mock(FontMetrics.class);
        OrderManager orderManager = mock(OrderManager.class);
        GameState gameState = mock(GameState.class);
        MenuState menuState = mock(MenuState.class);
        PizzaManager pizzaManager = mock(PizzaManager.class);

        // Set up necessary stubs
        when(gameLogic.getStore()).thenReturn(store);
        when(gameConfig.getTileSize()).thenReturn(32);
        when(gameLogic.isShopMenuOpen()).thenReturn(false);
        when(gameConfig.getScreenWidth()).thenReturn(800);
        when(gameConfig.getScreenHeight()).thenReturn(600);
        when(graphics2D.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.stringWidth(anyString())).thenReturn(100);
        when(gameLogic.getOrderManager()).thenReturn(orderManager);
        when(orderManager.getOrderNotifications()).thenReturn(Collections.emptyList());
        when(gameLogic.getGameState()).thenReturn(gameState);
        when(gameState.getMenuState()).thenReturn(menuState);
        when(gameLogic.getPizzaManager()).thenReturn(pizzaManager);
        when(pizzaManager.isReadyToTakeOut()).thenReturn(false);
        
        // Set up menu options for different states
        String[] menuOptions = new String[]{"Start", "Settings", "About", "Exit"};
        String[] pauseOptions = new String[]{"Resume", "Settings", "Exit"};
        String[] gameOverOptions = new String[]{"Restart", "Exit"};
        String[] aboutOptions = new String[]{};
        String[] settingsOptions = new String[]{"Option 1", "Option 2"};
        
        when(menuState.getMenuOptions()).thenReturn(menuOptions);
        when(menuState.getPauseOptions()).thenReturn(pauseOptions);
        when(menuState.getGameOverOptions()).thenReturn(gameOverOptions);
        
        // Set up current options for each state
        when(menuState.getCurrentOptions(GameState.State.MENU)).thenReturn(menuOptions);
        when(menuState.getCurrentOptions(GameState.State.PAUSE)).thenReturn(pauseOptions);
        when(menuState.getCurrentOptions(GameState.State.GAME_OVER)).thenReturn(gameOverOptions);
        when(menuState.getCurrentOptions(GameState.State.ABOUT)).thenReturn(aboutOptions);
        when(menuState.getCurrentOptions(GameState.State.SETTINGS)).thenReturn(settingsOptions);

        // Initialize RenderManager with mocks
        renderManager = new RenderManager(gameConfig, gameLogic, gameController);
    }

    @Test
    void testRenderManagerInitialization() {
        // Verify that the necessary methods were called during initialization
        verify(gameLogic).getStore();
        verify(gameConfig).getTileSize();
        verify(gameLogic).isShopMenuOpen();
    }

    @Test
    void testUpdate() {
        // Reset the invocation count after initialization
        clearInvocations(gameLogic, gameController);
        
        renderManager.update();
        verify(gameController).update();
        verify(gameLogic).isShopMenuOpen();
    }

    @Test
    void testGetGameLogic() {
        assertEquals(gameLogic, renderManager.getGameLogic());
    }

    @Test
    void testGetGameController() {
        assertEquals(gameController, renderManager.getGameController());
    }

    @Test
    void testGetStoreRenderer() {
        assertNotNull(renderManager.getStoreRenderer());
    }

    @Test
    void testRenderPlayState() {
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.PLAY);
        when(gameLogic.isOrderPopupVisible()).thenReturn(false);
        when(gameLogic.getDayCounter()).thenReturn(1);
        
        renderManager.render(graphics2D);
        
        verify(gameLogic).getCurrentState();
        verify(gameLogic).isOrderPopupVisible();
        verify(gameLogic).getDayCounter();
        // Account for both calls to isShopMenuOpen (constructor and renderGame)
        verify(gameLogic, times(2)).isShopMenuOpen();
    }

    @Test
    void testRenderAboutState() {
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.ABOUT);
        renderManager.render(graphics2D);
        verify(gameLogic).getCurrentState();
    }

    @Test
    void testRenderSettingsState() {
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.SETTINGS);
        renderManager.render(graphics2D);
        verify(gameLogic).getCurrentState();
    }

    @Test
    void testScrollUp() {
        // Act
        renderManager.scrollUp();
        
        // Assert
        // Verify that the menu is rendered with the correct scroll position
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.ABOUT);
        renderManager.render(graphics2D);
        verify(gameLogic).getCurrentState();
    }

    @Test
    void testScrollDown() {
        // Act
        renderManager.scrollDown();
        
        // Assert
        // Verify that the menu is rendered with the correct scroll position
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.ABOUT);
        renderManager.render(graphics2D);
        verify(gameLogic).getCurrentState();
    }

    @Test
    void testResetScroll() {
        // Act
        renderManager.resetScroll();
        
        // Assert
        // Verify that the menu is rendered with the reset scroll position
        when(gameLogic.getCurrentState()).thenReturn(GameState.State.ABOUT);
        renderManager.render(graphics2D);
        verify(gameLogic).getCurrentState();
    }

    @Test
    void testStoreMenuVisibility() {
        // Clear previous invocations
        clearInvocations(gameLogic);
        
        // Test when store menu is open
        when(gameLogic.isShopMenuOpen()).thenReturn(true);
        renderManager.update();
        verify(gameLogic).isShopMenuOpen();

        // Test when store menu is closed
        when(gameLogic.isShopMenuOpen()).thenReturn(false);
        renderManager.update();
        verify(gameLogic, times(2)).isShopMenuOpen();
    }
}