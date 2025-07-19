package com.myblog.pojo.entity;

import lombok.Data;

@Data
public class Oauth2Info {
    private String openId;
    private String accessToken;
}