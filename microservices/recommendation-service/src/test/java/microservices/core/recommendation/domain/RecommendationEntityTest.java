package microservices.core.recommendation.domain;

import microservices.core.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class RecommendationEntityTest {

    @Autowired
    private RecommendationRepository recommandationRepository;

    private RecommendationEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        recommandationRepository.deleteAll().block();

        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        savedEntity = recommandationRepository.save(entity).block();

        assertEqualsRecommendation(entity, savedEntity);
    }

    @Test
    public void create() {

        RecommendationEntity newEntity = new RecommendationEntity(1, 3, "a", 3, "c");
        recommandationRepository.save(newEntity).block();

        RecommendationEntity foundEntity = recommandationRepository.findById(newEntity.getId()).block();
        assertEqualsRecommendation(newEntity, foundEntity);

        assertEquals(2, recommandationRepository.count().block());
    }

    @Test
    public void update() {
        savedEntity.setAuthor("a2");
        recommandationRepository.save(savedEntity).block();

        RecommendationEntity foundEntity = recommandationRepository.findById(savedEntity.getId()).block();
        assertEquals(1, foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    public void delete() {
        recommandationRepository.delete(savedEntity).block();
        assertFalse(recommandationRepository.existsById(savedEntity.getId()).block());
    }

    @Test
    public void getByProductId() {
        List<RecommendationEntity> entityList = recommandationRepository.findByProductId(savedEntity.getProductId()).collectList().block();

        assertEquals(entityList.size(), 1);
        assertEqualsRecommendation(savedEntity, entityList.get(0));
    }

    @Test
    public void duplicateError() {
        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> {
            recommandationRepository.save(entity).block();
        });
    }

    @Test
    public void optimisticLockError() {

        RecommendationEntity entity1 = recommandationRepository.findById(savedEntity.getId()).block();
        RecommendationEntity entity2 = recommandationRepository.findById(savedEntity.getId()).block();

        entity1.setAuthor("a1");
        recommandationRepository.save(entity1).block();

        try {
            entity2.setAuthor("a2");
            recommandationRepository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        RecommendationEntity updatedEntity = recommandationRepository.findById(savedEntity.getId()).block();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsRecommendation(RecommendationEntity expectedEntity, RecommendationEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(),        actualEntity.getProductId());
        assertEquals(expectedEntity.getRecommendationId(), actualEntity.getRecommendationId());
        assertEquals(expectedEntity.getAuthor(),           actualEntity.getAuthor());
        assertEquals(expectedEntity.getRating(),           actualEntity.getRating());
        assertEquals(expectedEntity.getContent(),          actualEntity.getContent());
    }
}
