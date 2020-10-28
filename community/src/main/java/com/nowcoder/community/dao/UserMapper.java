package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    // 修改状态
    int updateStatus(int id, int status);

    // 修改密码
    int updatePassword(int id, String password);

    // 修改用户头像
    int updateHeader(int id, String headerUrl);

    // 通过用户名删除用户
    int deleteUser(String username);
}
