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
            @Mapping(target = "rate", source = "rating"),
            @Mapping(target = "serviceAddress", ignore = true)
    })
    RecommendationDTO entityToDTO(RecommendationEntity entity);

    @Mappings({
            @Mapping(target = "rating", source="rate"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RecommendationEntity DTOToEntity(RecommendationDTO dto);

    List<RecommendationDTO> entityListToDTOList(List<RecommendationEntity> entity);
    List<RecommendationEntity> DTOListToEntityList(List<RecommendationDTO> dto);
}
