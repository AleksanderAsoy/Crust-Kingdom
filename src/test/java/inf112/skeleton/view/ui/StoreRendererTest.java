package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.entities.PizzaStore;
import inf112.skeleton.model.entities.Store;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.view.renderers.StoreRenderer;

class StoreRendererTest {
    private StoreRenderer storeRenderer;
    private Store store;
    private Graphics2D mockGraphics;
    private FontMetrics mockFontMetrics;
    private PizzaStore mockPizzaStore;
    private List<Store.StoreItem> items;

    @BeforeEach
    void setUp() {
        GameConfig config = mock(GameConfig.class);
        when(config.getScreenWidth()).thenReturn(800);
        when(config.getScreenHeight()).thenReturn(600);
        
        store = mock(Store.class);
        when(store.getSelectedIndex()).thenReturn(0);
        when(store.getCurrentPage()).thenReturn(0);
        
        items = new ArrayList<>();
        Store.StoreItem item = mock(Store.StoreItem.class);
        when(item.getName()).thenReturn("Test Item");
        when(item.getPrice()).thenReturn(100);
        when(item.isPurchased()).thenReturn(false);
        items.add(item);
        when(store.getItems()).thenReturn(items);
        
        mockPizzaStore = mock(PizzaStore.class);
        when(store.getPizzaStore()).thenReturn(mockPizzaStore);
        when(mockPizzaStore.getPizzaCount(anyString())).thenReturn(0);
        when(mockPizzaStore.getPizzaPrice()).thenReturn(100);
        
        storeRenderer = new StoreRenderer(config, store);
        mockGraphics = mock(Graphics2D.class);
        mockFontMetrics = mock(FontMetrics.class);
        when(mockGraphics.getFontMetrics()).thenReturn(mockFontMetrics);
        when(mockFontMetrics.stringWidth(anyString())).thenReturn(100);
    }

    @Test
    void testInitialization() {
        assertNotNull(storeRenderer);
        assertFalse(storeRenderer.isMenuVisible());
    }

    @Test
    void testMenuVisibility() {
        storeRenderer.setMenuVisible(true);
        assertTrue(storeRenderer.isMenuVisible());
        
        storeRenderer.setMenuVisible(false);
        assertFalse(storeRenderer.isMenuVisible());
    }

    @Test
    void testPurchaseMessage() {
        storeRenderer.setMenuVisible(true);
        storeRenderer.showPurchaseMessage("Test message");
        storeRenderer.render(mockGraphics);
        verify(mockGraphics).setColor(Color.GREEN);
        verify(mockGraphics).drawString(eq("Test message"), anyInt(), anyInt());
    }

    @Test
    void testRenderWhenMenuNotVisible() {
        storeRenderer.setMenuVisible(false);
        storeRenderer.render(mockGraphics);
        verify(mockGraphics, never()).drawImage(any(), anyInt(), anyInt(), anyInt(), anyInt(), any());
    }

    @Test
    void testRenderWhenMenuVisible() {
        storeRenderer.setMenuVisible(true);
        storeRenderer.render(mockGraphics);
        verify(mockGraphics).drawImage(any(), anyInt(), anyInt(), anyInt(), anyInt(), any());
        verify(mockGraphics, atLeastOnce()).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void testFontLoading() {
        assertNotNull(storeRenderer);
        try {
            assertNotNull(storeRenderer.getClass().getDeclaredField("menuFont"));
        } catch (NoSuchFieldException e) {
            fail("menuFont field not found");
        }
    }

    @Test
    void testBackgroundImageLoading() {
        assertNotNull(storeRenderer);
        try {
            assertNotNull(storeRenderer.getClass().getDeclaredField("storeBackground"));
        } catch (NoSuchFieldException e) {
            fail("storeBackground field not found");
        }
    }

    @Test
    void testRenderUpgradesPage() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(0);
        storeRenderer.render(mockGraphics);
        verify(mockGraphics, atLeastOnce()).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void testRenderPizzaStorePage() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(1);
        storeRenderer.render(mockGraphics);
        verify(mockGraphics, atLeastOnce()).drawString(anyString(), anyInt(), anyInt());
    }

    @Test
    void testMoveSelectionUpgradesPage() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(0);
        when(store.getSelectedIndex()).thenReturn(0);
        when(store.getItems()).thenReturn(items);
        
