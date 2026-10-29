package inf112.skeleton.model.managers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import inf112.skeleton.model.entities.GameObject;
import inf112.skeleton.model.entities.Player;
import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.view.ui.ImageLoader;

/**
 * Tests for the ObjectManager class that handles game objects.
 */
@ExtendWith(MockitoExtension.class)
class ObjectManagerTest {
    @Mock
    private PizzaManager mockPizzaManager;
    
    private ObjectManager objectManager;
    private static final int TEST_TILE_SIZE = 48;

    @BeforeEach
    void setUp() {
        BufferedImage mockImage = new BufferedImage(TEST_TILE_SIZE, TEST_TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        GameConfig config = new GameConfig();
        
        try (MockedStatic<ImageLoader> imageLoader = mockStatic(ImageLoader.class)) {
            imageLoader.when(() -> ImageLoader.loadImage(anyString())).thenReturn(mockImage);
            objectManager = new ObjectManager(config, mockPizzaManager);
        }
    }

    @Nested
    class ObjectInitializationTests {
        @Test
        void shouldInitializeWithCorrectNumberOfObjects() {
            List<GameObject> objects = objectManager.getObjects();
            assertFalse(objects.isEmpty(), "Object list should not be empty at startup");
            assertEquals(7, objects.size(), "There should be 7 objects at startup (6 static + 1 oven)");
        }

        @Test
        void shouldHaveCorrectObjectTypes() {
            List<GameObject> objects = objectManager.getObjects();
            assertTrue(objects.stream().anyMatch(obj -> "oven".equals(obj.getType())), 
                "There should be an oven");
            assertTrue(objects.stream().anyMatch(obj -> "table_upper".equals(obj.getType())), 
                "There should be an upper table");
            assertTrue(objects.stream().anyMatch(obj -> "table_under".equals(obj.getType())), 
                "There should be a lower table");
        }

        @Test
        void shouldNotAllowModificationOfObjectList() {
            List<GameObject> objects = objectManager.getObjects();
            assertThrows(UnsupportedOperationException.class, () -> objects.add(null),
                "Object list should be unmodifiable");
        }
    }

    @Nested
    class OvenStateTests {
        @Test
        void shouldUpdateOvenImageWhenCooking() {
            when(mockPizzaManager.isCooking()).thenReturn(true);
            objectManager.update();
            
            GameObject oven = findObjectByType("oven");
            assertNotNull(oven, "Oven should exist");
            assertNotNull(oven.getImage(), "Oven should have an image");
        }

        @Test
        void shouldUpdateOvenImageWhenNotCooking() {
            when(mockPizzaManager.isCooking()).thenReturn(false);
            objectManager.update();
            
            GameObject oven = findObjectByType("oven");
            assertNotNull(oven, "Oven should exist");
            assertNotNull(oven.getImage(), "Oven should have an image");
        }
    }

    @Nested
    class PlayerInteractionTests {
        @Test
        void shouldDetectPlayerNearOven() {
            Player mockPlayer = mock(Player.class);
            when(mockPlayer.getX()).thenReturn(0);
            when(mockPlayer.getY()).thenReturn(0);
            
            assertTrue(objectManager.isPlayerNearOven(mockPlayer), 
                "Player should be detected near oven");
        }

        @Test
        void shouldNotDetectPlayerFarFromOven() {
            Player mockPlayer = mock(Player.class);
            when(mockPlayer.getX()).thenReturn(1000);
            when(mockPlayer.getY()).thenReturn(1000);
            
            assertFalse(objectManager.isPlayerNearOven(mockPlayer), 
                "Player should not be detected far from oven");
        }
    }

    @Nested
    class ObjectPropertiesTests {
        @Test
        void tableShouldBeSolidAndInteractable() {
            GameObject table = findObjectByType("table_upper");
            assertTrue(table.isSolid(), "Table should be solid");
            assertTrue(table.isInteractable(), "Table should be interactable");
        }

        @Test
        void counterShouldBeSolidButNotInteractable() {
            GameObject counter = findObjectByType("counter_upper");
            assertTrue(counter.isSolid(), "Counter should be solid");
            assertFalse(counter.isInteractable(), "Counter should not be interactable");
        }
    }

    @Nested
    class DrawingTests {
        @Test
        void shouldDrawAllObjects() {
            Graphics2D mockGraphics = mock(Graphics2D.class);
            objectManager.drawObjects(mockGraphics);
            
            verify(mockGraphics, atLeastOnce()).drawImage(any(), anyInt(), anyInt(), anyInt(), anyInt(), isNull());
        }
    }

    private GameObject findObjectByType(String type) {
        return objectManager.getObjects().stream()
            .filter(obj -> type.equals(obj.getType()))
            .findFirst()
            .orElse(null);
    }
} 