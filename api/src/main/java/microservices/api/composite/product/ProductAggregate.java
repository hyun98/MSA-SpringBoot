package microservices.api.composite.product;

import lombok.*;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.review.dto.ReviewDTO;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ProductAggregate {
    private final int productId;
    private final String name;
    private final int weight;
    private final List<RecommendationDTO> recommendations;
    private final List<ReviewDTO> reviews;
    private final ServiceAddresses serviceAddresses;

    public ProductAggregate() {
        productId = 0;
        name = null;
        weight = 0;
        recommendations = null;
        reviews = null;
        serviceAddresses = null;
    }
}
