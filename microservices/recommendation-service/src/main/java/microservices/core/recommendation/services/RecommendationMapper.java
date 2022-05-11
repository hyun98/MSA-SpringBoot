package microservices.core.recommendation.services;

import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.core.recommendation.domain.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mappings({
            @Mapping(target = "rate", source = "entity.rating")
    })
    RecommendationDTO entityToDTO(RecommendationEntity entity);

    @Mapping(target = "rating", source = "dto.rate")
    RecommendationEntity DTOToEntity(RecommendationDTO dto);

    List<RecommendationDTO> entityListToDTOList(List<RecommendationEntity> entity);
    List<RecommendationEntity> DTOListToEntityList(List<RecommendationDTO> dto);
}
