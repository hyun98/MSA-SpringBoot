package microservices.core.review.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.core.review.domain.ReviewEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-06-21T15:57:36+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.11 (AdoptOpenJDK)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewDTO entityToDTO(ReviewEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setProductId( entity.getProductId() );
        reviewDTO.setReviewId( entity.getReviewId() );
        reviewDTO.setAuthor( entity.getAuthor() );
        reviewDTO.setSubject( entity.getSubject() );
        reviewDTO.setContent( entity.getContent() );

        return reviewDTO;
    }

    @Override
    public ReviewEntity DTOToEntity(ReviewDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ReviewEntity reviewEntity = new ReviewEntity();

        reviewEntity.setProductId( dto.getProductId() );
        reviewEntity.setReviewId( dto.getReviewId() );
        reviewEntity.setAuthor( dto.getAuthor() );
        reviewEntity.setSubject( dto.getSubject() );
        reviewEntity.setContent( dto.getContent() );

        return reviewEntity;
    }

    @Override
    public List<ReviewDTO> entityListToDTOList(List<ReviewEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<ReviewDTO> list = new ArrayList<ReviewDTO>( entity.size() );
        for ( ReviewEntity reviewEntity : entity ) {
            list.add( entityToDTO( reviewEntity ) );
        }

        return list;
    }

    @Override
    public List<ReviewEntity> DTOListToEntityList(List<ReviewDTO> api) {
        if ( api == null ) {
            return null;
        }

        List<ReviewEntity> list = new ArrayList<ReviewEntity>( api.size() );
        for ( ReviewDTO reviewDTO : api ) {
            list.add( DTOToEntity( reviewDTO ) );
        }

        return list;
    }
}
