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

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final RecommendationRepository repository;

    private final RecommendationMapper mapper;


    @Override
    public RecommendationDTO createRecommendation(RecommendationDTO body) {
        try {
            RecommendationEntity entity = mapper.DTOToEntity(body);
            RecommendationEntity newEntity = repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return mapper.entityToDTO(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
        }
    }

    @Override
    public List<RecommendationDTO> getRecommendations(int productId) {

        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendationEntity> entityList = repository.findByProductId(productId);
        List<RecommendationDTO> list = mapper.entityListToDTOList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRecommendations: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}

