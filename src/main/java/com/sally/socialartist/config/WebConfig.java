package com.sally.socialartist.config;

import com.sally.socialartist.service.AlmacenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AlmacenamientoService almacenamientoService;

    // Sirve las imagenes subidas desde la carpeta ./uploads en /uploads/**
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(almacenamientoService.getRaiz().toUri().toString());
    }
}
