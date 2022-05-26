//package microservices.core.productcomposite;
//
//import microservices.api.composite.product.ProductAggregate;
//import microservices.api.composite.product.RecommendationSummary;
//import microservices.api.composite.product.ReviewSummary;
//import microservices.api.core.product.dto.ProductDTO;
//import microservices.api.core.recommendation.dto.RecommendationDTO;
//import microservices.api.core.review.dto.ReviewDTO;
//import microservices.api.event.Event;
//import microservices.core.productcomposite.services.ProductCompositeIntegration;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.stream.function.StreamBridge;
//import org.springframework.cloud.stream.messaging.Processor;
//import org.springframework.cloud.stream.test.binder.MessageCollector;
//import org.springframework.http.HttpStatus;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//import java.util.concurrent.BlockingQueue;
//
//import static java.util.Collections.singletonList;
//import static microservices.api.event.Event.Type.CREATE;
//import static microservices.api.event.Event.Type.DELETE;
//import static microservices.core.productcomposite.MessagingTests.OUTPUT_PRODUCT;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
//import static org.springframework.http.HttpStatus.OK;
//import static reactor.core.publisher.Mono.just;
//
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@EmbeddedKafka(topics = {OUTPUT_PRODUCT}, partitions = 1)
//public class MessagingTests {
//
//    private static final int PRODUCT_ID_OK = 1;
//    private static final int PRODUCT_ID_NOT_FOUND = 2;
//    private static final int PRODUCT_ID_INVALID = 3;
//
//    public static final String OUTPUT_PRODUCT = "products";
////    private static final String GROUP_NAME = "auditGroup";
//
//    @Autowired
//    private WebTestClient client;
//
//    @Autowired
//    private StreamBridge streamBridge;
//    
//    @Autowired
//    private MessageCollector collector;
//
//    BlockingQueue<Event> queueProducts = null;
//    BlockingQueue<Message<?>> queueRecommendations = null;
//    BlockingQueue<Message<?>> queueReviews = null;
//
//    @BeforeEach
//    public void setUp() {
//        
//    }
//
//    @Test
//    public void createCompositeProduct1() {
//
//        ProductAggregate composite = new ProductAggregate(1, "name", 1, null, null, null);
//        postAndVerifyProduct(composite, OK);
//
//        // Assert one expected new product events queued up
//        assertEquals(1, queueProducts.size());
//
//        Event<Integer, ProductDTO> expectedEvent = new Event(CREATE, composite.getProductId(), new ProductDTO(composite.getProductId(), composite.getName(), composite.getWeight(), null));
////        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));
//
//        // Assert none recommendations and review events
//        assertEquals(0, queueRecommendations.size());
//        assertEquals(0, queueReviews.size());
//    }
//
//    @Test
//    public void createCompositeProduct2() {
//
//        ProductAggregate composite = new ProductAggregate(1, "name", 1,
//                singletonList(new RecommendationDTO(1, 1, "a", 1, "c", "a")),
//                singletonList(new ReviewDTO(1, 1, "a", "A", "c", "a")), null);
//
//        postAndVerifyProduct(composite, OK);
//
//        // Assert one create product event queued up
//        assertEquals(1, queueProducts.size());
//
//        Event<Integer, ProductDTO> expectedProductEvent = new Event(CREATE, composite.getProductId(), new ProductDTO(composite.getProductId(), composite.getName(), composite.getWeight(), null));
////        assertThat(queueProducts, receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent)));
//
//        // Assert one create recommendation event queued up
//        assertEquals(1, queueRecommendations.size());
//
//        RecommendationDTO rec = composite.getRecommendations().get(0);
//        Event<Integer, ProductDTO> expectedRecommendationEvent = new Event(CREATE, composite.getProductId(), new RecommendationDTO(composite.getProductId(), rec.getRecommendationId(), rec.getAuthor(), rec.getRate(), rec.getContent(), null));
////        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));
//
//        // Assert one create review event queued up
//        assertEquals(1, queueReviews.size());
//
//        ReviewDTO rev = composite.getReviews().get(0);
//        Event<Integer, ProductDTO> expectedReviewEvent = new Event(CREATE, composite.getProductId(), new ReviewDTO(composite.getProductId(), rev.getReviewId(), rev.getAuthor(), rev.getSubject(), rev.getContent(), null));
////        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
//    }
//
//    @Test
//    public void deleteCompositeProduct() {
//
//        deleteAndVerifyProduct(1, OK);
//
//        // Assert one delete product event queued up
//        assertEquals(1, queueProducts.size());
//
//        Event<Integer, ProductDTO> expectedEvent = new Event(DELETE, 1, null);
////        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));
//
//        // Assert one delete recommendation event queued up
//        assertEquals(1, queueRecommendations.size());
//
//        Event<Integer, ProductDTO> expectedRecommendationEvent = new Event(DELETE, 1, null);
////        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));
//
//        // Assert one delete review event queued up
//        assertEquals(1, queueReviews.size());
//
//        Event<Integer, ProductDTO> expectedReviewEvent = new Event(DELETE, 1, null);
////        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
//    }
//
//    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
//        return collector.forChannel(messageChannel);
//    }
//
//    private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
//        client.post()
//                .uri("/product-composite")
//                .body(just(compositeProduct), ProductAggregate.class)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus);
//    }
//
//    private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
//        client.delete()
//                .uri("/product-composite/" + productId)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus);
//    }
//
//
//}