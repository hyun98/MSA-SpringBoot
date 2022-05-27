package microservices.core.product.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import microservices.api.core.product.ProductService;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.event.Event;
import microservices.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final ProductService productService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // products-in-0
    @Bean
    public Consumer<Event> products() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            LOG.info("Event data: {}", event.getData());
            switch (event.getEventType()) {
                case CREATE:
                    ProductDTO product = objectMapper.convertValue(event.getData(), ProductDTO.class);
                    LOG.info("Create product with ID: {}", product.getProductId());
                    productService.createProduct(product);
                    break;

                case DELETE:
                    int productId = objectMapper.convertValue(event.getKey(), int.class);
                    LOG.info("Delete recommendations with ProductID: {}", productId);
                    productService.deleteProduct(productId);
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
            LOG.info("Message processing done!");
        };
    }
}
