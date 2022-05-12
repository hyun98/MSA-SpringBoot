package microservices.core.productcomposite.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.product.ProductService;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.recommendation.RecommendationService;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.api.core.review.ReviewService;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService{

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int    productServicePort,

            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int    recommendationServicePort,

            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int    reviewServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl        = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
    }
    
    
    // Product
    @Override
    public ProductDTO getProduct(int productId) {

        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the getProduct API on URL: {}", url);

            ProductDTO product = restTemplate.getForObject(url, ProductDTO.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public ProductDTO createProduct(ProductDTO body) {
        try {
            String url = productServiceUrl;
            LOG.debug("Will post a new product to URL: {}", url);

            ProductDTO product = restTemplate.postForObject(url, body, ProductDTO.class);
            LOG.debug("Created a product with id: {}", product.getProductId());

            return product;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the deleteProduct API on URL: {}", url);

            restTemplate.delete(url);

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    
    // Recommendation
    @Override
    public List<RecommendationDTO> getRecommendations(int productId) {

        try {
            String url = recommendationServiceUrl + "?productId=" + productId;

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
    public List<ReviewDTO> getReviews(int productId) {

        try {
            String url = reviewServiceUrl + "?productId=" + productId;

            LOG.debug("Will call the getReviews API on URL: {}", url);
            List<ReviewDTO> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<ReviewDTO>>() {}).getBody();

            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
            return reviews;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
            return new ArrayList<>();
        }
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

    
    
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(ex));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
                LOG.warn("Error body: {}", ex.getResponseBodyAsString());
                return ex;
        }
    }
}
