package com.myblog.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserContextInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        Object username = session.getAttribute("username");
        Object email =  session.getAttribute("email");
        if(username == null || email == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
        return true;
    }
}
