package microservices.core.productcomposite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.springframework.http.HttpMethod.GET;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${api.common.version}")           String apiVersion;
    @Value("${api.common.title}")             String apiTitle;
    @Value("${api.common.description}")       String apiDescription;
    @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
    @Value("${api.common.license}")           String apiLicense;
    @Value("${api.common.licenseUrl}")        String apiLicenseUrl;
    @Value("${api.common.contact.name}")      String apiContactName;
    @Value("${api.common.contact.url}")       String apiContactUrl;
    @Value("${api.common.contact.email}")     String apiContactEmail;
    
    @Bean
    public Docket apiDocumentation() {
        return new Docket(SWAGGER_2)
            .select()
            .apis(basePackage("microservices"))
            .paths(PathSelectors.any())
            .build()
            .globalResponses(GET, emptyList())
            .apiInfo(apiInfo());
        
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(apiTitle)
                .version(apiVersion)
                .description(apiDescription)
                .termsOfServiceUrl(apiTermsOfServiceUrl)
                .contact(new Contact(apiContactName, apiContactUrl, apiContactEmail))
                .license(apiLicense)
                .licenseUrl(apiLicenseUrl)
                .extensions(emptyList())
                .build();
    }
}
