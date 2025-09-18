package com.practice.base_setup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String TITLE = "Spring Boot Base setup";

    @Bean
    public OpenAPI customOpenAPI() {

        License mitLicense = new License().name("Apache 2.0").url("http://springdoc.org");
        Info info = new Info().title(TITLE).version("1.0")
                .description("Spring Boot Base setup")
                .termsOfService("http://swagger.io/terms/")
                .license(mitLicense);
        return new OpenAPI().info(info)
                .addSecurityItem(new SecurityRequirement()
                        .addList(TITLE))
                .components(new Components().addSecuritySchemes(TITLE, new SecurityScheme()
                        .name(TITLE)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("Bearer").bearerFormat("JWT")));
    }
}
