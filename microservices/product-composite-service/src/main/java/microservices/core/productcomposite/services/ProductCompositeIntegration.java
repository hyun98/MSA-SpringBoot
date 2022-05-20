package microservices.core.productcomposite.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.product.ProductService;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.recommendation.RecommendationService;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.api.core.review.ReviewService;
import microservices.api.event.Event;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static microservices.api.event.Event.Type.CREATE;
import static org.springframework.http.HttpMethod.GET;
import static reactor.core.publisher.Flux.empty;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final WebClient webClient;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;
    
    @Autowired
    private StreamBridge streamBridge;
    

    @Autowired
    public ProductCompositeIntegration(
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int    productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int    recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int    reviewServicePort) {

        this.webClient = WebClient.builder().build();
        this.mapper = mapper;

        productServiceUrl        = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
    }
    
    
    // Product
    @Override
    public Mono<ProductDTO> getProduct(int productId) {
        String url = productServiceUrl + "/" + productId;
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
        streamBridge.send("product-out-0", new Event(CREATE, body.getProductId(), body));
        return body;
    }

    @Override
    public void deleteProduct(int productId) {
        streamBridge.send("product-out-0", new Event(CREATE, productId, null));
    }

    
    // Recommendation
    @Override
    public Flux<RecommendationDTO> getRecommendations(int productId) {
        String url = recommendationServiceUrl + "?productId=" + productId;

        LOG.debug("Will call the getRecommendations API on URL: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve().bodyToFlux(RecommendationDTO.class)
                .log()
                .onErrorResume(e -> empty());
        
        try {

            LOG.debug("Will call the getRecommendations API on URL: {}", url);
            List<RecommendationDTO> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<RecommendationDTO>>() {}).getBody();

            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
            return recommendations;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public RecommendationDTO createRecommendation(RecommendationDTO body) {

        try {
            String url = recommendationServiceUrl;
            LOG.debug("Will post a new recommendation to URL: {}", url);

            RecommendationDTO recommendation = restTemplate.postForObject(url, body, RecommendationDTO.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());

            return recommendation;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    
    // Review
    @Override
    public Flux<ReviewDTO> getReviews(int productId) {
        String url = reviewServiceUrl + "?productId=" + productId;
        LOG.debug("Will call the getReviews API on URL: {}", url);

        return webClient.get().uri(url).retrieve()
                .bodyToFlux(ReviewDTO.class).onErrorResume(error -> empty());
    }

    @Override
    public ReviewDTO createReview(ReviewDTO body) {
        try {
            String url = reviewServiceUrl;
            LOG.debug("Will post a new review to URL: {}", url);

            ReviewDTO review = restTemplate.postForObject(url, body, ReviewDTO.class);
            LOG.debug("Created a review with id: {}", review.getProductId());

            return review;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            LOG.debug("Will call the deleteReviews API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
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
