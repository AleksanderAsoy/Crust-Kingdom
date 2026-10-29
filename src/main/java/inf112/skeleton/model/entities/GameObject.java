package inf112.skeleton.model.entities;

import java.awt.image.BufferedImage;
import java.io.Serial;
import java.io.Serializable;


import inf112.skeleton.model.interfaces.IEntity;
import inf112.skeleton.model.interfaces.IGameObject;

/**
 * Represents a game object in the game world.
 */
public class GameObject implements IGameObject, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private transient BufferedImage image;
    private final int x;
    private final int y;
    private final boolean isSolid;
    private final boolean isInteractable;
    private final String type;
    private transient boolean[][] collisionMask;
    private static final int DEFAULT_OBJECT_SCALE = 3; // Match the rendering scale

    /**
     * Creates a new game object.
     * 
     * @param image the image of the game object
     * @param x the x-position
     * @param y the y-position
     * @param isSolid whether the object is solid
     * @param isInteractable whether the object is interactable
     * @param type the type of the game object
     */
    public GameObject(BufferedImage image, int x, int y, boolean isSolid, boolean isInteractable, String type) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isSolid = isSolid;
        this.isInteractable = isInteractable;
        this.type = type;
        if (image != null) {
            generateCollisionMask();
        }
    }

    private void generateCollisionMask() {
        if (image == null) return;
        
        int width = image.getWidth();
        int height = image.getHeight();
        collisionMask = new boolean[width][height];
        
        int collisionBottom = findCollisionBottom(height, width);
        generateMaskForHeight(height, width, collisionBottom);
    }

    private int findCollisionBottom(int height, int width) {
        int actualBottom = height;
        for (int rowY = height - 1; rowY >= 0; rowY--) {
            if (hasNonTransparentPixelInRow(rowY, width)) {
                actualBottom = rowY;
                break;
            }
        }
        int bottomIgnoreHeight = (int)(height * 0.25);
        return Math.min(actualBottom - bottomIgnoreHeight, height);
    }

    private boolean hasNonTransparentPixelInRow(int y, int width) {
        for (int pixelX = 0; pixelX < width; pixelX++) {
            if ((image.getRGB(pixelX, y) & 0xFF000000) != 0) {
                return true;
            }
        }
        return false;
    }

    private void generateMaskForHeight(int height, int width, int collisionBottom) {
        for (int rowY = 0; rowY < height; rowY++) {
            for (int pixelX = 0; pixelX < width; pixelX++) {
                collisionMask[pixelX][rowY] = rowY < collisionBottom && (image.getRGB(pixelX, rowY) & 0xFF000000) != 0;
            }
        }
    }

    /**
     * Gets the collision mask for the game object.
     * @return The collision mask.
     */
    public boolean[][] getCollisionMask() {
        return collisionMask;
    }
    
    public boolean hasCollisionAt(int localX, int localY) {
        if (collisionMask == null || !isSolid) return false;
        
        // Convert world coordinates to local coordinates, accounting for scaling
        int maskX = (localX - x) / DEFAULT_OBJECT_SCALE;
        int maskY = (localY - y) / DEFAULT_OBJECT_SCALE;
        
        // Check bounds
        if (maskX < 0 || maskX >= collisionMask.length || 
            maskY < 0 || maskY >= collisionMask[0].length) {
            return false;
        }
        
        return collisionMask[maskX][maskY];
    }

    /**
     * Checks if the game object collides with an entity.
     * @param entity The entity to check collision with.
     * @return True if there is a collision, false otherwise.
     */
    public boolean collidesWith(IEntity entity) {
        if (!isSolid || collisionMask == null) return false;
        
        // Get entity bounds
        int entityLeft = entity.getX();
        int entityRight = entity.getX() + entity.getWidth();
        int entityTop = entity.getY();
        int entityBottom = entity.getY() + entity.getHeight();
        
        // Check multiple points along the entity's edges for more accurate collision
        int numPoints = 4; // Number of points to check on each edge
        for (int i = 0; i < numPoints; i++) {
            float t = i / (float)numPoints;
            
            // Check points along the bottom edge
            int bottomX = (int)(entityLeft + (entityRight - entityLeft) * t);
            if (hasCollisionAt(bottomX, entityBottom)) return true;
            
            // Check points along the top edge
            int topX = (int)(entityLeft + (entityRight - entityLeft) * t);
            if (hasCollisionAt(topX, entityTop)) return true;
            
            // Check points along the left edge
            int leftY = (int)(entityTop + (entityBottom - entityTop) * t);
            if (hasCollisionAt(entityLeft, leftY)) return true;
            
            // Check points along the right edge
            int rightY = (int)(entityTop + (entityBottom - entityTop) * t);
            if (hasCollisionAt(entityRight, rightY)) return true;
        }
        
        return false;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public boolean isSolid() {
        return isSolid;
    }

    @Override
    public boolean isInteractable() {
        return isInteractable;
    }

    @Override
    public String getType() {
        return type;
    }
    
    /**
     * Sets the image of the game object.
     * @param image the new image
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        if (image != null) {
            generateCollisionMask();
        }
    }
}
