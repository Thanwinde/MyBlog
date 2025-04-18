package com.myblog.module.blog;

import com.myblog.config.PathConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public List<String> getBlogsList() {
        List<String> result = new ArrayList<>();
        System.out.println(pathConfig.blogsURL);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver ();
        try {
            Resource[] resources = resolver.getResources(pathConfig.blogsURL);
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName != null) {
                    result.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "获取博客失败！",
                    e
            );
        }

        return result;
    }

}
