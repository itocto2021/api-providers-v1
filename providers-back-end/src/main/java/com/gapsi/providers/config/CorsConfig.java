package com.gapsi.providers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gapsi.providers.Utils.Constants;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping(Constants.CORS_MAPPING_PATTERN)
                        .allowedOrigins(Constants.CORS_ORIGIN_3000, Constants.CORS_ORIGIN_3001)
                        .allowedMethods(Constants.CORS_ALLOWED_METHODS)
                        .allowedHeaders(Constants.CORS_ALLOWED_HEADERS)
                        .allowCredentials(true)
                        .maxAge(Constants.CORS_MAX_AGE);
            }
        };
    }
}
