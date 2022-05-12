package microservices.core.recommendation.services;

import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.core.recommendation.repository.RecommendationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;


@SpringBootTest(webEnvironment=RANDOM_PORT)
public class RecommendationServiceTests {

	@Autowired
	private WebTestClient client;
	@Autowired
	private RecommendationRepository recommendationRepository;


	@BeforeEach
	public void setupDb() {
		recommendationRepository.deleteAll();
	}

	@Test
	public void getRecommendationsByProductId() {

		//given
		int productId = 1;

		postAndVerifyRecommendation(productId, 1, OK);
		postAndVerifyRecommendation(productId, 2, OK);
		postAndVerifyRecommendation(productId, 3, OK);

		assertEquals(3, recommendationRepository.findByProductId(productId).size());

		getAndVerifyRecommendationsByProductId(productId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].productId").isEqualTo(productId)
				.jsonPath("$[2].recommendationId").isEqualTo(3);
	}

	@Test
	public void duplicateError() {

		int productId = 1;
		int recommendationId = 1;

		postAndVerifyRecommendation(productId, recommendationId, OK)
				.jsonPath("$.productId").isEqualTo(productId)
				.jsonPath("$.recommendationId").isEqualTo(recommendationId);

		assertEquals(1, recommendationRepository.count());

		postAndVerifyRecommendation(productId, recommendationId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Recommendation Id:1");

		assertEquals(1, recommendationRepository.count());
	}

	@Test
	public void deleteRecommendations() {

		int productId = 1;
		int recommendationId = 1;

		postAndVerifyRecommendation(productId, recommendationId, OK);
		assertEquals(1, recommendationRepository.findByProductId(productId).size());

		deleteAndVerifyRecommendationsByProductId(productId, OK);
		assertEquals(0, recommendationRepository.findByProductId(productId).size());

		deleteAndVerifyRecommendationsByProductId(productId, OK);
	}

	@Test
	public void getRecommendationsMissingParameter() {

		getAndVerifyRecommendationsByProductId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/recommendation");
	}

	@Test
	public void getRecommendationsInvalidParameter() {

		getAndVerifyRecommendationsByProductId("?productId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/recommendation");
	}

	@Test
	public void getRecommendationsNotFound() {

		getAndVerifyRecommendationsByProductId("?productId=113", OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getRecommendationsInvalidParameterNegativeValue() {

		int productIdInvalid = -1;

		getAndVerifyRecommendationsByProductId("?productId=" + productIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/recommendation")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}

	private BodyContentSpec getAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
		return getAndVerifyRecommendationsByProductId("?productId=" + productId, expectedStatus);
	}

	private BodyContentSpec getAndVerifyRecommendationsByProductId(String productIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/recommendation" + productIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec postAndVerifyRecommendation(int productId, int recommendationId, HttpStatus expectedStatus) {
		RecommendationDTO recommendation = new RecommendationDTO(productId, recommendationId, "Author " + recommendationId, recommendationId, "Content " + recommendationId, "SA");
		return client.post()
				.uri("/recommendation")
				.body(just(recommendation), RecommendationDTO.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec deleteAndVerifyRecommendationsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/recommendation?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
}
