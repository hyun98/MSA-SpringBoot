package microservices.core.review.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
// indexes : productId와 reviewId로 구성된 복합 비즈니스 키를 인덱스로 생성하는 역할을 함
@Table(
        name = "reviews", 
        indexes = {
        @Index(
                name = "reviews_unique_idx", 
                unique = true, 
                columnList = "productId,reviewId")})    // productId와 reviewId로 구성된 복합 비즈니스 키를 생성
@NoArgsConstructor
@Getter
@Setter
public class ReviewEntity {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int productId;
    private int reviewId;
    private String author;
    private String subject;
    private String content;

    public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }
    
}
