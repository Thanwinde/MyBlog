package com.myblog.controller;

import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author nsh
 * @data 2025/6/2 14:05
 * @description
 **/
@RestController
@RequestMapping
public class getUserInfo {

    @GetMapping("/api/auth/me")
    public String me( HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        Object username = session.getAttribute("username");
        Object email =  session.getAttribute("email");
        if(username == null || email == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        JSONObject json = new JSONObject();
        json.set("username", (String)username);
        json.set("email", (String)email);
        return json.toString();
    }

    @PostMapping("/api/auth/logout")
    public void logout( HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        session.invalidate();
    }
}
