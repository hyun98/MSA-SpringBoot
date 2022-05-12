package microservices.core.recommendation;

import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.core.recommendation.domain.RecommendationEntity;
import microservices.core.recommendation.services.RecommendationMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTests {

    private RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        RecommendationDTO dto = new RecommendationDTO(1, 2, "a", 4, "C", "adr");

        RecommendationEntity entity = mapper.DTOToEntity(dto);

        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getRecommendationId(), entity.getRecommendationId());
        assertEquals(dto.getAuthor(), entity.getAuthor());
        assertEquals(dto.getRate(), entity.getRating());
        assertEquals(dto.getContent(), entity.getContent());

        RecommendationDTO dto2 = mapper.entityToDTO(entity);

        assertEquals(dto.getProductId(), dto2.getProductId());
        assertEquals(dto.getRecommendationId(), dto2.getRecommendationId());
        assertEquals(dto.getAuthor(), dto2.getAuthor());
        assertEquals(dto.getRate(), dto2.getRate());
        assertEquals(dto.getContent(), dto2.getContent());
        assertNull(dto2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        RecommendationDTO dto = new RecommendationDTO(1, 2, "a", 4, "C", "adr");
        List<RecommendationDTO> dtoList = Collections.singletonList(dto);

        List<RecommendationEntity> entityList = mapper.DTOListToEntityList(dtoList);
        assertEquals(dtoList.size(), entityList.size());

        RecommendationEntity entity = entityList.get(0);

        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getRecommendationId(), entity.getRecommendationId());
        assertEquals(dto.getAuthor(), entity.getAuthor());
        assertEquals(dto.getRate(), entity.getRating());
        assertEquals(dto.getContent(), entity.getContent());

        List<RecommendationDTO> dto2List = mapper.entityListToDTOList(entityList);
        assertEquals(dtoList.size(), dto2List.size());

        RecommendationDTO dto2 = dto2List.get(0);

        assertEquals(dto.getProductId(), dto2.getProductId());
        assertEquals(dto.getRecommendationId(), dto2.getRecommendationId());
        assertEquals(dto.getAuthor(), dto2.getAuthor());
        assertEquals(dto.getRate(), dto2.getRate());
        assertEquals(dto.getContent(), dto2.getContent());
        assertNull(dto2.getServiceAddress());
    }
}
