//package microservices.core.productcomposite;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//import static org.springframework.http.HttpMethod.*;
//
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//                .authorizeExchange()
//                .pathMatchers("/actuator/**").permitAll()
//                .pathMatchers("swagger-ui/**").permitAll()
//                .pathMatchers(POST, "/product-composite/**").hasAuthority("SCOPE_product:write")
//                .pathMatchers(DELETE, "/product-composite/**").hasAuthority("SCOPE_product:write")
//                .pathMatchers(GET, "/product-composite/**").hasAuthority("SCOPE_product:read")
//                .anyExchange().authenticated();
//        return http.build();
//    }
//}
