package microservices.api.core.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Product {
    private final Long productId;
    private final String name;
    private final int weight;
    private final String serviceAddress;

    public Product() {
        productId = 0L;
        name = null;
        weight = 0;
        serviceAddress = null;
    }
    
}
