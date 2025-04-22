package com.myblog.module.blog;

import cn.hutool.json.JSONArray;
import com.myblog.config.PathConfig;
import com.myblog.module.blog.entity.Blog;
import com.myblog.module.comment.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        ArrayList<String> folderName = new ArrayList<>();
        for(File a : folder) {
            String name = a.getName();
            Blog blog = new Blog();
            ArrayList<String> blogName = new ArrayList<>();
            blog.setCategory(name);
            File[] blogFiles = a.listFiles();
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
