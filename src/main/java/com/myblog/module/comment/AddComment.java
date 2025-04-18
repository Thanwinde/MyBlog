package com.myblog.module.comment;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.module.comment.entity.Comment;
import com.myblog.module.comment.mapper.CommentMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


/**
 * @author nsh
 * @data 2025/4/18 21:47
 * @description
 **/
@RequestMapping("/api")
@RestController
public class AddComment extends ServiceImpl<CommentMapper, Comment> {

    @PostMapping("/addComment")
    public void addComment(@RequestBody String strJson) {
        JSONObject json = JSONUtil.parseObj(strJson);
        String content = json.getStr("content");
        String username = json.getStr("username");
        System.out.println(json);
        System.out.println(strJson);
        System.out.println(content);
        System.out.println(username);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUsername(username);
        comment.setTime(new Date());
        save(comment);
    }
}
