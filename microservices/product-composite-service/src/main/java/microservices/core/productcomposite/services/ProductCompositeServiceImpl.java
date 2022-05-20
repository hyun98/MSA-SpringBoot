package microservices.core.productcomposite.services;

import lombok.RequiredArgsConstructor;
import microservices.api.composite.product.*;
import microservices.api.core.product.dto.ProductDTO;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.util.exceptions.NotFoundException;
import microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private  ProductCompositeIntegration integration;

    @Autowired
    public ProductCompositeServiceImpl(ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
        this.serviceUtil = serviceUtil;
        this.integration = integration;
    }

    @Override
    public void createCompositeProduct(ProductAggregate body) {

        try {

            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());
            
            ProductDTO product = new ProductDTO(body.getProductId(), body.getName(), body.getWeight(), null);
            integration.createProduct(product);

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(r -> {
                    RecommendationDTO recommendation = new RecommendationDTO(body.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
                    integration.createRecommendation(recommendation);
                });
            }

            if (body.getReviews() != null) {
                body.getReviews().forEach(r -> {
                    ReviewDTO review = new ReviewDTO(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
                    integration.createReview(review);
                });
            }

            LOG.debug("createCompositeProduct: composite entites created for productId: {}", body.getProductId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public Mono<ProductAggregate> getCompositeProduct(int productId) {

        return Mono.zip(values ->
                        createProductAggregate(
                                (ProductDTO) values[0],
                                (List<RecommendationDTO>) values[1],
                                (List<ReviewDTO>) values[2],
                                serviceUtil.getServiceAddress()),
                        integration.getProduct(productId),
                        integration.getRecommendations(productId).collectList(),
                        integration.getReviews(productId).collectList()
                )
                .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
                .log();
    }
    
    @Override
    public void deleteCompositeProduct(int productId) {

        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

        integration.deleteProduct(productId);

        integration.deleteRecommendations(productId);

        integration.deleteReviews(productId);

        LOG.debug("getCompositeProduct: aggregate entities deleted for productId: {}", productId);
    }

    private ProductAggregate createProductAggregate(ProductDTO product, List<RecommendationDTO> recommendations, List<ReviewDTO> reviews, String serviceAddress) {

        // 1. 상품 정보 in memory
        int productId = product.getProductId();
        String name = product.getName();
        int weight = product.getWeight();

        // 2. 추천 정보 List in memory
        List<RecommendationDTO> recommendationDTOList = (recommendations == null) ? null :
                recommendations.stream()
                        .map(r -> new RecommendationDTO(productId, r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null))
                        .collect(Collectors.toList());

        // 3. 리뷰 정보 list in memory
        List<ReviewDTO> reviewDTOList = (reviews == null)  ? null :
                reviews.stream()
                        .map(r -> new ReviewDTO(productId, r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null))
                        .collect(Collectors.toList());

        // 4. 각 dto에 serviceAddress를 입력하고 마무리
        String productAddress = product.getServiceAddress();
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
        String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

        return new ProductAggregate(productId, name, weight, recommendationDTOList, reviewDTOList, serviceAddresses);
    }
}
