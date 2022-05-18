package microservices.core.recommendation.services;

import lombok.RequiredArgsConstructor;
import microservices.api.core.recommendation.dto.RecommendationDTO;
import microservices.api.core.recommendation.RecommendationService;
import microservices.core.recommendation.domain.RecommendationEntity;
import microservices.core.recommendation.repository.RecommendationRepository;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationRepository recommendationRepository;

    private final RecommendationMapper mapper;


    @Override
    public RecommendationDTO createRecommendation(RecommendationDTO body) {
        
        if(body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());

        RecommendationEntity entity = mapper.DTOToEntity(body);
        Mono<RecommendationDTO> newEntity = recommendationRepository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId()))
                .map(e -> mapper.entityToDTO(e));
        
        return newEntity.block();
    }

    @Override
    public Flux<RecommendationDTO> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        return recommendationRepository.findByProductId(productId)
                .log()
                .map(e -> mapper.entityToDTO(e))
                .map(e -> {
                    e.setServiceAddress(serviceUtil.getServiceAddress());
                    return e;
                });
    }

    @Override
    public void deleteRecommendations(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        recommendationRepository.deleteAll(recommendationRepository.findByProductId(productId)).block();
    }
}

