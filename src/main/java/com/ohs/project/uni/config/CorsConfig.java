package com.ohs.project.uni.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Value("${frontend_uri}")
    private String frontend_uri;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String domain = "http://localhost:5173";
        registry.addMapping("/**")
                .allowedOrigins(domain, frontend_uri)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
