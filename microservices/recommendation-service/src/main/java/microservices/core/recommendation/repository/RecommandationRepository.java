package microservices.core.recommendation.repository;

import microservices.core.recommendation.domain.RecommendationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecommandationRepository extends CrudRepository<RecommendationEntity, String> {
    Optional<RecommendationEntity> findByProductId(int productId);
}
