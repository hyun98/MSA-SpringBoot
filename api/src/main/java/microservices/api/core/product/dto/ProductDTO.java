package microservices.api.core.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ProductDTO {
    private int productId;
    private String name;
    private int weight;
    private String serviceAddress;

    public ProductDTO() {
        productId = 0;
        name = null;
        weight = 0;
        serviceAddress = null;
    }
}
