package microservices.core.review.repository;

import microservices.core.review.domain.ReviewEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    // find 를 통해 가져온 엔티티들을 수정할 수 없음
    @Transactional(readOnly = true)
    List<ReviewEntity> findByProductId(int productId);
}
