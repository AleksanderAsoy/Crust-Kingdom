package inf112.skeleton.view.sprites;

import java.awt.image.BufferedImage;

import inf112.skeleton.model.interfaces.IEntity;
import inf112.skeleton.model.interfaces.ISprite;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * Handles sprite rendering for Entity objects.
 * This is the view implementation of sprite handling.
 */
public class EntitySprite implements ISprite {
    private BufferedImage up1;
    private BufferedImage up2;
    private BufferedImage down1;
    private BufferedImage down2;
    private BufferedImage left1;
    private BufferedImage left2;
    private BufferedImage right1;
    private BufferedImage right2;
    private final IEntity entity;
    private final String entityType;

    public EntitySprite(IEntity entity, String entityType) {
        this.entity = entity;
        this.entityType = entityType;
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Handle different entity types and their paths
            String basePath;
            if (entityType.equals("Chef")) {
                basePath = "player";
            } else {
                // For both Npc and NpcRobber, use the "npc" directory
                basePath = "npc";
            }

            up1 = ImageLoader.loadImage(basePath + "/" + entityType + "_up1.png");
            up2 = ImageLoader.loadImage(basePath + "/" + entityType + "_up2.png");
            down1 = ImageLoader.loadImage(basePath + "/" + entityType + "_down1.png");
            down2 = ImageLoader.loadImage(basePath + "/" + entityType + "_down2.png");
            left1 = ImageLoader.loadImage(basePath + "/" + entityType + "_left1.png");
            left2 = ImageLoader.loadImage(basePath + "/" + entityType + "_left2.png");
            right1 = ImageLoader.loadImage(basePath + "/" + entityType + "_right1.png");
            right2 = ImageLoader.loadImage(basePath + "/" + entityType + "_right2.png");
        } catch (Exception e) {
            BufferedImage defaultImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
            up1 = up2 = down1 = down2 = left1 = left2 = right1 = right2 = defaultImg;
        }
    }

    /**
     * Gets the current sprite.
     * @return the current sprite
     */
    @Override
    public BufferedImage getCurrentSprite() {
        String direction = entity.getDirection();
        int spriteNum = entity.getAnimationState();

        if (direction == null)
            return down1;

        return switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down", "waiting_at_counter" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };
    }

    /**
     * Gets the direction of the entity.
     * @return the direction of the entity
     */
    @Override
    public String getDirection() {
        return entity.getDirection();
    }

    /**
     * Gets the x-coordinate of the entity.
     * @return the x-coordinate of the entity
     */
    @Override
    public int getX() {
        return entity.getX();
    }

    /**
     * Gets the y-coordinate of the entity.
     * @return the y-coordinate of the entity
     */
    @Override
    public int getY() {
        return entity.getY();
    }

    /**
     * Checks if the entity is visible.
     * @return true if the entity is visible, false otherwise
     */
    @Override
    public boolean isVisible() {
        return true; // Standard Entity is always visible, subclasses can override this
    }
    
    /**
     * Gets the down-sprite with the given number (1 or 2).
     * @param num the number of the sprite
     * @return the down-sprite
     */
    protected BufferedImage getDownSprite(int num) {
        return num == 1 ? down1 : down2;
    }

    /**
     * Gets the entity type for this sprite.
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }
}