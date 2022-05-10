package microservices.core.recommendation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recommendations")
// productId와 recommendationId 필드로 구성된 복합 비즈니스 키를 위한 고유 복합 인덱스를 생성
@CompoundIndex(
        name = "prod-rec-id", 
        unique = true, def = "{'productId': 1, 'recommendationId': 1}")
@NoArgsConstructor
@Getter
@Setter
public class RecommendationEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int productId;
    private int recommendationId;
    private String author;
    private int rating;
    private String content;

    public RecommendationEntity(int productId, int recommendationId, String author, int rating, String content) {
        this.productId = productId;
        this.recommendationId = recommendationId;
        this.author = author;
        this.rating = rating;
        this.content = content;
    }
    
}
