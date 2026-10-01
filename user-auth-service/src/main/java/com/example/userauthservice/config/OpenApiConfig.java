package com.example.userauthservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userAuthOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Auth Service API")
                        .version("1.0")
                        .description("User Auth Service API")
                        .contact(new Contact()
                                .name("User Auth Service API")
                                .email("support@example.com")
                                .url("https://github.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0"))
                );
    }
}
