package com.myblog;

import cn.hutool.core.io.FileUtil;
import com.myblog.config.PathConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
class MyBlogApplicationTests {

    final PathConfig pathConfig;

    @Test
    void NewOrUpdate(String category, String name) throws IOException {
        String cssPath = pathConfig.blogs  + category + "/theme.txt";
        String outPath = pathConfig.blogs  + category + "/htmls/" + name +".html";
        String mdPath = pathConfig.blogs  + category +'/'+ name +".html";
        ProcessBuilder pb = new ProcessBuilder(
                "D:/nodejs/node_modules/npm/bin",
                "typora-export",
                "-g", cssPath,
                "-o", outPath,
                mdPath
        );
        pb.redirectErrorStream(true);
        try {
            // 4. 启动进程
            Process proc = pb.start();

            int exitCode = proc.waitFor();
            System.out.println(exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("error! {}",e.getMessage());
        }
    }

}
