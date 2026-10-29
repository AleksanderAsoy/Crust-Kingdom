package inf112.skeleton.model.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import inf112.skeleton.model.factories.NPCFactory;
import inf112.skeleton.model.managers.PizzaManager;

/**
 * Represents a store in the game that can sell items to the player.
 */
public class Store implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private final List<StoreItem> items;
    private int selectedIndex;
    private int currentPage; // 0 for upgrades, 1 for pizzas
    private final PizzaStore pizzaStore;
    private static final String[] PIZZA_TYPES = {"Vegetarian", "Pepperoni", "Cheese"};
    
    private final List<StoreObserver> observers = new ArrayList<>();
    
    public interface StoreObserver {
        void update(Store store);
    }
    
    public void addObserver(StoreObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    protected void notifyObservers() {
        for (StoreObserver observer : observers) {
            observer.update(this);
        }
    }

    /**
     * Constructor for Store.
     */
    public Store() {
        this.items = new ArrayList<>();
        this.selectedIndex = 0;
        this.currentPage = 0;
        this.pizzaStore = new PizzaStore();
        initializeItems();
    }

    private void initializeItems() {
        // Add store items here
        items.add(new StoreItem("Speed Boost", "Increases movement speed", 10));
        items.add(new StoreItem("Faster Pizza", "Cuts pizza cooking time in half", 50));
        items.add(new StoreItem("VIP Spawn", "Increases spawn rate of VIP NPCs", 100));
        items.add(new StoreItem("Police Patrol", "Reduces the chance of a crime being committed", 150));
    }

    /**
     * Gets the items in the store.
     * @return the items in the store
     */
    public List<StoreItem> getItems() {
        return items;
    }

    /**
     * Gets the selected index.
     * @return the selected index
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the selected index.
     * @param index the new selected index
     */
    public void setSelectedIndex(int index) {
        if (currentPage == 0) {
            if (index >= 0 && index < items.size()) {
                this.selectedIndex = index;
                notifyObservers();
            }
        } else {
            if (index >= 0 && index < PIZZA_TYPES.length) {
                this.selectedIndex = index;
                notifyObservers();
            }
        }
    }

    /**
     * Gets the selected item.
     * @return the selected item
     */
    public StoreItem getSelectedItem() {
        return currentPage == 0 ? items.get(selectedIndex) : null;
    }

    /**
     * Gets the selected pizza type.
     * @return the selected pizza type
     */ 
    public String getSelectedPizzaType() {
        return currentPage == 1 ? PIZZA_TYPES[selectedIndex] : null;
    }

    /**
     * Purchases an item.
     * @param player the player
     * @return true if the item was purchased, false otherwise
     */
    public boolean purchaseItem(Player player) {
        if (currentPage == 0) {
            StoreItem item = getSelectedItem();
            if (item != null && !item.isPurchased() && player.getMoney() >= item.getPrice()) {
                player.spendMoney(item.getPrice());
                item.purchase();
                
                // Apply the upgrade effect
                switch (item.getName()) {
                    case "Speed Boost":
                        player.setSpeed(player.getSpeed() + 2);
                        break;
                    case "Faster Pizza":
                        PizzaManager.getInstance(null).setCookingSpeedMultiplier(2.0);
                        break;
                    case "VIP Spawn":
                        NPCFactory.setVipSpawnChance(0.20);
                        break;
                    case "Police Patrol":
                        NPCFactory.setRobberSpawnChance(0.025); // Lower robber spawn chance to 2.5%
                        break;
                    default:
                        // No effect for unknown items
                        break;
                }
                return true;
            }
        } else {
            String pizzaType = getSelectedPizzaType();
            if (pizzaType != null) {
                return pizzaStore.purchasePizza(pizzaType, player);
            }
        }
        return false;
    }

    /**
     * Resets the store.
     */
    public void reset() {
        selectedIndex = 0;
        currentPage = 0;
        for (StoreItem item : items) {
            item.reset();
        }
        pizzaStore.reset();
        NPCFactory.resetSpawnChances(); // Reset NPC spawn chances to default
        notifyObservers();
    }

    /**
     * Sets the page.
     * @param page the new page
     */
    public void setPage(int page) {
        if (page >= 0 && page <= 1) {
            this.currentPage = page;
            this.selectedIndex = 0;
            notifyObservers();
        }
    }

    /**
     * Gets the current page.
     * @return the current page
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Gets the pizza store.
     * @return the pizza store
     */
    public PizzaStore getPizzaStore() {
        return pizzaStore;
    }

    /**
     * Represents an item in the store.
     */
    public static class StoreItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        
        private final String name;
        private final String description;
        private final int price;
        private boolean purchased;

        /**
         * Constructor for StoreItem.
         * @param name the name of the item
         * @param description the description of the item
         * @param price the price of the item
         */
        public StoreItem(String name, String description, int price) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.purchased = false;
        }

        /**
         * Gets the name of the item.
         * @return the name of the item
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the description of the item.
         * @return the description of the item
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the price of the item.
         * @return the price of the item
         */
        public int getPrice() {
            return price;
        }

        /**
         * Checks if the item has been purchased.
         * @return true if the item has been purchased, false otherwise
         */
        public boolean isPurchased() {
            return purchased;
        }

        /**
         * Purchases the item.
         */
        public void purchase() {
            this.purchased = true;
        }

        /**
         * Resets the item.
         */
        public void reset() {
            this.purchased = false;
        }
    }
} 