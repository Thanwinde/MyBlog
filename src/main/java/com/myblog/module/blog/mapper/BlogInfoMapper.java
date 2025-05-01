package com.myblog.module.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.module.blog.entity.BlogInfo;
import com.myblog.module.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nsh
 * @data 2025/4/18 21:56
 * @description
 **/
@Mapper
public interface BlogInfoMapper extends BaseMapper<BlogInfo> {
}
