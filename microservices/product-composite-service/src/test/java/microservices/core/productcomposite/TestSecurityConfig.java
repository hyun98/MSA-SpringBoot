//package microservices.core.productcomposite;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//
//@TestConfiguration
//@WithMockUser
//public class TestSecurityConfig {
//    
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http.csrf().disable().authorizeExchange().anyExchange().permitAll();
//        return http.build();
//    }
//}
