package rs.acs.uns.sw.sct.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configuration.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * Setting and building up necessary information about API.
     *
     * @return Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Additional info about API.
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Baćova Nekretnina API",
                "Current version of API approved by Baćo!",
                "1.5",
                null,
                "admin@bacovanekretnina.com",
                null,
                null);
    }

}