package microservices.core.productcomposite.domain;

import microservices.core.productcomposite.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class productEntityTests {

    @Autowired
    private ProductRepository productRepository;
    private ProductEntity savedEntity;

    @BeforeEach
    public void setupDb() {
        productRepository.deleteAll();
        ProductEntity product = new ProductEntity(1, "n", 1);
        savedEntity = productRepository.save(product);
        assertEqualsProduct(product, savedEntity);
    }

    @Test
    @DisplayName("생성 성공 테스트")
    public void create() {
        //given
        ProductEntity newProduct = new ProductEntity(2, "n", 2);
        savedEntity = productRepository.save(newProduct);
        
        //when
        ProductEntity foundEntity = productRepository.findById(newProduct.getId()).get();

        //then
        assertEqualsProduct(newProduct, foundEntity);
        assertEquals(2, productRepository.count());
    }

    @Test
    @DisplayName("이름 업데이트 성공 테스트")
    public void update() {
        //given
        savedEntity.setName("n2");
        productRepository.save(savedEntity);

        //when
        ProductEntity foundEntity = productRepository.findById(savedEntity.getId()).get();

        //then
        assertEquals(1, foundEntity.getVersion());
        assertEquals("n2", foundEntity.getName());
    }

    @Test
    @DisplayName("삭제 성공 테스트")
    public void delete() {
        //given
        productRepository.delete(savedEntity);

        //then
        // 삭제해도 메모리에는 남아있음을 통해 테스트
        assertFalse(productRepository.existsById(savedEntity.getId()));
    }

    @Test
    @DisplayName("조회 성공 테스트")
    public void getByProductId() {
        //given
        Optional<ProductEntity> foundEntity = productRepository.findByProductId(savedEntity.getProductId());
        
        //then
        assertTrue(foundEntity.isPresent());
        assertEqualsProduct(savedEntity, foundEntity.get());
    }

    @Test
    @DisplayName("중복 키 에러 테스트")
    public void duplicateError() {
        //given
        ProductEntity productEntity = new ProductEntity(savedEntity.getProductId(), "n", 1);

        //when
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> {
            productRepository.save(productEntity);
        });

        System.out.println(thrown);
    }

    @Test
    @DisplayName("낙관적 락 에러 테스트")
    public void optimisticLockError() {
        //given
        ProductEntity productEntity1 = productRepository.findById(savedEntity.getId()).get();
        ProductEntity productEntity2 = productRepository.findById(savedEntity.getId()).get();

        //when
        // 첫 번째 객체 업데이트
        productEntity1.setName("n1");
        productRepository.save(productEntity1);
        
        // 두 번째 객체 업데이트
        // 두 번째 엔티티 객체의 버전이 낮으므로 실패할 것임
        // 낙관적 락 오류가 발생해 실패한다.
        try {
            productEntity2.setName("n2");
            productRepository.save(productEntity2);
            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {

        }
        
        //then
        ProductEntity updatedEntity = productRepository.findById(savedEntity.getId()).get();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getName());
    }

    @Test
    @DisplayName("페이징 테스트")
    public void paging() {
        //given
        productRepository.deleteAll();

        List<ProductEntity> newProducts = IntStream.rangeClosed(1001, 1010).
                mapToObj(i -> new ProductEntity(i, "name " + i, i)).
                collect(Collectors.toList());
        productRepository.saveAll(newProducts);
        
        //when
        Pageable nextPage = PageRequest.of(0, 4, Sort.Direction.ASC, "productId");

        //then
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }


    /**
     * 테스트 헬퍼 메서드 <br>
     * 2개의 product 객체가 서로 완벽히 동일한 객체인지 확인한다.
     * @param expectedEntity
     * @param actualEntity
     * 
     */
    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
    }

    private Pageable testNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
        Page<ProductEntity> productPage = productRepository.findAll(nextPage);
        assertEquals(expectedProductIds, productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, productPage.hasNext());
        return productPage.nextPageable();
    }
}
