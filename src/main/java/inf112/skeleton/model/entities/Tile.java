package inf112.skeleton.model.entities;

import java.awt.image.BufferedImage;

/**
 * Represents a tile in the game.
 */
public class Tile {

    private BufferedImage image;

    /**
     * Gets the image of the tile.
     * @return the image of the tile
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image of the tile.
     * @param image the image of the tile
     */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
