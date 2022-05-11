package microservices.core.recommendation.repository;

import microservices.core.recommendation.domain.RecommendationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String> {
    List<RecommendationEntity> findByProductId(int productId);
}
