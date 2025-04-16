package com.myblog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class MyBlogApplicationTests {

    @Test
    public void getBlogs(){
        List<String> fileNames = new ArrayList<>();
        // 使用 PathMatchingResourcePatternResolver 进行资源扫描
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 这里采用 ** 表示递归子目录
            Resource[] resources = resolver.getResources("classpath:/static/*.html");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName != null) {
                    fileNames.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(fileNames);
    }

}
