package microservices.api.core.product;

import microservices.api.core.product.dto.ProductDTO;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @PostMapping(
            value    = "/product",
            consumes = "application/json",
            produces = "application/json")
    ProductDTO createProduct(@RequestBody ProductDTO body);

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return the product, if found, else null
     */
    @GetMapping(
            value    = "/product/{productId}",
            produces = "application/json")
    Mono<ProductDTO> getProduct(@PathVariable int productId);

    @DeleteMapping(value = "/product/{productId}")
    void deleteProduct(@PathVariable int productId);
}
