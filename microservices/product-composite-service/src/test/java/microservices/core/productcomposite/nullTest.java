package microservices.core.productcomposite;

import microservices.api.composite.product.ProductAggregate;
import org.junit.jupiter.api.Test;

public class nullTest {
    
    @Test
    public void fNullTest() {
        ProductAggregate body = new ProductAggregate(
                1, "a", 100, null, null, null
        );
        
        if(body.getRecommendations() != null){
            System.out.println("Good");
        }
        if (body.getReviews() != null) {
            System.out.println("Good");
        }
    }
}
