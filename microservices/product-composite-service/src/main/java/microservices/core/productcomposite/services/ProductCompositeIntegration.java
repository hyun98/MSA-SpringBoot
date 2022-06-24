package microservices.core.productcomposite.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.api.core.product.ProductService;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.recommendation.RecommendationService;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.review.ReviewService;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.api.event.Event;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;

import static microservices.api.event.Event.Type.CREATE;
import static microservices.api.event.Event.Type.DELETE;
import static reactor.core.publisher.Flux.empty;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final WebClient webClient;
    
    private final ObjectMapper mapper;

    private final String productServiceUrl = "http://product";
    private final String recommendationServiceUrl = "http://recommendation";
    private final String reviewServiceUrl = "http://review";
    
    // for functional
    private final StreamBridge streamBridge;

    @Autowired
    public ProductCompositeIntegration(
            WebClient.Builder webClient,
            ObjectMapper mapper,
            StreamBridge streamBridge
    ) {

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10));
        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        this.mapper = mapper;
        this.streamBridge = streamBridge;
    }
    
    // Product
    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        String url = productServiceUrl + "/product/" + productId;
        LOG.debug("Will call the getProduct API on URL: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .log()
                .onErrorMap(
                        WebClientResponseException.class,
                        ex -> handleException(ex)
                );
    }
    
    
    @Override
    public ProductDTO createProduct(ProductDTO body) {
        streamBridge.send("products-out-0", new Event(CREATE, body.getProductId(), body));
        return body;
    }

    @Override
    public void deleteProduct(int productId) {
        streamBridge.send("products-out-0", new Event(DELETE, productId, null));
    }

    
    // Recommendation
    @Override
    public Flux<RecommendationDTO> getRecommendations(int productId) {
        String url = recommendationServiceUrl + "/recommendation?productId=" + productId;
        LOG.debug("Will call the getRecommendations API on URL: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve().bodyToFlux(RecommendationDTO.class)
                .log()
                .onErrorResume(e -> empty());
    }

    @Override
    public RecommendationDTO createRecommendation(RecommendationDTO body) {
        streamBridge.send("recommendations-out-0", new Event(CREATE, body.getProductId(), body));
        return body;
    }

    @Override
    public void deleteRecommendations(int productId) {
        streamBridge.send("recommendations-out-0", new Event(DELETE, productId, null));
    }

    
    // Review
    @Override
    public Flux<ReviewDTO> getReviews(int productId) {
        String url = reviewServiceUrl + "/review?productId=" + productId;
        LOG.debug("Will call the getReviews API on URL: {}", url);

        return webClient.get().uri(url).retrieve()
                .bodyToFlux(ReviewDTO.class).onErrorResume(error -> empty());
    }

    @Override
    public ReviewDTO createReview(ReviewDTO body) {
        streamBridge.send("reviews-out-0", new Event(CREATE, body.getProductId(), body));
        return body;
    }

    @Override
    public void deleteReviews(int productId) {
        streamBridge.send("reviews-out-0", new Event(DELETE, productId, null));
    }

    public Mono<Health> getProductHealth() {
        return getHealth(productServiceUrl);
    }

    public Mono<Health> getRecommendationHealth() {
        return getHealth(recommendationServiceUrl);
    }

    public Mono<Health> getReviewHealth() {
        return getHealth(reviewServiceUrl);
    }

    public Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve()
                .bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down().build()));
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private Throwable handleException(Throwable ex) {
        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }

}
