package microservices.core.review.services;

import microservices.api.core.review.dto.ReviewDTO;
import microservices.core.review.domain.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    ReviewDTO entityToDTO(ReviewEntity entity);
    
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity DTOToEntity(ReviewDTO dto);

    List<ReviewDTO> entityListToDTOList(List<ReviewEntity> entity);
    List<ReviewEntity> DTOListToEntityList(List<ReviewDTO> api);
}
