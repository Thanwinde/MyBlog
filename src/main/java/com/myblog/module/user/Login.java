package com.myblog.module.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.module.user.mapper.UserMapper;
import com.myblog.module.user.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;

/**
 * @author nsh
 * @data 2025/4/21 14:10
 * @description
 **/
@Controller
@RequestMapping("/api")
public class Login extends ServiceImpl<UserMapper,User> {

    @PostMapping("/login")
    public void login(@RequestBody Map<String,String> mp,
                      HttpServletResponse res) throws IOException {
        String username = mp.get("username");
        String password = mp.get("password");
        User user = lambdaQuery().eq(User::getUsername,username).eq(User::getPassword,password).one();
        if (user==null) {
            res.sendError(403, "密码不正确");
            return;
        }
        String email = user.getEmail();
        Cookie cookie = new Cookie("username", username);
        Cookie cookie1 = new Cookie("email", email);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 3600);
        cookie1.setPath("/");
        cookie1.setMaxAge(7 * 24 * 3600);
        res.addCookie(cookie);
        res.addCookie(cookie1);

    }


}
