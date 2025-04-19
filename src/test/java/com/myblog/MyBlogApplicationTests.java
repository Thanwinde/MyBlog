package com.myblog;

import cn.hutool.json.JSONArray;
import com.myblog.module.blog.entity.Blog;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyBlogApplicationTests {

    String path = "E:/IT/MyBlogs/blogs";
    @Test
    public void getBlogs(){
        ArrayList<String> files = new ArrayList<>();
        ArrayList<File> folder = new ArrayList<>();
        JSONArray result = new JSONArray();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                folder.add(tempList[i]);
                System.out.println("文件夹：" + tempList[i]);
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
        System.out.println(result);

    }

}
