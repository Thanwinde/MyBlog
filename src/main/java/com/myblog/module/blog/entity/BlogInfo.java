package com.myblog.module.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nsh
 * @data 2025/5/1 14:52
 * @description
 **/
@Data
@TableName("blog_info")
public class BlogInfo {
    @TableId(value = "id",type = IdType.AUTO)
    Long id;
    String name;
    long lastMod;
}
