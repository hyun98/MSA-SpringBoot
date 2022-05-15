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
        recommandationRepository.deleteAll();

        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        savedEntity = recommandationRepository.save(entity);

        assertEqualsRecommendation(entity, savedEntity);
    }

    @Test
    public void create() {

        RecommendationEntity newEntity = new RecommendationEntity(1, 3, "a", 3, "c");
        recommandationRepository.save(newEntity);

        RecommendationEntity foundEntity = recommandationRepository.findById(newEntity.getId()).get();
        assertEqualsRecommendation(newEntity, foundEntity);

        assertEquals(2, recommandationRepository.count());
    }

    @Test
    public void update() {
        savedEntity.setAuthor("a2");
        recommandationRepository.save(savedEntity);

        RecommendationEntity foundEntity = recommandationRepository.findById(savedEntity.getId()).get();
        assertEquals(1, foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
    public void delete() {
        recommandationRepository.delete(savedEntity);
        assertFalse(recommandationRepository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByProductId() {
        List<RecommendationEntity> entityList = recommandationRepository.findByProductId(savedEntity.getProductId());

        assertEquals(entityList.size(), 1);
        assertEqualsRecommendation(savedEntity, entityList.get(0));
    }

    @Test
    public void duplicateError() {
        RecommendationEntity entity = new RecommendationEntity(1, 2, "a", 3, "c");
        
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> {
            recommandationRepository.save(entity);
        });
    }

    @Test
    public void optimisticLockError() {

        RecommendationEntity entity1 = recommandationRepository.findById(savedEntity.getId()).get();
        RecommendationEntity entity2 = recommandationRepository.findById(savedEntity.getId()).get();

        entity1.setAuthor("a1");
        recommandationRepository.save(entity1);

        try {
            entity2.setAuthor("a2");
            recommandationRepository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        RecommendationEntity updatedEntity = recommandationRepository.findById(savedEntity.getId()).get();
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