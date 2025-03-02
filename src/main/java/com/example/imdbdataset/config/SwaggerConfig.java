package com.example.imdbdataset.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI imdbApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("IMDB Dataset API")
                        .description("RESTful API for querying IMDB dataset")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mehran Irandegani")
                                .url("https://www.linkedin.com/in/mehran-irandegani-403216a4/")
                                .email("me.irandegani@gmail.com")));
    }
}