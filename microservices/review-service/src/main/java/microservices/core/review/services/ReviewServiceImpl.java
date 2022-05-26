package microservices.core.review.services;

import lombok.RequiredArgsConstructor;
import microservices.api.core.review.dto.ReviewDTO;
import microservices.api.core.review.ReviewService;
import microservices.core.review.domain.ReviewEntity;
import microservices.core.review.repository.ReviewRepository;
import microservices.util.exceptions.InvalidInputException;
import microservices.util.http.ServiceUtil;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

@RestController
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;

    private final Scheduler scheduler;

    
    @Override
    public Flux<ReviewDTO> getReviews(int productId) {
        
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.info("Will get reviews for product with id={}", productId);

        return asyncFlux(() -> Flux.fromIterable(getByProductId(productId))).log(null, Level.FINE);
    }

    protected List<ReviewDTO> getByProductId(int productId) {
        List<ReviewEntity> entityList = reviewRepository.findByProductId(productId);
        List<ReviewDTO> list = reviewMapper.entityListToDTOList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getReviews: response size: {}", list.size());

        return list;
    }

    @Override
    public ReviewDTO createReview(ReviewDTO body) {
        if (body.getProductId() < 1) throw new InvalidInputException("Invalid productId: " + body.getProductId());

        try {
            ReviewEntity reviewEntity = reviewMapper.DTOToEntity(body);
            ReviewEntity save = reviewRepository.save(reviewEntity);

            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return reviewMapper.entityToDTO(save);
            
        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    /**
     * product에 적힌 모든 review를 삭제
     * @param productId
     */
    @Override
    public void deleteReviews(int productId) {
        if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
    }

    private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
        return Flux.defer(publisherSupplier).subscribeOn(scheduler);
    }
}
