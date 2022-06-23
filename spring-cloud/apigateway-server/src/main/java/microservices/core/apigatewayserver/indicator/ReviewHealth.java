package microservices.core.apigatewayserver.indicator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReviewHealth implements ReactiveHealthIndicator {

    private final WebClient webClient;

    @Value("${app.review-service.host}")
    private String reviewHost;

    @Value("${app.review-service.port}")
    private String reviewPort;

    @Autowired
    public ReviewHealth(final WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Override
    public Mono<Health> health() {
        String url = "http://" + reviewHost + ":" + reviewPort;
        return webClient.get()
                .uri( url + "/actuator/health")
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log();
    }
}