        // Test moving down
        storeRenderer.moveSelection(1);
        verify(store).setSelectedIndex(0); // Should wrap around to 0 since only 1 item
        
        // Test moving up
        storeRenderer.moveSelection(-1);
        verify(store, times(2)).setSelectedIndex(0); // Should wrap around to 0
    }

    @Test
    void testMoveSelectionPizzaStorePage() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(1);
        when(store.getSelectedIndex()).thenReturn(0);
        
        // Test moving down
        storeRenderer.moveSelection(1);
        verify(store).setSelectedIndex(1);
        
        // Test moving up from middle
        when(store.getSelectedIndex()).thenReturn(1);
        storeRenderer.moveSelection(-1);
        verify(store).setSelectedIndex(0);
        
        // Test wrapping from top
        when(store.getSelectedIndex()).thenReturn(0);
        storeRenderer.moveSelection(-1);
        verify(store).setSelectedIndex(2); // Should wrap to last pizza type
    }

    @Test
    void testStoreObserverUpdate() {
        when(store.getSelectedIndex()).thenReturn(5);
        storeRenderer.update(store);
        // Verify that the selection was updated
        verify(store).getSelectedIndex();
    }

    @Test
    void testPurchaseMessageExpiration() {
        storeRenderer.setMenuVisible(true);
        
        // Show message and render immediately - should show message
        storeRenderer.showPurchaseMessage("Test message");
        storeRenderer.render(mockGraphics);
        verify(mockGraphics).setColor(Color.GREEN);
        verify(mockGraphics).drawString(eq("Test message"), anyInt(), anyInt());
        
        // Reset mock to verify next render
        reset(mockGraphics);
        when(mockGraphics.getFontMetrics()).thenReturn(mockFontMetrics);
        
        // Set purchase message time to more than MESSAGE_DURATION ago
        try {
            java.lang.reflect.Field field = StoreRenderer.class.getDeclaredField("purchaseMessageTime");
            field.setAccessible(true);
            field.setLong(storeRenderer, System.currentTimeMillis() - 1100); // Set to pastime
        } catch (Exception e) {
            fail("Could not set purchase message time");
        }
        
        // Render again - message should be expired
        storeRenderer.render(mockGraphics);
        verify(mockGraphics, never()).setColor(Color.GREEN);
    }

    @Test
    void testPizzaInventoryDisplay() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(1);
        when(mockPizzaStore.getPizzaCount("Vegetarian")).thenReturn(2);
        when(mockPizzaStore.getPizzaCount("Pepperoni")).thenReturn(1);
        when(mockPizzaStore.getPizzaCount("Cheese")).thenReturn(3);
        
        storeRenderer.render(mockGraphics);
        
        // Verify that all pizza types are displayed with their counts
        verify(mockGraphics, atLeastOnce()).drawString(contains("Vegetarian Pizza: 2"), anyInt(), anyInt());
        verify(mockGraphics, atLeastOnce()).drawString(contains("Pepperoni Pizza: 1"), anyInt(), anyInt());
        verify(mockGraphics, atLeastOnce()).drawString(contains("Cheese Pizza: 3"), anyInt(), anyInt());
    }

    @Test
    void testMenuVisibilityResetSelection() {
        storeRenderer.setMenuVisible(true);
        when(store.getSelectedIndex()).thenReturn(5);
        
        // Reset selection when menu is hidden
        storeRenderer.setMenuVisible(false);
        verify(store, never()).setSelectedIndex(0); // The selection is not reset when menu is hidden
    }

    @Test
    void testUpgradeItemDisplay() {
        storeRenderer.setMenuVisible(true);
        when(store.getCurrentPage()).thenReturn(0);
        
        // Create a purchased item
        Store.StoreItem purchasedItem = mock(Store.StoreItem.class);
        when(purchasedItem.getName()).thenReturn("Purchased Item");
        when(purchasedItem.getPrice()).thenReturn(200);
        when(purchasedItem.isPurchased()).thenReturn(true);
        
        List<Store.StoreItem> testItems = new ArrayList<>();
        testItems.add(purchasedItem);
        when(store.getItems()).thenReturn(testItems);
        
        storeRenderer.render(mockGraphics);
        
        // Verify that purchased status is displayed
        verify(mockGraphics, atLeastOnce()).drawString(contains("(Purchased)"), anyInt(), anyInt());
    }
} 