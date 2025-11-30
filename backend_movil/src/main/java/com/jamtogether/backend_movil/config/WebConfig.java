package com.jamtogether.backend_movil.config; // O tu paquete base

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la URL "/uploads/**" a la carpeta física "uploads/" en el disco
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}