package com.yc.xiaomi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yc.bean.Address;
import com.yc.bean.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserDao extends BaseMapper<User> {
    @Select("select * from user where email=#{username} or tel=#{username} or username=#{username};")
    User selectUserByTelOREmail(String username);

    //布隆过滤器  查询所有用户id  TODO:暂时在Test中实现
    @Select("select id from user")
    List<String> selectAllId();
}
