package com.nowcoder.community;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@Slf4j
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser() {
        // id = 101; username = liubei
        Assertions.assertEquals(userMapper.selectById(101).getUsername(), "liubei");
        Assertions.assertNotNull(userMapper.selectByName("liubei"));
        Assertions.assertEquals(userMapper.selectByEmail("nowcoder101@sina.com").getUsername(), "liubei");
    }

    @Test
    public void testInsertUser() {
        User test = User.builder()
                .username("test")
                .password("123456")
                .salt("abc")
                .email("test@qq.com")
                .headerUrl("http://www.nowcoder.com/101.png")
                .createTime(new Date())
                .build();
        int rows = userMapper.insertUser(test);
        Assertions.assertEquals(rows, 1);
        userMapper.deleteUser("test");
    }

    @Test
    public void testUpdateUser() {
        User test = User.builder()
                .username("test")
                .password("123456")
                .salt("abc")
                .email("test@qq.com")
                .headerUrl("http://www.nowcoder.com/101.png")
                .createTime(new Date())
                .build();
        userMapper.insertUser(test);
        int id = userMapper.selectByName("test").getId();
        userMapper.updateStatus(id, 1);
        userMapper.updateHeader(id, "http://www.nowcoder.com/102.png");
        userMapper.updatePassword(id, "654321");

        User updatedUser = userMapper.selectById(id);
        Assertions.assertEquals(updatedUser.getStatus(), 1);
        Assertions.assertEquals(updatedUser.getPassword(), "654321");
        Assertions.assertEquals(updatedUser.getHeaderUrl(), "http://www.nowcoder.com/102.png");

        userMapper.deleteUser("test");
    }
}
