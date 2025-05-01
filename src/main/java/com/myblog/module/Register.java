package com.myblog.module;

import cn.hutool.jwt.JWT;
import com.myblog.module.utils.MailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author nsh
 * @data 2025/4/22 11:11
 * @description
 **/
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Register {

    byte[] key = "5mm114514191918".getBytes();

    private final MailUtil mailUtil;

    @PostMapping("/register")
    public String register(@RequestBody Map<String,String> mp) throws MessagingException {
        String nickname = mp.get("username");
        String password = mp.get("password");
        String email = mp.get("email");


        String token = JWT.create()
                .setPayload("username", nickname)
                .setPayload("password", password)
                .setPayload("email", email)
                .setKey(key)
                .sign();

        mailUtil.sendSimpleMail(email,token);

        return "forward:/SendSuc.html";
    }

}
