package com.myblog.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.pojo.entity.Comment;
import com.myblog.mapper.CommentMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author nsh
 * @data 2025/4/18 21:14
 * @description
 **/
@RequestMapping("/api")
@RestController
public class GetComment extends ServiceImpl<CommentMapper, Comment> {

    @GetMapping("/getComment")
    public JSONObject getComment() {
        List<Comment> comments = this.list();
        JSONObject result = new JSONObject();
        result.put("comments", comments);
        return result;
    }

}
