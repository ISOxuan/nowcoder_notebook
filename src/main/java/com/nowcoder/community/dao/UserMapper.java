package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);             //返回插入用户的行数

    int updateStatus(int id, int status);         //更新登陆状态

    int updateHeader(int id, String headerUrl);   //更新头像

    int updatePassword(int id, String password);  //更新密码
}
