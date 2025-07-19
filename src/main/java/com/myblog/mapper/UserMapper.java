package com.myblog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myblog.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author nsh
 * @data 2025/4/21 19:57
 * @description
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
