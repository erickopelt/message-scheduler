package io.opelt.messagescheduler.adapter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import lombok.Getter;
import lombok.Setter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.opelt"))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(Pageable.class, SwaggerPageable.class);
    }

    @Getter
    @Setter
    static class SwaggerPageable {
        private Integer page;
        private Integer size;
        private String sort;
    }
}
