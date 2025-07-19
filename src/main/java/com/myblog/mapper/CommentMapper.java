package com.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nsh
 * @data 2025/4/18 21:56
 * @description
 **/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
