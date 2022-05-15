package microservices.core.product.services;

import javax.annotation.processing.Generated;
import microservices.api.core.product.dto.ProductDTO;
import microservices.core.product.domain.ProductEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-15T22:44:12+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO entityToDTO(ProductEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        productDTO.setProductId( entity.getProductId() );
        productDTO.setName( entity.getName() );
        productDTO.setWeight( entity.getWeight() );

        return productDTO;
    }

    @Override
    public ProductEntity DTOToEntity(ProductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ProductEntity productEntity = new ProductEntity();

        productEntity.setProductId( dto.getProductId() );
        productEntity.setName( dto.getName() );
        productEntity.setWeight( dto.getWeight() );

        return productEntity;
    }
}
