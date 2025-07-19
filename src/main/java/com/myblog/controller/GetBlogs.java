package com.myblog.controller;

import cn.hutool.json.JSONArray;
import com.myblog.config.PathConfig;
import com.myblog.pojo.entity.Blog;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author nsh
 * @data 2025/4/16 19:59
 * @description
 **/
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GetBlogs {

    final PathConfig pathConfig;

    @GetMapping("/getBlogsList")
    public JSONArray getBlogsList() {
        ArrayList<File> folder = new ArrayList<>();
        JSONArray result = new JSONArray();
        //System.out.println("blogList:" + pathConfig.blogs);
        File file = new File(pathConfig.blogs);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                folder.add(tempList[i]);
                //System.out.println("文件夹：" + tempList[i]);
            }
        }

        for(File a : folder) {
            String name = a.getName();
            Blog blog = new Blog();
            ArrayList<String> blogName = new ArrayList<>();
            blog.setCategory(name);
            File[] files  = a.listFiles();

            List<File> blogFiles = new ArrayList<>();
            if (files != null) {
                Collections.addAll(blogFiles, files);
            }
            blogFiles.sort(

                    Comparator.comparingLong((File f) -> {
                        try {
                            BasicFileAttributes attrs =
                                    Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                            return attrs.creationTime().toMillis();
                        } catch (IOException e) {

                            return f.lastModified();
                        }
                    }).reversed()
            );

            for(File blogFile : blogFiles) {
                if(blogFile.isFile())
                    blogName.add(blogFile.getName());
            }

            blog.setBlogs(blogName);
            result.add(blog);
        }
        //System.out.println(result);

        return result;
    }

}
