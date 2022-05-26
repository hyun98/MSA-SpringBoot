package microservices.core.productcomposite.services;

import lombok.RequiredArgsConstructor;
import microservices.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class MessageProcessor {

    private final ProductCompositeIntegration productCompositeIntegration;

    // products-out-0
    @Bean
    public Function<Event, Event> products(){
        return e -> e;
    }

    // recommendations-out-0
    @Bean
    public Function<Event, Event> recommendations(){
        return e -> e;
    }

    // reviews-out-0
    @Bean
    public Function<Event, Event> reviews(){
        return e -> e;
    }

}
