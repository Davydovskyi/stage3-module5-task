package com.mjc.school.controller.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI apiEndPointInfo() {
        return new OpenAPI()
                .info(new Info().title("Application Rest API")
                        .description("News Application API")
                        .version("v0.0.1")
                        .contact(new Contact().email("jHh7H@example.com").name("News Management").url("https://github.com/mjc-school"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("News Management Wiki Documentation")
                        .url("https://mjc.wiki.github.org/docs"));
    }
}