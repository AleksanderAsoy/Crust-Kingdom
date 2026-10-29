package inf112.skeleton.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import inf112.skeleton.model.orders.Order;

class OrderTest {
    
    @Test
    void shouldCreateOrderWithType() {
        String testType = "Margherita";
        Order order = new Order(testType);
        
        assertEquals(testType, order.getType(), "Order should have the specified type");
    }
    
    @Test
    void shouldGenerateUniqueIds() {
        Order order1 = new Order("Margherita");
        Order order2 = new Order("Pepperoni");
        
        assertNotEquals(order1.getOrderId(), order2.getOrderId(), 
                       "Different orders should have unique IDs");
    }
    
    @Test
    void shouldGenerateSequentialIds() {
        Order order1 = new Order("Margherita");
        Order order2 = new Order("Pepperoni");
        
        assertEquals(order1.getOrderId() + 1, order2.getOrderId(), 
                    "Order IDs should be sequential");
    }
    
    @Test
    void shouldHandleNullType() {
        assertDoesNotThrow(() -> new Order(null), 
                          "Order should handle null type without throwing exception");
    }
} 