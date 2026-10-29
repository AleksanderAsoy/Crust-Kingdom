package inf112.skeleton.view.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.NPC;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.factories.NPCFactory;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;
import inf112.skeleton.model.notifications.OrderPaperNotification;
import inf112.skeleton.view.sprites.EntitySprite;
import inf112.skeleton.view.ui.NotificationManager;


/**
 * Handles all rendering of the game world.
 * This includes:
 * - Tiles (the game board)
 * - Game objects (tables, ovens, etc.)
 * - NPCs
 * - The player
 */
public class WorldRenderer implements GameLogic.GameLogicObserver {
    private final GameConfig config;
    private final GameLogic gameLogic;
    private final int tileSize;
    private static final int DEFAULT_OBJECT_SCALE = 3;
    private final Map<Player, EntitySprite> playerSpriteMap = new HashMap<>();
    private BufferedImage backgroundCache;
    private boolean needsBackgroundUpdate = true;
    private boolean showCollisionMasks = false; // Debug flag for collision masks
    private final NotificationManager notificationManager;
    private boolean hasShownRobberWarning;

    public WorldRenderer(GameConfig config, GameLogic gameLogic) {
        this.config = config;
        this.gameLogic = gameLogic;
        this.tileSize = config.getTileSize();
        this.notificationManager = new NotificationManager();
        this.hasShownRobberWarning = false;
        
        // Register as observer of GameLogic
        gameLogic.addObserver(this);
    }

    @Override
    public void update(GameLogic gameLogic) {
        showCollisionMasks = gameLogic.shouldShowCollisionMasks();
    }

    /**
     * Creates a background cache.
     */
    public void createBackgroundCache() {
        if (backgroundCache != null) {
            backgroundCache.flush();
        }
        backgroundCache = new BufferedImage(config.getScreenWidth(), config.getScreenHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = backgroundCache.createGraphics();
        
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;
        
        while (col < config.getMaxScreenCol() && row < config.getMaxScreenRow()) {
            int tileNum = gameLogic.getTileNum(col, row);
            g2d.drawImage(gameLogic.getTileImage(tileNum), x, y, tileSize, tileSize, null);
            col++;
            x += tileSize;
            
            if (col == config.getMaxScreenCol()) {
                col = 0;
                x = 0;
                row++;
                y += tileSize;
            }
        }
        g2d.dispose();
        needsBackgroundUpdate = false;
    }

    /**
     * Renders the game world.
     * @param g2 The Graphics2D object to draw on
     */
    public void render(Graphics2D g2) {
        // Always draw the background and game elements
        if (needsBackgroundUpdate || backgroundCache == null) {
            createBackgroundCache();
        }
        g2.drawImage(backgroundCache, 0, 0, null);
        drawGameObjects(g2);
        drawNPCs(g2);
        drawPlayer(g2);
        drawOrderPaperNotifications(g2);

        // Check if robbers can now appear and show warning if not shown yet
        if (NPCFactory.canRobbersAppear() && !hasShownRobberWarning) {
            notificationManager.showMessage("Robbers can now appear!");
            hasShownRobberWarning = true;
        }

        // Draw any active notifications
        notificationManager.draw(g2, config.getScreenWidth(), config.getScreenHeight());
    }

    private void drawGameObjects(Graphics2D g2) {
        for (GameObject obj : gameLogic.getGameObjects()) {
            if (obj != null && obj.getImage() != null) {
                // Draw the object image
                g2.drawImage(obj.getImage(), 
                    obj.getX(), 
                    obj.getY(), 
                    tileSize * DEFAULT_OBJECT_SCALE,
                    tileSize * DEFAULT_OBJECT_SCALE, 
                    null);

                // Draw collision mask if debug mode is enabled
                if (showCollisionMasks && obj.getCollisionMask() != null) {
                    drawCollisionMask(g2, obj);
                }
            }
        }
    }

    private void drawCollisionMask(Graphics2D g2, GameObject obj) {
        boolean[][] mask = obj.getCollisionMask();
        if (mask == null) return;

        // Save the original color and set up for semi-transparent red
        Color originalColor = g2.getColor();
        g2.setColor(new Color(255, 0, 0, 128)); // Semi-transparent red

        // Calculate scale factors
        float scaleX = (float)(tileSize * DEFAULT_OBJECT_SCALE) / mask.length;
        float scaleY = (float)(tileSize * DEFAULT_OBJECT_SCALE) / mask[0].length;

        // Draw each pixel of the collision mask
        for (int x = 0; x < mask.length; x++) {
            for (int y = 0; y < mask[0].length; y++) {
                if (mask[x][y]) {
                    g2.fillRect(
                        obj.getX() + (int)(x * scaleX),
                        obj.getY() + (int)(y * scaleY),
                        (int)scaleX + 1,
                        (int)scaleY + 1
                    );
                }
            }
        }

        // Restore the original color
        g2.setColor(originalColor);
    }

    private void drawOrderPaperNotifications(Graphics2D g2) {
        for (OrderPaperNotification notification : gameLogic.getOrderManager().getOrderNotifications()) {
            if (notification.isVisible()) {
                // Center the 32x32 order paper within the 48x48 tile by adding 8 pixels offset
                g2.drawImage(notification.getImage(), 
                    notification.getX() + 8, 
                    notification.getY() + 8, 
                    32, 32, null);
            }
        }
    }

    /**
     * Draws the NPCs.
     * @param g2 The Graphics2D object to draw on
     */
    public void drawNPCs(Graphics2D g2) {
        for (NPC npc : gameLogic.getNPCs()) {
            if (npc != null && npc.getSprite() != null) {
                g2.drawImage(npc.getSprite(),
                    npc.getX(),
                    npc.getY(),
                    tileSize, tileSize, null);
            }
        }
    }

    private void drawPlayer(Graphics2D g2) {
        Player player = gameLogic.getPlayer();
        if (player != null) {
            EntitySprite playerSprite = playerSpriteMap.computeIfAbsent(player, p -> new EntitySprite(p, "Chef"));
            if (playerSprite.getCurrentSprite() != null) {
                g2.drawImage(playerSprite.getCurrentSprite(),
                    player.getX(),
                    player.getY(),
                    tileSize, tileSize, null);
            }
        }
    }

    /**
     * Invalidates the background cache.
     */
    public void invalidateBackgroundCache() {
        needsBackgroundUpdate = true;
    }

    /**
     * Toggles the visibility of collision masks for debugging.
     */
    public void toggleCollisionMasks() {
        showCollisionMasks = !showCollisionMasks;
    }

    /**
     * Returns whether collision masks are visible or not.
     * @return true if collision masks are visible, false otherwise
     */
    public boolean isCollisionMasksVisible() {
        return showCollisionMasks;
    }

}