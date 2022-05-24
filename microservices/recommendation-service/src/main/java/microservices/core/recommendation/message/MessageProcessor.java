package microservices.core.recommendation.message;

import lombok.RequiredArgsConstructor;
import microservices.api.core.product.ProductService;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.recommendation.RecommendationService;
import microservices.api.core.recommendation.dto.RecommendationDTO;
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

    private final RecommendationService recommendationService;

    // recommendations-in-0
    @Bean
    public Consumer<Event> recommendations() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            switch (event.getEventType()) {
                case CREATE:
                    RecommendationDTO recommendation = (RecommendationDTO) event.getData();
                    LOG.info("Create Recommendation with ID: {}", recommendation.getProductId());
                    recommendationService.createRecommendation(recommendation);
                    break;

                case DELETE:
                    int productId = (int) event.getKey();
                    LOG.info("Delete recommendations with ProductID: {}", productId);
                    recommendationService.deleteRecommendations(productId);
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
