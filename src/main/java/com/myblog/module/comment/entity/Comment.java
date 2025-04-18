package com.myblog.module.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


/**
 * @author nsh
 * @data 2025/4/18 21:48
 * @description
 **/
@TableName("comment")
@Data
public class Comment {
    @TableId(value = "id",type = IdType.AUTO)
    Long id;
    String content;
    String username;
    Date time;
}
