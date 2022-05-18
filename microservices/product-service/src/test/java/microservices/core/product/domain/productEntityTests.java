package microservices.core.product.domain;

import microservices.core.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;


@DataMongoTest
public class productEntityTests {

    @Autowired
    private ProductRepository productRepository;
    private ProductEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        StepVerifier.create(productRepository.deleteAll()).verifyComplete();
        ProductEntity product = new ProductEntity(1, "n", 1);
        StepVerifier.create(productRepository.save(product))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areProductEqual(product, savedEntity);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("생성 성공 테스트")
    public void create() {
        // given
        ProductEntity newEntity = new ProductEntity(2, "n", 2);

        StepVerifier.create(productRepository.save(newEntity))
                .expectNextMatches(createdEntity -> newEntity.getProductId() == createdEntity.getProductId())
                .verifyComplete();

        StepVerifier.create(productRepository.findById(newEntity.getId()))
                .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
                .verifyComplete();

        StepVerifier.create(productRepository.count()).expectNext(2l).verifyComplete();
    }

    @Test
    @DisplayName("이름 업데이트 성공 테스트")
    public void update() {
        savedEntity.setName("n2");
        StepVerifier.create(productRepository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
                .verifyComplete();

        StepVerifier.create(productRepository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("n2"))
                .verifyComplete();
    }

    @Test
    @DisplayName("삭제 성공 테스트")
    public void delete() {
        StepVerifier.create(productRepository.delete(savedEntity))
                .verifyComplete();
        StepVerifier.create(productRepository.existsById(savedEntity.getId()))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void getByProductId() {
        StepVerifier.create(productRepository.findByProductId(savedEntity.getProductId()))
                .expectNextMatches(found -> 
                    areProductEqual(found, savedEntity)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("중복 에러 테스트")
    public void duplicateError() {
        ProductEntity newEntity = new ProductEntity(savedEntity.getProductId(), "n", 1);
        StepVerifier.create(productRepository.save(newEntity))
                .expectError(DuplicateKeyException.class)
                .verify();
    }

    @Test
    public void optimisticLockError() {

        // given
        // 둘 다 같은 version 을 가지고 있다.
        ProductEntity entity1 = productRepository.findById(savedEntity.getId()).block();
        ProductEntity entity2 = productRepository.findById(savedEntity.getId()).block();

        // 첫 번째 객체 업데이트
        entity1.setName("n1");
        productRepository.save(entity1).block();

        // 두 번째 객체 업데이트
        // 두 번째 엔티티 객체의 버전이 낮으므로 실패할 것임
        // 낙관적 락 오류가 발생해 실패한다.
        StepVerifier.create(productRepository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // SELECT 절이 hibernate.jdbc.batch 목록에 입력되면 트랜잭션 commit 이 발생하고 DB 엔티티는 업데이트 된다.
        // 업데이트 된 DB 에서 findById 로 엔티티를 다시 가져오는 작업.
        StepVerifier.create(productRepository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getName().equals("n1"))
                .verifyComplete();
    }

    private boolean areProductEqual(ProductEntity expectedEntity, ProductEntity actualEntity) {
        return
                (expectedEntity.getId().equals(actualEntity.getId())) &&
                        (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                        (expectedEntity.getProductId() == actualEntity.getProductId()) &&
                        (expectedEntity.getName().equals(actualEntity.getName())) &&
                        (expectedEntity.getWeight() == actualEntity.getWeight());
    }
}
