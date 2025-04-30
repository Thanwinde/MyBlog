package com.myblog.module.blog;

import com.myblog.config.PathConfig;
import com.youbenzi.mdtool.tool.MDTool;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * @author nsh
 * @data 2025/4/30 20:20
 * @description
 **/
@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class ShowBlog {

    final PathConfig pathConfig;

    @GetMapping("/{category}/{name:^(?!.*\\.css$).+}")
    public HttpEntity getBlog( @PathVariable String category, @PathVariable String name ) throws IOException {
        File MDfile = new File(pathConfig.blogs + '/' + category + '/' + name + ".md");
        String row = MDTool.markdown2Html(MDfile);
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>关于JVM和OS中的栈帧的区别和内存浅析</title>\n" +
                "  <!-- 外链你的主题 CSS -->\n" +
                "  <link rel=\"stylesheet\" href=\"theme.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                row +
                "</body>\n" +
                "</html>\n";
        return new HttpEntity(html);
    }
}
