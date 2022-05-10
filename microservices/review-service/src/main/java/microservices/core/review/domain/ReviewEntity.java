package microservices.core.review.domain;

import javax.persistence.*;

@Entity
@Table(
        name = "reviews", 
        indexes = {
        @Index(
                name = "reviews_unique_idx", 
                unique = true, 
                columnList = "productId,reviewId")})
public class ReviewEntity {

    @Id
    @GeneratedValue
    private int id;
    
}
