package com.myblog.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author nsh
 * @data 2025/4/21 14:16
 * @description
 **/
@TableName("user")
@Data
public class User {
    @TableId(value = "id",type = IdType.AUTO)
    Integer id;
    String email;
    String username;
    String password;
}
