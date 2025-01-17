package com.tui.proof;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Set;

@SpringBootApplication
@EnableSwagger2
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.tui.proof.ws"))
                .paths(PathSelectors.regex("/.*(client|order).*"))
                .build()
                .apiInfo(this.apiInfo())
                .useDefaultResponseMessages(false)
                .protocols(Set.of("http"));
    }

    //http://localhost:8080/v2/api-docs
    private ApiInfo apiInfo() {
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder();
        apiInfoBuilder.title("REST API for pilotes order");
        apiInfoBuilder.description("REST API Generation");
        apiInfoBuilder.version("1.0.0");
        apiInfoBuilder.license("GNU GENERAL PUBLIC LICENSE, Version 3");
        apiInfoBuilder.licenseUrl("https://www.gnu.org/licenses/gpl-3.0.en.html");
        return apiInfoBuilder.build();
    }
}
