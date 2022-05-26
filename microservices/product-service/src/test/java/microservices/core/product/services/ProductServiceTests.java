package microservices.core.product.services;

import microservices.api.core.product.ProductService;
import microservices.api.core.product.dto.ProductDTO;
import microservices.core.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;


@SpringBootTest(webEnvironment=RANDOM_PORT)
class ProductServiceTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductRepository productRepository;

	@BeforeEach
	public void setupDb(){
		productRepository.deleteAll().block();
	}

	@Test
	public void getProductById() {
		int productId = 1;

		assertNull(productRepository.findByProductId(productId).block());
		assertEquals(0, productRepository.count().block());

		postAndVerifyProduct(productId, OK);

		assertNotNull(productRepository.findByProductId(productId).block());

		getAndVerifyProduct(productId, OK)
				.jsonPath("$.productId").isEqualTo(productId);
	}

	@Test
	public void getProductNotFound() {

		int productIdNotFound = 13;

		client.get()
				.uri("/product/" + productIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
				.jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
	}

	@Test
	public void duplicateError() {
		int productId = 1;

		assertNull(productRepository.findByProductId(productId).block());

		postAndVerifyProduct(productId, OK);

		assertNotNull(productRepository.findByProductId(productId).block());

		postAndVerifyProduct(productId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/product")
				.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: " + productId);
	}

	@Test
	@DisplayName("DELETE 멱등성 체크")
	public void deleteProduct() {
		int productId = 1;

		postAndVerifyProduct(productId, OK);
		assertNotNull(productRepository.findByProductId(productId).block());

		deleteAndVerifyProduct(productId, OK);
		assertNull(productRepository.findByProductId(productId).block());

		deleteAndVerifyProduct(productId, OK);
	}

	// ** Helper Method **

	private BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return getAndVerifyProduct("/" + productId, expectedStatus);
	}

	private BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/product" + productIdPath)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		ProductDTO product = new ProductDTO(productId, "Name " + productId, productId, "SA");
		return client.post()
				.uri("/product")
				.body(just(product), ProductDTO.class)
//				.body(BodyInserters.fromValue(product))
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/product/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
