//package microservices.core.productcomposite.config;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//
//@Component
//@RequiredArgsConstructor
//public class HealthConfig implements HealthIndicator {
//    private static final Logger LOG = LoggerFactory.getLogger(HealthConfig.class);
//
//    private final RestTemplate restTemplate;
//    private final String productServiceUrl;
//    private final String recommendationServiceUrl;
//    private final String reviewServiceUrl;
//
//    @Autowired
//    public HealthConfig(
//            RestTemplate restTemplate,
//            @Value("${app.product-service.host}") String productServiceHost,
//            @Value("${app.product-service.port}") int    productServicePort,
//
//            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
//            @Value("${app.recommendation-service.port}") int    recommendationServicePort,
//
//            @Value("${app.review-service.host}") String reviewServiceHost,
//            @Value("${app.review-service.port}") int    reviewServicePort
//    ) {
//        productServiceUrl        = "http://" + productServiceHost + ":" + productServicePort;
//        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort;
//        reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort;
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public Health health() {
//        healthCheck(productServiceUrl);
//        healthCheck(recommendationServiceUrl);
//        healthCheck(reviewServiceUrl);
//
//        LOG.info("---- All Services are Running ----");
//
//        Health.Builder status = Health.up();
//        return status.build();
//    }
//
//    private void healthCheck(String serviceUrl){
//        while (true) {
//            String check = restTemplate.getForEntity(
//                    serviceUrl + "/actuator/health", String.class).getBody();
//            if(check == "{\"status\":\"UP\"}"){
//                return;
//            }
//            if (check != "") {
//                LOG.info("health message : {}", check);
//                return;
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ie){}
//        }
//    }
//
//    @Deprecated
//    class HealthCheck implements Runnable {
//        String serviceHealthString = "";
//        final String serviceUrl;
//
//        public HealthCheck(String serviceUrl) {
//            this.serviceUrl = serviceUrl;
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                String check = restTemplate.getForEntity(
//                        serviceUrl + "/actuator/health", String.class)
//                        .toString();
//                if(check == "{\"status\":\"UP\"}"){
//                    return;
//                }
//                try{
//                    Thread.sleep(1000);
//                } catch (InterruptedException ie){}
//            }
//        }
//    }
//}
