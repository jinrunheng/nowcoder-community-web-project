package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@Slf4j
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    @Test
    public void testSelectPosts() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }
        Assertions.assertEquals(posts.size(), 10);
        List<DiscussPost> posts1 = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : posts1) {
            System.out.println(post);
        }
        Assertions.assertEquals(discussPostMapper.selectDiscussPostRows(149), posts1.size());
    }

    @Test
    public void testLoginTicketCRUD() {
        LoginTicket testTicket = LoginTicket.builder()
                .userId(101)
                .ticket("test")
                .status(1)
                .expired(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .build();
        // test insert
        Assertions.assertEquals(loginTicketMapper.insertLoginTicket(testTicket), 1);
        // test select
        Assertions.assertNotNull(loginTicketMapper.selectByTicket("test"));
        // test update
        Assertions.assertEquals(loginTicketMapper.updateStatus("test", 0), 1);
        Assertions.assertEquals(loginTicketMapper.selectByTicket("test").getStatus(), 0);
        // delete
        Assertions.assertEquals(loginTicketMapper.deleteByTicket("test"), 1);
    }
}
