package microservices.springcloud.authorizationserver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public class OAuth2AuthorizationServerConfigurationTests {

    @Test
    public void assertOrderHighestPrecedence() {
        Method authorizationServerSecurityFilterChainMethod =
                ClassUtils.getMethod(
                        OAuth2AuthorizationServerConfiguration.class,
                        "authorizationServerSecurityFilterChain",
                        HttpSecurity.class
                );
        Integer order = OrderUtils.getOrder(authorizationServerSecurityFilterChainMethod);
        Assertions.assertEquals(order, Ordered.HIGHEST_PRECEDENCE);
    }
}
