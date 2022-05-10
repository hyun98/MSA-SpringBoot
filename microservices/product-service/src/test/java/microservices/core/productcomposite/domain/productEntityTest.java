package microservices.core.productcomposite.domain;

import microservices.core.productcomposite.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class productEntityTest {

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

//        productRepository.save(productEntity);
//        
//        assertEqualsProduct(productEntity, savedEntity);
//
//        System.out.println("count : " + productRepository.count());
//
//        Optional<ProductEntity> byProductId = productRepository.findByProductId(productEntity.getProductId());
//        System.out.println(byProductId.get().getProductId());

        //when
        DuplicateKeyException thrown = assertThrows(DuplicateKeyException.class, () -> {
            productRepository.save(productEntity);
        });

        System.out.println(thrown);
    }


    /**
     * 테스트 헬퍼 메서드 <br>
     * 2개의 product 객체가 서로 완벽히 동일한 객체인지 확인한다.
     * @param expectedEntity
     * @param actualEntity
     * 
     */
    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
//        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
    }
}
