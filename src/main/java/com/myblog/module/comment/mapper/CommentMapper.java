package com.myblog.module.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.module.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nsh
 * @data 2025/4/18 21:56
 * @description
 **/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
