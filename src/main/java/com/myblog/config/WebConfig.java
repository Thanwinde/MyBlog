package com.myblog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    final PathConfig pathConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/blog/**")
          .addResourceLocations("file:" + pathConfig.blogs);
        registry
                .addResourceHandler("/**")
                .addResourceLocations("file:" + pathConfig.index);
    }
}
