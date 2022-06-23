package microservices.core.apigatewayserver;

import lombok.extern.slf4j.Slf4j;
import microservices.core.apigatewayserver.indicator.ProductCompositeHealth;
import microservices.core.apigatewayserver.indicator.ProductHealth;
import microservices.core.apigatewayserver.indicator.RecommendationHealth;
import microservices.core.apigatewayserver.indicator.ReviewHealth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Slf4j
@Configuration
public class HealthCheckConfiguration {
    private final ProductHealth productHealth;

    private final ReviewHealth reviewHealth;

    private final RecommendationHealth recommendationHealth;

    private final ProductCompositeHealth productCompositeHealth;

    @Autowired
    public HealthCheckConfiguration(ProductHealth productHealth, ReviewHealth reviewHealth, RecommendationHealth recommendationHealth, ProductCompositeHealth productCompositeHealth) {
        this.productHealth = productHealth;
        this.reviewHealth = reviewHealth;
        this.recommendationHealth = recommendationHealth;
        this.productCompositeHealth = productCompositeHealth;
    }

    @Bean
    public ReactiveHealthContributor coreServices() {
        return CompositeReactiveHealthContributor.fromMap(Map.of(
                "productCompositeHealthContrib", productCompositeHealth,
                "productHealthContrib", productHealth,
                "recommendationHealthContrib", recommendationHealth,
                "reviewHealthContrib", reviewHealth
        ));
    }
}
