package com.myblog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author nsh
 * @data 2025/4/19 22:42
 * @description
 **/
@Controller
public class Index {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}
