package microservices.core.recommendation.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.core.recommendation.domain.RecommendationEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-18T15:29:47+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class RecommendationMapperImpl implements RecommendationMapper {

    @Override
    public RecommendationDTO entityToDTO(RecommendationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        RecommendationDTO recommendationDTO = new RecommendationDTO();

        recommendationDTO.setRate( entity.getRating() );
        recommendationDTO.setProductId( entity.getProductId() );
        recommendationDTO.setRecommendationId( entity.getRecommendationId() );
        recommendationDTO.setAuthor( entity.getAuthor() );
        recommendationDTO.setContent( entity.getContent() );

        return recommendationDTO;
    }

    @Override
    public RecommendationEntity DTOToEntity(RecommendationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RecommendationEntity recommendationEntity = new RecommendationEntity();

        recommendationEntity.setRating( dto.getRate() );
        recommendationEntity.setProductId( dto.getProductId() );
        recommendationEntity.setRecommendationId( dto.getRecommendationId() );
        recommendationEntity.setAuthor( dto.getAuthor() );
        recommendationEntity.setContent( dto.getContent() );

        return recommendationEntity;
    }

    @Override
    public List<RecommendationDTO> entityListToDTOList(List<RecommendationEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<RecommendationDTO> list = new ArrayList<RecommendationDTO>( entity.size() );
        for ( RecommendationEntity recommendationEntity : entity ) {
            list.add( entityToDTO( recommendationEntity ) );
        }

        return list;
    }

    @Override
    public List<RecommendationEntity> DTOListToEntityList(List<RecommendationDTO> dto) {
        if ( dto == null ) {
            return null;
        }

        List<RecommendationEntity> list = new ArrayList<RecommendationEntity>( dto.size() );
        for ( RecommendationDTO recommendationDTO : dto ) {
            list.add( DTOToEntity( recommendationDTO ) );
        }

        return list;
    }
}
