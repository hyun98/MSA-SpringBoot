package microservices.core.product;

import microservices.api.core.product.dto.ProductDTO;
import microservices.core.product.domain.ProductEntity;
import microservices.core.product.services.ProductMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTests {
    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        ProductDTO dto = new ProductDTO(1, "n", 1, "sa");

        ProductEntity entity = mapper.DTOToEntity(dto);

        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getWeight(), entity.getWeight());

        ProductDTO dto2 = mapper.entityToDTO(entity);

        assertEquals(dto.getProductId(), dto2.getProductId());
        assertEquals(dto.getProductId(), dto2.getProductId());
        assertEquals(dto.getName(),      dto2.getName());
        assertEquals(dto.getWeight(),    dto2.getWeight());
        assertNull(dto2.getServiceAddress());
    }
}
