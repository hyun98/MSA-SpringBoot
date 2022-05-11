package microservices.core.productcomposite.services;

import lombok.RequiredArgsConstructor;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.product.ProductService;
import microservices.core.productcomposite.domain.ProductEntity;
import microservices.core.productcomposite.repository.ProductRepository;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ServiceUtil serviceUtil;
    private final ProductRepository productRepository;
    private final ProductMapper mapper;


    @Override
    public ProductDTO getProduct(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        ProductEntity entity = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));

        ProductDTO response = mapper.entityToDTO(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getProduct: found productId: {}", response.getProductId());

        return response;
    }

    @Override
    public ProductDTO createProduct(ProductDTO body) {
        try {
            ProductEntity productEntity = mapper.DTOToEntity(body);
            ProductEntity newEntity = productRepository.save(productEntity);
            return mapper.entityToDTO(newEntity);
            
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        productRepository.findByProductId(productId).ifPresent(e -> productRepository.delete(e));
    }
}
