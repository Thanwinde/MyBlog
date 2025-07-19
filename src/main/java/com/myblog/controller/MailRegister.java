package com.myblog.controller;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.pojo.entity.User;
import com.myblog.mapper.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author nsh
 * @data 2025/4/22 12:37
 * @description
 **/
@Controller
@RequestMapping("/api")
public class MailRegister extends ServiceImpl<UserMapper,User> {

    @GetMapping("/reg")
    public String reg(@RequestParam String token) {
        JWT jwt = JWTUtil.parseToken(token);
        String username = jwt.getPayload("username").toString();
        String email = jwt.getPayload("email").toString();
        String password = jwt.getPayload("password").toString();
        User user = lambdaQuery().eq(User::getEmail,email).one();
        if(user != null){
            return "forward:/error.html";
        }
        User user1 = new User();
        user1.setUsername(username);
        user1.setEmail(email);
        user1.setPassword(password);
        this.save(user1);
        return "forward:/RegSuc.html";
    }

}
