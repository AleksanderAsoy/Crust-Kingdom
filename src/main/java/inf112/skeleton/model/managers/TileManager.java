package inf112.skeleton.model.managers;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import inf112.skeleton.model.entities.Tile;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.view.ui.ImageLoader;

public class TileManager implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Logger logger = Logger.getLogger(TileManager.class.getName());

    private final GameConfig config;
    private final transient Tile[] tile;
    private final int[][] mapTileNum;
    private final Map<Integer, Map<Integer, String>> interactableTileCache;

    private static final String DEFAULT_MAP_PATH = "/maps/map01.txt";

    /**
     * Constructor for TileManager.
     * @param config the game configuration
     * @param mapPath the path to the map file (optional, defaults to "/maps/map01.txt")
     */
    public TileManager(GameConfig config, String mapPath) {
        this.config = config;
        this.tile = new Tile[10];
        this.mapTileNum = new int[config.getMaxScreenCol()][config.getMaxScreenRow()];
        this.interactableTileCache = new HashMap<>();

        getTileImage();
        loadMap(mapPath != null ? mapPath : DEFAULT_MAP_PATH);

        addInteractableTile("oven", 1, 3);
        addInteractableTile("oven", 2, 3);
        addInteractableTile("Store", 7, 3);
        addInteractableTile("Store", 8, 3);
        addInteractableTile("counter", 2, 4);
    }

    /**
     * Constructor for TileManager that uses the default map path.
     * @param config the game configuration
     */
    public TileManager(GameConfig config) {
        this(config, DEFAULT_MAP_PATH);
    }

    /**
     * Adds an interactable tile to the map.
     * @param type the type of tile
     * @param col the column of the tile
     * @param row the row of the tile
     */
    public void addInteractableTile(String type, int col, int row) {
        interactableTileCache.computeIfAbsent(col, k -> new HashMap<>()).put(row, type);
    }

    /**
     * Checks if a tile is interactable.
     * @param col the column of the tile
     * @param row the row of the tile
     * @return true if the tile is interactable, false otherwise
     */
    public boolean isTileInteractable(int col, int row) {
        Map<Integer, String> rowMap = interactableTileCache.get(col);
        return rowMap != null && rowMap.containsKey(row);
    }

    /**
     * Gets the type of tile.
     * @param col the column of the tile
     * @param row the row of the tile
     * @return the type of the tile
     */
    public String getTileType(int col, int row) {
        Map<Integer, String> rowMap = interactableTileCache.get(col);
        return rowMap != null ? rowMap.get(row) : null;
    }

    private void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].setImage(ImageLoader.loadImage("tiles/Floor.png"));

            tile[1] = new Tile();
            tile[1].setImage(ImageLoader.loadImage("tiles/Wall.png"));

            tile[2] = new Tile();
            tile[2].setImage(ImageLoader.loadImage("tiles/Wall_skirt.png"));

            tile[3] = new Tile();
            tile[3].setImage(ImageLoader.loadImage("tiles/Floor_skirt.png"));

            tile[4] = new Tile();
            tile[4].setImage(ImageLoader.loadImage("tiles/Floor_shadow.png"));

        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> String.format("Error while loading tile-images: %s", e.getMessage()));
        }
    }

    private void loadMap(String filePath) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            assert is != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col;
            int row = 0;

            while (row < config.getMaxScreenRow()) {
                String line = br.readLine();
                String[] numbers = line.split(" ");

                for (col = 0; col < config.getMaxScreenCol(); col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                }

                row++;
            }

            br.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> String.format("Error while loading map: %s", e.getMessage()));
        }
    }

    /**
     * Gets the tile number at a specific position.
     * @param col the column of the tile
     * @param row the row of the tile
     * @return the tile number
     */
    public int getMapTileNum(int col, int row) {
        if (col >= 0 && col < config.getMaxScreenCol() && row >= 0 && row < config.getMaxScreenRow()) {
            return mapTileNum[col][row];
        }
        return 0;
    }

    /**
     * Gets the tile image at a specific tile number.
     * @param tileNum the tile number
     * @return the tile image
     */
    public BufferedImage getTileImage(int tileNum) {
        if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null) {
            return tile[tileNum].getImage();
        }
        return null;
    }
}
