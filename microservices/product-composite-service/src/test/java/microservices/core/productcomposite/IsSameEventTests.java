//package microservices.core.productcomposite;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import microservices.api.core.product.dto.ProductDTO;
//import microservices.api.event.Event;
//import org.junit.jupiter.api.Test;
//
//import static microservices.api.event.Event.Type.CREATE;
//import static microservices.api.event.Event.Type.DELETE;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class IsSameEventTests {
//
//    @Test
//    public void testEventObjectCompare() throws JsonProcessingException {
//
//        Event<Integer, ProductDTO> event1 = new Event<>(CREATE, 1, new ProductDTO(1, "name", 1, null));
//        Event<Integer, ProductDTO> event2 = new Event<>(CREATE, 1, new ProductDTO(1, "name", 1, null));
//        Event<Integer, ProductDTO> event3 = new Event<>(DELETE, 1, null);
//        Event<Integer, ProductDTO> event4 = new Event<>(CREATE, 1, new ProductDTO(2, "name", 1, null));
//        
//        checkSameEvent(event1, event2);
//        assertTrue(checkDifferentEvent(event1, event3));
//        assertTrue(checkDifferentEvent(event1, event4));
//    }
//
//    private void checkSameEvent(Event e1, Event e2) {
//        assertEquals(e1.getEventType(), e2.getEventType());
//        assertEquals(e1.getData(), e2.getData());
//        assertEquals(e1.getKey(), e2.getKey());
//    }
//    
//    private boolean checkDifferentEvent(Event e1, Event e2) {
//        if (e1.getEventType() != e2.getEventType() ||
//                 e1.getData() != e2.getData() ||
//                  e1.getKey() != e2.getKey()) {
//            return true;
//        }
//        return false;
//    }
//}
