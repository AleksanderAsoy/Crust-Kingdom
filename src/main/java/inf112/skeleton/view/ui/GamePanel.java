package inf112.skeleton.view.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import inf112.skeleton.model.game.GameConfig;

/**
 * Main panel for game rendering that handles rendering and game loop
 * <p>
 * This class is responsible for:
 * - Rendering game objects via RenderManager
 * - Handling the game loop with a fixed FPS
 * 
 * @see RenderManager - Handles all rendering and game updates
 */
public class GamePanel extends JPanel implements Runnable {
    private static final int FPS = 60;
    private transient Thread gameThread;
    private final transient RenderManager renderManager;

    /**
     * Creates a new GamePanel with the specified configuration and render manager.
     * 
     * @param config The game configuration
     * @param renderManager The render manager for handling rendering
     */
    public GamePanel(GameConfig config, RenderManager renderManager) {
        this.renderManager = renderManager;
        
        this.setPreferredSize(new Dimension(config.getScreenWidth(), config.getScreenHeight()));
        this.setBackground(Color.pink);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        
        // Connect input to panel
        this.addKeyListener(renderManager.getGameController().keyHandler());
    }

    /**
     * Main panel for game rendering that handles rendering and game loop
     * <p>
     * This class is responsible for:
     * - Rendering game objects via RenderManager
     * - Handling the game loop with a fixed FPS
     * 
     * @see RenderManager - Handles all rendering and game updates
     */
    @Override
    public void paintComponent(Graphics g) {
        if (g == null) {
            return;
        }
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        renderManager.render(g2);
        g2.dispose();
    }

    /**
     * Starts the game thread.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Runs the game loop with a fixed frame rate.
     * This method is called by the game thread and handles:
     * - Updating the game state
     * - Rendering the game
     * - Maintaining a consistent frame rate
     */
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double delta = 0;
        final double ns = 1_000_000_000.0 / FPS;

        while (gameThread != null && !Thread.currentThread().isInterrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                renderManager.update();
                repaint();
                delta--;
            }

            // Use a small sleep to prevent CPU overuse
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        gameThread = null;
    }

    
}
