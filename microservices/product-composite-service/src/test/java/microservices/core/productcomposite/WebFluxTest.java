package microservices.core.productcomposite;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;

public class WebFluxTest {
    @Test
    public void TestFlux() {
        List<Integer> list = new ArrayList<>();

        Flux.just(1, 2, 3, 4)
                .filter(n -> n % 2 == 0)
                .map(n -> n / 2)
                .log()
                .subscribe(n -> list.add(n));
        
        list.stream().forEach(n -> System.out.println(n));
    }
    
}
