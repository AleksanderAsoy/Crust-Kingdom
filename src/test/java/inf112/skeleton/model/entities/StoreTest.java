package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.utils.TestUtils;

import java.io.Serializable;
import java.util.List;

class StoreTest {
    private Store store;
    private Player player;

    @BeforeEach
    void setUp() {
        store = new Store();
        GameConfig config = new GameConfig();
        player = TestUtils.createMockPlayer(config, 1000);
    }

    @Test
    void testConstructor() {
        assertNotNull(store);
        assertNotNull(store.getItems());
        assertEquals(0, store.getSelectedIndex());
        assertEquals(0, store.getCurrentPage());
        assertNotNull(store.getPizzaStore());
    }

    @Test
    void testGetItems() {
        List<Store.StoreItem> items = store.getItems();
        assertNotNull(items);
        assertEquals(4, items.size());
        
        // Verify the items are initialized correctly
        assertEquals("Speed Boost", items.get(0).getName());
        assertEquals("Faster Pizza", items.get(1).getName());
        assertEquals("VIP Spawn", items.get(2).getName());
    }

    @Test
    void testSetSelectedIndex() {
        // Test valid indices for upgrades page
        store.setSelectedIndex(1);
        assertEquals(1, store.getSelectedIndex());
        
        // Test invalid indices for upgrades page
        store.setSelectedIndex(-1);
        assertEquals(1, store.getSelectedIndex()); // Should not change
        
        store.setSelectedIndex(10);
        assertEquals(1, store.getSelectedIndex()); // Should not change
        
        // Test valid indices for pizza page
        store.setPage(1);
        store.setSelectedIndex(1);
        assertEquals(1, store.getSelectedIndex());
        
        // Test invalid indices for pizza page
        store.setSelectedIndex(-1);
        assertEquals(1, store.getSelectedIndex()); // Should not change
        
        store.setSelectedIndex(10);
        assertEquals(1, store.getSelectedIndex()); // Should not change
    }

    @Test
    void testGetSelectedItem() {
        // Test on upgrades page
        Store.StoreItem item = store.getSelectedItem();
        assertNotNull(item);
        assertEquals("Speed Boost", item.getName());
        
        // Test on pizza page
        store.setPage(1);
        assertNull(store.getSelectedItem());
    }

    @Test
    void testGetSelectedPizzaType() {
        // Test on upgrades page
        assertNull(store.getSelectedPizzaType());
        
        // Test on pizza page
        store.setPage(1);
        assertEquals("Vegetarian", store.getSelectedPizzaType());
        
        store.setSelectedIndex(1);
        assertEquals("Pepperoni", store.getSelectedPizzaType());
    }

    @Test
    void testPurchaseItem() {
        // Test purchasing upgrades
        Store.StoreItem item = store.getSelectedItem();
        assertFalse(item.isPurchased());
        
        boolean result = store.purchaseItem(player);
        assertTrue(result);
        assertTrue(item.isPurchased());
        
        // Test purchasing with insufficient money
        player.spendMoney(player.getMoney());
        result = store.purchaseItem(player);
        assertFalse(result);
        
        // Test purchasing already purchased item
        result = store.purchaseItem(player);
        assertFalse(result);
        
        // Test purchasing pizza
        store.setPage(1);
        player.addMoney(1000);
        result = store.purchaseItem(player);
        assertTrue(result);
    }

    @Test
    void testReset() {
        // Purchase some items first
        store.purchaseItem(player);
        store.setPage(1);
        store.setSelectedIndex(1);
        
        store.reset();
        
        assertEquals(0, store.getSelectedIndex());
        assertEquals(0, store.getCurrentPage());
        assertFalse(store.getItems().get(0).isPurchased());
    }

    @Test
    void testSetPage() {
        // Test valid page
        store.setPage(1);
        assertEquals(1, store.getCurrentPage());
        assertEquals(0, store.getSelectedIndex());
        
        // Test invalid page
        store.setPage(-1);
        assertEquals(1, store.getCurrentPage()); // Should not change
        
        store.setPage(2);
        assertEquals(1, store.getCurrentPage()); // Should not change
    }

    @Test
    void testStoreItem() {
        Store.StoreItem item = new Store.StoreItem("Test Item", "Test Description", 100);
        
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertEquals(100, item.getPrice());
        assertFalse(item.isPurchased());
        
        item.purchase();
        assertTrue(item.isPurchased());
        
        item.reset();
        assertFalse(item.isPurchased());
    }

    @Test
    void testSerialization() {
        assertInstanceOf(Serializable.class, store);
        assertInstanceOf(Serializable.class, new Store.StoreItem("Test", "Test", 0));
    }
} 