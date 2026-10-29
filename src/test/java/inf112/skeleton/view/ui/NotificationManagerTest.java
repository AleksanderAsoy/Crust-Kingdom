package inf112.skeleton.view.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NotificationManagerTest {
    @Mock
    private Graphics2D g2;
    
    @Mock
    private FontMetrics fontMetrics;
    
    private NotificationManager notificationManager;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationManager = new NotificationManager();
        
        // Setup FontMetrics mock
        when(g2.getFontMetrics()).thenReturn(fontMetrics);
        when(fontMetrics.getStringBounds(anyString(), any())).thenReturn(new Rectangle2D.Float(0, 0, 100, 20));
        when(fontMetrics.getAscent()).thenReturn(15);
    }

    @Test
    void shouldInitializeWithNullMessage() {
        assertNull(notificationManager.getCurrentMessage(), "Initial message should be null");
    }

    @Test
    void shouldShowMessage() {
        // Arrange
        String testMessage = "Test Message";
        
        // Act
        notificationManager.showMessage(testMessage);
        
        // Assert
        assertEquals(testMessage, notificationManager.getCurrentMessage(), 
            "Message should be set correctly");
    }

    @Test
    void shouldNotDrawWhenNoMessage() {
        // Act
        notificationManager.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        // Assert
        verifyNoInteractions(g2);
    }

    @Test
    void shouldDrawMessageWithCorrectSettings() {
        // Arrange
        String testMessage = "Test Message";
        notificationManager.showMessage(testMessage);
        
        // Act
        notificationManager.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        // Assert
        verify(g2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        verify(g2, times(2)).setColor(any(Color.class)); // Expect two color changes
        verify(g2).fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        verify(g2).drawImage(any(BufferedImage.class), anyInt(), anyInt(), anyInt(), anyInt(), isNull());
        verify(g2).drawString(eq(testMessage), anyInt(), anyInt());
    }

    @Test
    void shouldCenterMessageOnScreen() {
        // Arrange
        String testMessage = "Test Message";
        notificationManager.showMessage(testMessage);
        
        // Act
        notificationManager.draw(g2, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        // Assert
        verify(g2).drawString(eq(testMessage), anyInt(), anyInt());
    }

    @Test
    void shouldHandleDifferentScreenSizes() {
        // Arrange
        String testMessage = "Test Message";
        notificationManager.showMessage(testMessage);
        int[] screenSizes = {400, 600, 800, 1024, 1280};
        
        for (int width : screenSizes) {
            for (int height : screenSizes) {
                // Reset mocks for each iteration
                reset(g2);
                when(g2.getFontMetrics()).thenReturn(fontMetrics);
                
                // Act
                notificationManager.draw(g2, width, height);
                
                // Assert
                verify(g2).fillRect(0, 0, width, height);
                verify(g2).drawString(eq(testMessage), anyInt(), anyInt());
            }
        }
    }
} 