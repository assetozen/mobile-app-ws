package com.assegd.app.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // /** :- to allow all CORS
        // "/users/-email-verification" :- to allow only this path
        registry
                .addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("*");
    }
}
