package com.myblog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author nsh
 * @data 2025/4/17 21:47
 * @description
 **/
@Configuration
public class PathConfig {
    public String blogs;
    public String index;

    public PathConfig(@Value("${htmls.blogs}") String blogs, @Value("${htmls.index}") String index) {
        this.blogs = blogs;
        this.index = index;
    }
}
