package com.myblog.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

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
