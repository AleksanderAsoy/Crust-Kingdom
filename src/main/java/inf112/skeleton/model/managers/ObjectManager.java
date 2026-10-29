package inf112.skeleton.model.managers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.interfaces.managers.IPizzaManager;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * Handles game objects like tables, ovens, and other interactive elements.
 * This class is responsible for:
 * - Loading and managing game objects
 * - Updating the state of objects
 * - Handling interactions between objects
 */
public class ObjectManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ObjectManager.class.getName());
    private static final int DEFAULT_OBJECT_SCALE = 3;
    private static final boolean ALL_OBJECTS_SOLID = true; // All objects are solid to prevent walking through them
    
    private final GameConfig config;
    private final transient IPizzaManager pizzaManager;
    private final List<GameObject> objects;
    private transient BufferedImage ovenOff;
    private transient BufferedImage ovenOn;
    private transient GameObject oven;

    /**
     * Creates a new ObjectManager.
     * @param config game configuration
     * @param pizzaManager reference to the pizza manager to check the state
     */
    public ObjectManager(GameConfig config, IPizzaManager pizzaManager) {
        this.objects = new ArrayList<>();
        this.config = config;
        this.pizzaManager = pizzaManager;
        loadObjects();
    }

    /**
     * Loads all game objects and sets their initial positions.
     */
    private void loadObjects() {
        try {
            objects.clear();
            loadStaticObjects();
            loadOven();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error while loading objects", e);
        }
    }

    /**
     * Loads static objects like tables and dishes.
     * All objects are solid to prevent the player from walking through them.
     */
    private void loadStaticObjects() {
        addObject("objects/table48.png", 11, 4, ALL_OBJECTS_SOLID, true, "table_upper");
        addObject("objects/table48.png", 11, 8, ALL_OBJECTS_SOLID, true, "table_under");
        addObject("objects/counter_upper.png", 2, 2, ALL_OBJECTS_SOLID, false, "counter_upper");
        addObject("objects/counter_lower.png", 2, 5, ALL_OBJECTS_SOLID, false, "counter_lower");
        addObject("objects/Store.png", 7, 2, ALL_OBJECTS_SOLID, true, "Store");
        addObject("objects/Plant.png", 14, 1, ALL_OBJECTS_SOLID, false, "Plant");
    }

    /**
     * Loads the oven and its states.
     */
    private void loadOven() {
        ovenOff = ImageLoader.loadImage("objects/oven_off.png");
        ovenOn = ImageLoader.loadImage("objects/oven_on.png");
    
        if (ovenOff != null) {
            oven = new GameObject(ovenOff, config.getTileSize(), 2 * config.getTileSize(), true, true, "oven");
            objects.add(oven);
        } else {
            LOGGER.warning(() -> String.format("Could not load oven-images: ovenOff=%s, ovenOn=%s", ovenOff, ovenOn));
        }
    }    

    /**
     * Updates the state of all objects.
     */
    public void update() {
        updateOvenState();
    }

    /**
     * Updates the state of the oven based on whether it is in use.
     */
    private void updateOvenState() {
        if (oven != null) {
            boolean cooking = pizzaManager.isCooking();
            oven.setImage(cooking ? ovenOn : ovenOff);
        }        
    }

    /**
     * Adds a new object to the game.
     */
    private void addObject(String path, int x, int y, boolean isSolid, boolean isInteractable, String name) {
        try {
            BufferedImage image = ImageLoader.loadImage(path);
            objects.add(new GameObject(image, x * config.getTileSize(), y * config.getTileSize(),
                    isSolid, isInteractable, name));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e, () -> String.format("Error loading object: %s from path: %s", name, path));
        }
    }
    

    /**
     * Returns an unmodifiable list of all game objects.
     */
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Draws all objects on the screen.
     */
    public void drawObjects(Graphics2D g2) {
        for (GameObject obj : objects) {
            g2.drawImage(obj.getImage(), obj.getX(), obj.getY(), 
                         config.getTileSize() * DEFAULT_OBJECT_SCALE, 
                         config.getTileSize() * DEFAULT_OBJECT_SCALE, null);
        }
    }
    

    /**
     * Checks if the player is near the oven.
     */
    public boolean isPlayerNearOven(Player player) {
        if (oven == null) return false;

        int interactionDistance = 2 * config.getTileSize();
        int playerCenterX = player.getX() + (config.getTileSize() / 2);
        int playerCenterY = player.getY() + (config.getTileSize() / 2);
        int ovenCenterX = oven.getX() + (config.getTileSize() / 2);
        int ovenCenterY = oven.getY() + (config.getTileSize() / 2);

        return Math.abs(playerCenterX - ovenCenterX) <= interactionDistance &&
               Math.abs(playerCenterY - ovenCenterY) <= interactionDistance;
    }

    public void reset() {
        loadObjects();
    }
}
