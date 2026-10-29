package inf112.skeleton.model.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the pizza inventory and purchasing system.
 */
public class PizzaStore implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Map<String, Integer> pizzaInventory;
    private static final int INITIAL_PIZZA_COUNT = 1;
    private static final int PIZZA_PRICE = 5; // Price per pizza
    
    /**
     * Creates a new PizzaStore with initial inventory.
     */
    public PizzaStore() {
        this.pizzaInventory = new HashMap<>();
        initializeInventory();
    }
    
    private void initializeInventory() {
        pizzaInventory.put("Vegetarian", INITIAL_PIZZA_COUNT);
        pizzaInventory.put("Pepperoni", INITIAL_PIZZA_COUNT);
        pizzaInventory.put("Cheese", INITIAL_PIZZA_COUNT);
    }
    
    /**
     * Attempts to purchase a pizza of the specified type.
     * 
     * @param type The type of pizza to purchase
     * @param player The player making the purchase
     * @return true if the purchase was successful, false otherwise
     */
    public boolean purchasePizza(String type, Player player) {
        if (player.getMoney() >= PIZZA_PRICE) {
            player.spendMoney(PIZZA_PRICE);
            pizzaInventory.put(type, pizzaInventory.get(type) + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Uses a pizza of the specified type from inventory.
     * 
     * @param type The type of pizza to use
     * @return true if the pizza was used successfully, false otherwise
     */
    public boolean usePizza(String type) {
        Integer count = pizzaInventory.get(type);
        if (count != null && count > 0) {
            pizzaInventory.put(type, count - 1);
            return true;
        }
        return false;
    }
    
    /**
     * Gets the count of a specific pizza type in inventory.
     * 
     * @param type The type of pizza to check
     * @return The number of pizzas of the specified type
     */
    public int getPizzaCount(String type) {
        return pizzaInventory.getOrDefault(type, 0);
    }
    
    /**
     * Gets the price of a pizza.
     * 
     * @return The price of a pizza
     */
    public int getPizzaPrice() {
        return PIZZA_PRICE;
    }
    
    /**
     * Resets the pizza inventory to initial values.
     */
    public void reset() {
        initializeInventory();
    }
} 