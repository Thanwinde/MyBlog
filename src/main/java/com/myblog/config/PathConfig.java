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
    public  String modulesPath;
    public  String blogs;
    public  String blogsURL;

    public PathConfig(
            @Value("${htmls.modules}") String modulesPath,
            @Value("${htmls.blogs}") String blogs){
        this.modulesPath = "file:" + modulesPath;
        this.blogs       = "file:" + blogs;
        this.blogsURL    = this.blogs + "*.html";
    }
}
