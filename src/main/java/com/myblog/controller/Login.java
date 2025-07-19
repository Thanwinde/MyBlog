package com.myblog.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.mapper.UserMapper;
import com.myblog.pojo.entity.Oauth2Info;
import com.myblog.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nsh
 * @data 2025/4/21 14:10
 * @description
 **/
@Controller
@RequestMapping
public class Login extends ServiceImpl<UserMapper,User> {

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }


    @PostMapping("/api/login")
    public void login(@RequestBody Map<String,String> mp,
                      HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        String username = mp.get("username");
        String password = mp.get("password");
        User user = lambdaQuery().eq(User::getUsername,username).eq(User::getPassword,password).one();
        if (user==null) {
            response.sendError(403, "密码不正确");
            return;
        }
        String email = user.getEmail();
        HttpSession session = request.getSession();
        session.setAttribute("username",username);
        session.setAttribute("email",email);
        response.setStatus(200);
    }

    @PostMapping("/api/qqsuc")
    public void qqsuc(@RequestBody Oauth2Info info,
                      HttpServletRequest request,
                      HttpServletResponse response) throws IOException {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("access_token",info.getAccessToken());
        paramMap.put("openid",info.getOpenId());
        paramMap.put("oauth_consumer_key","102791113");
        String openId = info.getOpenId();
        String accessToken = info.getAccessToken();
        String s = HttpUtil.get("https://graph.qq.com/user/get_user_info", paramMap);
        JSONObject json = new JSONObject(s);
        int code = (int) json.get("ret");
        if (code!=0){
            response.sendError(403);
            return;
        }
        String nickname = json.get("nickname").toString();
        String figureurl_1 = json.get("figureurl_1").toString();
        User user = lambdaQuery().eq(User::getOpenId,openId).one();
        HttpSession session = request.getSession();
        session.setAttribute("username",nickname);
        session.setAttribute("email","QQOauth");
        if(user == null) {
            User user1 = new User();
            user1.setUsername(nickname);
            user1.setEmail("QQOauth");
            user1.setPassword(RandomUtil.randomString(6));
            user1.setOpenId(openId);
            user1.setHeadImg(figureurl_1);
            user1.setAccessToken(accessToken);
            save(user1);
        }
    }


}
