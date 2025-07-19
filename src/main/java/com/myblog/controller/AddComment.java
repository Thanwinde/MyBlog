package com.myblog.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.pojo.entity.Comment;
import com.myblog.mapper.CommentMapper;
import com.myblog.utils.Investigate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;


/**
 * @author nsh
 * @data 2025/4/18 21:47
 * @description
 **/
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AddComment extends ServiceImpl<CommentMapper, Comment> {

    final Investigate investigate;
    @PostMapping("/addComment")
    public void addComment(@RequestBody String strJson) {
        JSONObject json = JSONUtil.parseObj(strJson);
        String email = json.getStr("email");
        String content = json.getStr("content");
        String username = json.getStr("username");
        try {
            if(!investigate.isLegal(content)){
                content = "此评论涉嫌违规，已被折叠...";
            }
        } catch (Exception e) {
            System.out.println("评论监管失效！" + e.getMessage());
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUsername(username);
        comment.setEmail(email);
        comment.setTime(new Date());
        save(comment);
    }
}
