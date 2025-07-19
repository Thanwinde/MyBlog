package com.myblog.controller;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.config.PathConfig;
import com.myblog.pojo.entity.BlogInfo;
import com.myblog.mapper.BlogInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author nsh
 * @data 2025/4/30 20:20
 * @description
 **/
@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
@Slf4j
public class ShowBlog extends ServiceImpl<BlogInfoMapper, BlogInfo> {

    final PathConfig pathConfig;

    @GetMapping("/{category}/{name}")
    public String getBlog( @PathVariable String category, @PathVariable String name ) throws IOException {
        File html = new File(pathConfig.blogs  + category + "/htmls/" + name + ".html");
        File md = new File(pathConfig.blogs  + category + "/" + name + ".md");

        if(!md.exists()){
            return "md文件不存在: " + pathConfig.blogs  + category + "/" + name + ".md";
        }

        if(!html.exists()) {
            log.info("html不存在，创建");
            NewOrUpdate(category,name);

            long lastMod = md.lastModified();
            BlogInfo info = new BlogInfo();
            info.setName(name);
            info.setLastMod(lastMod);
            saveOrUpdate(info);
        }

        long fileTime = md.lastModified();

        BlogInfo info = lambdaQuery().eq(BlogInfo::getName,name).one();

        long dbTime = info.getLastMod();

        if(fileTime > dbTime) {
            log.info("已过期，重更新");
            NewOrUpdate(category,name);
            info.setLastMod(fileTime);
            this.updateById(info);
        }
        return "forward:/blog/" + category + "/htmls/" + name + ".html";
    }

    void NewOrUpdate(String category, String name) throws IOException {
        String cssPath = pathConfig.blogs  + category + "/htmls/" +"theme.txt";
        String outPath = pathConfig.blogs  + category + "/htmls/" + name +".html";
        String mdPath = pathConfig.blogs  + category +'/'+ name +".md";

        ProcessBuilder pb = new ProcessBuilder(
                "typora-export",
                "-g", cssPath,
                "-o", outPath,
                mdPath
        );
        try {
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = proc.waitFor();
            if(exitCode == 0){
                log.info("转化成功！");
            }else {
                log.info("转化失败！");
            }
        } catch (IOException | InterruptedException e) {
            log.error("转化错误！ {}",e.getMessage());
        }
    }
}
