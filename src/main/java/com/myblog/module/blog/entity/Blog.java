package com.myblog.module.blog.entity;

import lombok.Data;

import java.util.List;

/**
 * @author nsh
 * @data 2025/4/19 16:28
 * @description
 **/
@Data
public class Blog {

    String category;

    List<String> blogs;
}
