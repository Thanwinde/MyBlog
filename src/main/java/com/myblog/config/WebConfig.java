package com.myblog.config;

import com.myblog.config.interceptor.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    final PathConfig pathConfig;

    final UserContextInterceptor userContextInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/blog/*/assets/**")
                .addResourceLocations("file:" + pathConfig.blogs);
        registry
                .addResourceHandler("/blog/*/htmls/*.html")
                .addResourceLocations("file:" + pathConfig.blogs);
        registry
                .addResourceHandler("/*.html")
                .addResourceLocations("file:" + pathConfig.index);
        registry
                .addResourceHandler("/*.png")
                .addResourceLocations("file:" + pathConfig.index);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/api/AddComment");

    }
}
