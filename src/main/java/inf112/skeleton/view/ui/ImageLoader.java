package inf112.skeleton.view.ui;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Loads images from resources
 */
public class ImageLoader {    
    private static final Logger LOGGER = Logger.getLogger(ImageLoader.class.getName());
    private ImageLoader() {}
    
    public static BufferedImage loadImage(String path) {
        try {
            var image = ImageIO.read(Objects.requireNonNull(ImageLoader.class.getClassLoader().getResourceAsStream(path)));
            if (image != null) return image;
        } catch (Exception e) {
            LOGGER.warning("Error while loading image " + path);
        }
        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB); // Fallback
    }

    /**
     * Returns a default 16x16 image
     */
    public static BufferedImage getDefaultImage() {
        return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
    }
} 