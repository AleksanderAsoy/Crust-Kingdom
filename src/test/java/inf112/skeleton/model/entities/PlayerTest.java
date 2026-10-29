package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import inf112.skeleton.model.game.GameConfig;
import inf112.skeleton.model.game.GameLogic;

/**
 * Comprehensive test suite for the Player class.
 * Tests initialization, movement, boundaries, money management, and inherited functionality.
 */
class PlayerTest {
    
    @Mock
    private GameConfig config;
    
    @Mock
    private GameLogic gameLogic;
    
    private Player player;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Set up config
        when(config.getScreenWidth()).thenReturn(800);
        when(config.getScreenHeight()).thenReturn(600);
        when(config.getTileSize()).thenReturn(48);
        
        // Create player
        player = new Player(config, gameLogic);
    }
    
    @Nested
    class InitializationTests {
        @Test
        void shouldCreatePlayer() {
            assertNotNull(player, "Player should be created");
        }
        
        @Test
        void shouldInitializeWithDefaultValues() {
            assertEquals(75, player.getX(), "X position should be 75");
            assertEquals(200, player.getY(), "Y position should be 200");
            assertEquals("down", player.getDirection(), "Default direction should be 'down'");
            assertEquals(1, player.getAnimationState(), "Initial animation state should be 1");
            assertTrue(player.isVisible(), "Player should be visible");
        }
        
        @Test
        void shouldResetToDefaultValues() {
            // Arrange
            player.setX(100);
            player.setY(300);
            player.setDirection("up");
            
            // Act
            player.setDefaultValues();
            
            // Assert
            assertEquals(75, player.getX(), "X position should be reset to 75");
            assertEquals(200, player.getY(), "Y position should be reset to 200");
            assertEquals("down", player.getDirection(), "Direction should be reset to 'down'");
        }
    }
    
    @Nested
    class MovementTests {
        @Test
        void shouldMoveUpWhenUpPressed() {
            // Arrange
            int initialY = player.getY();
            
            // Act
            player.updateMovement(true, false, false, false);
            
            // Assert
            assertEquals("up", player.getDirection(), "Direction should be 'up'");
            assertTrue(player.getY() < initialY, "Y position should decrease when moving up");
        }
        
        @Test
        void shouldMoveDownWhenDownPressed() {
            // Arrange
            int initialY = player.getY();
            
            // Act
            player.updateMovement(false, true, false, false);
            
            // Assert
            assertEquals("down", player.getDirection(), "Direction should be 'down'");
            assertTrue(player.getY() > initialY, "Y position should increase when moving down");
        }
        
        @Test
        void shouldMoveLeftWhenLeftPressed() {
            // Arrange
            int initialX = player.getX();
            
            // Act
            player.updateMovement(false, false, true, false);
            
            // Assert
            assertEquals("left", player.getDirection(), "Direction should be 'left'");
            assertTrue(player.getX() < initialX, "X position should decrease when moving left");
        }
        
        @Test
        void shouldMoveRightWhenRightPressed() {
            // Arrange
            int initialX = player.getX();
            
            // Act
            player.updateMovement(false, false, false, true);
            
            // Assert
            assertEquals("right", player.getDirection(), "Direction should be 'right'");
            assertTrue(player.getX() > initialX, "X position should increase when moving right");
        }
        
        @Test
        void shouldNotMoveWhenNoKeyPressed() {
            // Arrange
            int initialX = player.getX();
            int initialY = player.getY();
            
            // Act
            player.updateMovement(false, false, false, false);
            
            // Assert
            assertEquals(initialX, player.getX(), "X position should not change");
            assertEquals(initialY, player.getY(), "Y position should not change");
        }
        
        @Test
        void shouldPrioritizeUpWhenMultipleKeysPressed() {
            // Arrange
            int initialY = player.getY();
            
            // Act
            player.updateMovement(true, true, false, false);
            
            // Assert
            assertEquals("up", player.getDirection(), "Direction should be 'up'");
            assertTrue(player.getY() < initialY, "Y position should decrease when moving up");
        }
        
        @Test
        void shouldPrioritizeLeftWhenLeftAndRightPressed() {
            // Arrange
            int initialX = player.getX();
            
            // Act
            player.updateMovement(false, false, true, true);
            
            // Assert
            assertEquals("left", player.getDirection(), "Direction should be 'left'");
            assertTrue(player.getX() < initialX, "X position should decrease when moving left");
        }
    }
    
    @Nested
    class BoundaryTests {
        @Test
        void shouldNotMoveUpBeyondTopBoundary() {
            // Arrange
            player.setY((int)(2.3 * config.getTileSize()) + 1);
            int initialY = player.getY();
            
            // Act
            player.updateMovement(true, false, false, false);
            
            // Assert
            assertEquals(initialY, player.getY(), "Y position should not change when at top boundary");
        }
        
        @Test
        void shouldNotMoveDownBeyondBottomBoundary() {
            // Arrange
            player.setY(config.getScreenHeight() - config.getTileSize() - 1);
            int initialY = player.getY();
            
            // Act
            player.updateMovement(false, true, false, false);
            
            // Assert
            assertEquals(initialY, player.getY(), "Y position should not change when at bottom boundary");
        }
        
        @Test
        void shouldNotMoveLeftBeyondLeftBoundary() {
            // Arrange
            player.setX(1);
            int initialX = player.getX();
            
            // Act
            player.updateMovement(false, false, true, false);
            
            // Assert
            assertEquals(initialX, player.getX(), "X position should not change when at left boundary");
        }
        
        @Test
        void shouldNotMoveRightBeyondRightBoundary() {
            // Arrange
            player.setX(config.getScreenWidth() - config.getTileSize() - 1);
            int initialX = player.getX();
            
            // Act
            player.updateMovement(false, false, false, true);
            
            // Assert
            assertEquals(initialX, player.getX(), "X position should not change when at right boundary");
        }
    }
    
    @Nested
    class MoneyTests {
        @Test
        void shouldGetMoneyFromGameLogic() {
            when(gameLogic.getMoney()).thenReturn(100);
            assertEquals(100, player.getMoney());
        }
        
        @Test
        void shouldSpendMoney() {
            player.spendMoney(50);
            verify(gameLogic).spendMoney(50);
        }
        
        @Test
        void shouldHandleBankruptcy() {
            when(gameLogic.getMoney()).thenReturn(-10);
            player.spendMoney(100);
            verify(gameLogic).handlePlayerBankruptcy();
        }
        
        @Test
        void shouldAddMoney() {
            player.addMoney(50);
            verify(gameLogic).addMoney(50);
        }
    }
    
    @Nested
    class GameLogicInteractionTests {
        @Test
        void shouldUpdateAnimationWhenMoving() {
            player.updateMovement(true, false, false, false);
            player.update();
            assertTrue(player.getSpriteCounter() > 0);
        }
        
        @Test
        void shouldNotUpdateAnimationWhenNotMoving() {
            int initialSpriteCounter = player.getSpriteCounter();
            player.update();
            assertEquals(initialSpriteCounter, player.getSpriteCounter());
        }
        
        @Test
        void shouldUpdateAnimationState() {
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            assertEquals(2, player.getAnimationState());
        }
        
        @Test
        void shouldToggleAnimationState() {
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            int secondState = player.getAnimationState();
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            int thirdState = player.getAnimationState();
            assertEquals(2, secondState);
            assertEquals(1, thirdState);
        }
        
        @Test
        void shouldSetGameLogic() {
            // Arrange
            GameLogic newGameLogic = mock(GameLogic.class);
            
            // Act
            player.setGameLogic(newGameLogic);
            
            // Assert
            // Verify that the new GameLogic is used by checking money operations
            when(newGameLogic.getMoney()).thenReturn(200);
            assertEquals(200, player.getMoney(), "Should use new GameLogic instance");
        }
    }
    
    @Nested
    class AnimationTests {
        @Test
        void shouldUpdateAnimationState() {
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            assertEquals(2, player.getAnimationState());
        }
        
        @Test
        void shouldToggleAnimationState() {
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            int secondState = player.getAnimationState();
            player.setSpriteCounter(20);
            player.updateMovement(true, false, false, false);
            player.update();
            int thirdState = player.getAnimationState();
            assertEquals(2, secondState);
            assertEquals(1, thirdState);
        }
    }
} 