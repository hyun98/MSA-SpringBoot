package microservices.core.review.services;

import microservices.api.core.review.dto.ReviewDTO;
import microservices.core.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;



@SpringBootTest(webEnvironment=RANDOM_PORT)
class ReviewServiceTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ReviewRepository reviewRepository;

	@BeforeEach
	public void setupDb() {
		reviewRepository.deleteAll();
	}

	@Test
	public void getReviewsByProductId() {

		int productId = 1;

		assertEquals(0, reviewRepository.findByProductId(productId).size());

		postAndVerifyReview(productId, 1, OK);
		postAndVerifyReview(productId, 2, OK);
		postAndVerifyReview(productId, 3, OK);

		assertEquals(3, reviewRepository.findByProductId(productId).size());

		getAndVerifyReviewsByProductId(productId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].productId").isEqualTo(productId)
				.jsonPath("$[1].reviewId").isEqualTo(2);
	}

	@Test
	public void duplicateError() {
		
		int productId = 1;
		int reviewId = 1;
		
		assertEquals(0, reviewRepository.count());

		postAndVerifyReview(productId, reviewId, OK);

		assertEquals(1, reviewRepository.count());

		postAndVerifyReview(productId, reviewId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id:1");

		assertEquals(1, reviewRepository.count());
	}

	@Test
	public void deleteReviews() {
		
		int productId = 1;
		int reviewId = 1;

		postAndVerifyReview(productId, reviewId, OK);
		assertEquals(1, reviewRepository.findByProductId(productId).size());

		deleteAndVerifyReviewsByProductId(productId, OK);
		assertEquals(0, reviewRepository.findByProductId(productId).size());
		
		// 멱등성
		deleteAndVerifyReviewsByProductId(productId, OK);
	}

	@Test
	public void getReviewsMissingParameter() {

		client.get()
				.uri("/review")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review");
	}

	public static byte[] convertObjectToBytes(Object obj) throws IOException {
		ByteArrayOutputStream boas = new ByteArrayOutputStream();
		try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
			ois.writeObject(obj);
			return boas.toByteArray();
		}
	}

	@Test
	public void getReviewsInvalidParameter() {
		
		client.get()
				.uri("/review?productId=no-integer")
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(BAD_REQUEST)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review");
	}

	@Test
	public void getReviewsNotFound() {

		int productIdNotFound = 213;

		client.get()
				.uri("/review?productId=" + productIdNotFound)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getReviewsInvalidParameterNegativeValue() {

		int productIdInvalid = -1;

		client.get()
				.uri("/review?productId=" + productIdInvalid)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/review");
	}

	private BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
	}

	private BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/review" + productIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {
		ReviewDTO review = new ReviewDTO(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId, "SA");
		return client.post()
				.uri("/review")
				.body(BodyInserters.fromValue(review))
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/review?productId=" + productId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}

}
