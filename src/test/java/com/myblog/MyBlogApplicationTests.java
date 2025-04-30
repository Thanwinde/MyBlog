package com.myblog;

import cn.hutool.json.JSONArray;
import com.myblog.config.PathConfig;
import com.myblog.module.blog.entity.Blog;
import com.youbenzi.mdtool.tool.MDTool;
import lombok.RequiredArgsConstructor;
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

    @Test
    public void tran() throws IOException {
        String html = MDTool.markdown2Html(new File("E:/IT/MyBlogs/ReentrantLock的超详细源码解析.md"));
        System.out.println(html);
    }

}
