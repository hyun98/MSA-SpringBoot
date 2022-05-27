package microservices.core.productcomposite;

import microservices.api.composite.product.ProductAggregate;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.core.productcomposite.services.ProductCompositeIntegration;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;


@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductCompositeServiceApplicationTests {
	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getProduct(PRODUCT_ID_OK)).
				thenReturn(just(new ProductDTO(PRODUCT_ID_OK, "name", 1, "mock-address")));
		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new RecommendationDTO(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));
		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new ReviewDTO(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}

	@Test
	public void createCompositeProduct1() {
		RecommendationDTO recommendationDTO = new RecommendationDTO(1, 1, "a", 1, "c", null);
		List<RecommendationDTO> recommendationDTOList = new ArrayList<>();
		recommendationDTOList.add(recommendationDTO);

		ReviewDTO reviewDTO = new ReviewDTO(1, 1, "a", "s", "c", null);
		List<ReviewDTO> reviewDTOList = new ArrayList<>();
		reviewDTOList.add(reviewDTO);

		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				recommendationDTOList, reviewDTOList, null);

		postAndVerifyProduct(compositeProduct, OK);
	}

	@Test
	public void createCompositeProduct2() {
		RecommendationDTO recommendationDTO = new RecommendationDTO(1, 1, "a", 1, "c", null);
		List<RecommendationDTO> recommendationDTOList = new ArrayList<>();
		recommendationDTOList.add(recommendationDTO);

		ReviewDTO reviewDTO = new ReviewDTO(1, 1, "a", "s", "c", null);
		List<ReviewDTO> reviewDTOList = new ArrayList<>();
		reviewDTOList.add(reviewDTO);

		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				recommendationDTOList, reviewDTOList, null);

		postAndVerifyProduct(compositeProduct, OK);
	}

	@Test
	public void deleteCompositeProduct() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				singletonList(new RecommendationDTO(1, 1, "a", 1, "c", null)),
				singletonList(new ReviewDTO(1, 1, "a", "s", "c", null)), null);

		postAndVerifyProduct(compositeProduct, OK);

		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
	}

	@Test
	public void getProductById() {

		getAndVerifyProduct(PRODUCT_ID_OK, OK)
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	public void getProductNotFound() {

		getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

		getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}

	private BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/product-composite/" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
		client.post()
				.uri("/product-composite")
				.body(just(compositeProduct), ProductAggregate.class)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		client.delete()
				.uri("/product-composite/" + productId)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}
}
