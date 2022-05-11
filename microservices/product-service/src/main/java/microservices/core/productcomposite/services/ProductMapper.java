package microservices.core.productcomposite.services;

import microservices.api.core.product.dto.ProductDTO;
import microservices.core.productcomposite.domain.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    ProductDTO entityToDTO(ProductEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ProductEntity DTOToEntity(ProductDTO dto);
}
