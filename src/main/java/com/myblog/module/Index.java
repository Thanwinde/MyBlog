package com.myblog.module;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nsh
 * @data 2025/4/16 19:49
 * @description
 **/
@Controller
public class Index {
    @ResponseBody
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>TWind博客</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "      background: #f0f2f5;\n" +
                "      color: #333;\n" +
                "    }\n" +
                "    .header {\n" +
                "      background: linear-gradient(135deg, #6dd5ed, #2193b0);\n" +
                "      padding: 40px 20px;\n" +
                "      text-align: center;\n" +
                "      color: #fff;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 800px;\n" +
                "      margin: 20px auto;\n" +
                "      background: #fff;\n" +
                "      padding: 20px;\n" +
                "      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n" +
                "      border-radius: 5px;\n" +
                "    }\n" +
                "    .blog-list {\n" +
                "      margin-top: 20px;\n" +
                "    }\n" +
                "    .blog-item {\n" +
                "      padding: 10px;\n" +
                "      border-bottom: 1px solid #eee;\n" +
                "    }\n" +
                "    .blog-item:last-child {\n" +
                "      border-bottom: none;\n" +
                "    }\n" +
                "    .blog-item a {\n" +
                "      text-decoration: none;\n" +
                "      color: #2193b0;\n" +
                "      font-weight: bold;\n" +
                "    }\n" +
                "    .blog-item a:hover {\n" +
                "      text-decoration: underline;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"header\">\n" +
                "    <h1>欢迎来到TWind的博客</h1>\n" +
                "    <p>I LOVE 5mm!!!</p>\n" +
                "  </div>\n" +
                "  <div class=\"container\">\n" +
                "    <h2>碎碎念</h2>\n" +
                "    <div class=\"blog-list\">\n";

                List<String> list = getBlogs();
                for (String s : list) {
                    String name = s.substring(0,s.lastIndexOf("."));
                    htmlContent +=  "      <div class=\"blog-item\">\n" +
                                    "        <a href= "+ s + ">" + name +"</a>\n" +
                                    "      </div>\n" ;
                }

                htmlContent +=
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }

    public List<String> getBlogs(){
        List<String> fileNames = new ArrayList<>();
        // 使用 PathMatchingResourcePatternResolver 进行资源扫描
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            // 这里采用 ** 表示递归子目录
            Resource[] resources = resolver.getResources("classpath:/static/**/*.html");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName != null) {
                    fileNames.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames;
    }
}
