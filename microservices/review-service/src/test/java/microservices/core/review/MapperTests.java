package microservices.core.review;


import microservices.api.core.review.dto.ReviewDTO;
import microservices.core.review.domain.ReviewEntity;
import microservices.core.review.services.ReviewMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTests {

    private ReviewMapper mapper = Mappers.getMapper(ReviewMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        ReviewDTO reviewDTO = new ReviewDTO(1, 2, "a", "s", "C", "adr");

        ReviewEntity reviewEntity = mapper.DTOToEntity(reviewDTO);

        assertEquals(reviewDTO.getProductId(), reviewEntity.getProductId());
        assertEquals(reviewDTO.getReviewId(), reviewEntity.getReviewId());
        assertEquals(reviewDTO.getAuthor(), reviewEntity.getAuthor());
        assertEquals(reviewDTO.getSubject(), reviewEntity.getSubject());
        assertEquals(reviewDTO.getContent(), reviewEntity.getContent());

        ReviewDTO dto2 = mapper.entityToDTO(reviewEntity);

        assertEquals(reviewDTO.getProductId(), dto2.getProductId());
        assertEquals(reviewDTO.getReviewId(), dto2.getReviewId());
        assertEquals(reviewDTO.getAuthor(), dto2.getAuthor());
        assertEquals(reviewDTO.getSubject(), dto2.getSubject());
        assertEquals(reviewDTO.getContent(), dto2.getContent());
        assertNull(dto2.getServiceAddress());
        
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        ReviewDTO dto = new ReviewDTO(1, 2, "a", "s", "C", "adr");
        List<ReviewDTO> dtoList = Collections.singletonList(dto);

        List<ReviewEntity> entityList = mapper.DTOListToEntityList(dtoList);
        assertEquals(dtoList.size(), entityList.size());

        ReviewEntity entity = entityList.get(0);

        assertEquals(dto.getProductId(), entity.getProductId());
        assertEquals(dto.getReviewId(), entity.getReviewId());
        assertEquals(dto.getAuthor(), entity.getAuthor());
        assertEquals(dto.getSubject(), entity.getSubject());
        assertEquals(dto.getContent(), entity.getContent());

        List<ReviewDTO> dto2List = mapper.entityListToDTOList(entityList);
        assertEquals(dtoList.size(), dto2List.size());

        ReviewDTO dto2 = dto2List.get(0);

        assertEquals(dto.getProductId(), dto2.getProductId());
        assertEquals(dto.getReviewId(), dto2.getReviewId());
        assertEquals(dto.getAuthor(), dto2.getAuthor());
        assertEquals(dto.getSubject(), dto2.getSubject());
        assertEquals(dto.getContent(), dto2.getContent());
        assertNull(dto2.getServiceAddress());
    }
    
}
