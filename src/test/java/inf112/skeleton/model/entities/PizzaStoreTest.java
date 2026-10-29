package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.utils.TestUtils;

import java.io.Serializable;

class PizzaStoreTest {
    private PizzaStore pizzaStore;
    private Player player;

    @BeforeEach
    void setUp() {
        pizzaStore = new PizzaStore();
        GameConfig config = new GameConfig();
        player = TestUtils.createMockPlayer(config, 1000);
    }

    @Test
    void testConstructor() {
        assertNotNull(pizzaStore);
        assertEquals(1, pizzaStore.getPizzaCount("Vegetarian"));
        assertEquals(1, pizzaStore.getPizzaCount("Pepperoni"));
        assertEquals(1, pizzaStore.getPizzaCount("Cheese"));
    }

    @Test
    void testPurchasePizza() {
        // Test successful purchase
        boolean result = pizzaStore.purchasePizza("Vegetarian", player);
        assertTrue(result);
        assertEquals(2, pizzaStore.getPizzaCount("Vegetarian"));
        assertEquals(995, player.getMoney()); // 1000 - 5 (PIZZA_PRICE)

        // Test purchase with insufficient money
        player.spendMoney(player.getMoney());
        result = pizzaStore.purchasePizza("Pepperoni", player);
        assertFalse(result);
        assertEquals(1, pizzaStore.getPizzaCount("Pepperoni"));
        assertEquals(0, player.getMoney());

        // Test purchase with invalid pizza type
        result = pizzaStore.purchasePizza("InvalidType", player);
        assertFalse(result);
    }

    @Test
    void testUsePizza() {
        // Test successful use
        boolean result = pizzaStore.usePizza("Vegetarian");
        assertTrue(result);
        assertEquals(0, pizzaStore.getPizzaCount("Vegetarian"));

        // Test use when no pizzas left
        result = pizzaStore.usePizza("Vegetarian");
        assertFalse(result);
        assertEquals(0, pizzaStore.getPizzaCount("Vegetarian"));

        // Test use with invalid pizza type
        result = pizzaStore.usePizza("InvalidType");
        assertFalse(result);
    }

    @Test
    void testGetPizzaCount() {
        assertEquals(1, pizzaStore.getPizzaCount("Vegetarian"));
        assertEquals(1, pizzaStore.getPizzaCount("Pepperoni"));
        assertEquals(1, pizzaStore.getPizzaCount("Cheese"));
        assertEquals(0, pizzaStore.getPizzaCount("InvalidType"));
    }

    @Test
    void testGetPizzaPrice() {
        assertEquals(5, pizzaStore.getPizzaPrice());
    }

    @Test
    void testReset() {
        // Modify inventory first
        pizzaStore.purchasePizza("Vegetarian", player);
        pizzaStore.usePizza("Pepperoni");
        
        // Reset and verify
        pizzaStore.reset();
        assertEquals(1, pizzaStore.getPizzaCount("Vegetarian"));
        assertEquals(1, pizzaStore.getPizzaCount("Pepperoni"));
        assertEquals(1, pizzaStore.getPizzaCount("Cheese"));
    }

    @Test
    void testSerialization() {
        assertInstanceOf(Serializable.class, pizzaStore);
    }
} 