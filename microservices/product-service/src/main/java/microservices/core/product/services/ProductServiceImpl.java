package microservices.core.product.services;

import lombok.RequiredArgsConstructor;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.product.ProductService;
import microservices.core.product.domain.ProductEntity;
import microservices.core.product.repository.ProductRepository;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ServiceUtil serviceUtil;
    private final ProductRepository productRepository;
    private final ProductMapper mapper;


    /**
     * Mono 객체를 반환하며, 처리에 대한 선언을 할 뿐 트리거하진 않는다.
     * 서비스 요청을 받으면 웹플럭스 프레임워크에 의해 트리거된다.
     * @param productId
     * @return
     */
    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        // repo 에서 findByProductId 메서드를 통해 Mono 객체를 찾는다.
        // 찾지 못하면 NotFoundException
        // log 메서드로 log 를 출력
        // mapper.entityToDTO 메서드를 호출해 영속성 계층에서 가져온 엔티티를 DTO 모델 객체로 변환.
        // 마지막 map 메서드는 모델 객체의 serviceAddress 필드에 요청을 처리한 마이크로서비스의 DNS 이름과 IP 주소를 설정한다.
        return productRepository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log()
                .map(e -> mapper.entityToDTO(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public ProductDTO createProduct(ProductDTO body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());
        LOG.info("createProduct 호출");
        
        ProductEntity entity = mapper.DTOToEntity(body);
        System.out.println(entity);
        Mono<ProductDTO> newEntity = productRepository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId())
                )
                .map(e -> mapper.entityToDTO(e));
        
        return newEntity.block();
    }

    @Override
    public void deleteProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        
        // flatmap : entity를 Mono 객체로 바꿔주는 역할
        productRepository.findByProductId(productId)
                .log()
                .map(e -> productRepository.delete(e))
                .flatMap(e -> e)
                .block();
    }
}
