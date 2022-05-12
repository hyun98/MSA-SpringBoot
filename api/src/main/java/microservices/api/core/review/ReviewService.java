package microservices.api.core.review;

import microservices.api.core.review.dto.ReviewDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewService {

    /**
     * Sample usage: curl $HOST:$PORT/review?productId=1
     *
     * @param productId
     * @return
     */
    @GetMapping(
            value = "/review",
            produces = "application/json")
    List<ReviewDTO> getReviews(@RequestParam(value = "productId", required = true) int productId);

    @PostMapping(
            value = "/review",
            produces = "application/json",
            consumes = "application/json")
    ReviewDTO createReview(@RequestBody ReviewDTO body);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
