package com.nowcoder.community;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTest {
    @Autowired
    private AlphaService alphaService;

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void testSave1() {
        Assertions.assertThrows(NumberFormatException.class,()->{
            alphaService.save1();
        });

        // 数据会回滚，所以用户和帖子都不会插入到对应的数据库中
        Assertions.assertNull(userService.findUserByEmail("alpha@qq.com"));
        Assertions.assertNull(discussPostService.findDiscussPostById(999));
    }

    @Test
    public void testSave2() {
        Assertions.assertThrows(NumberFormatException.class,()->{
            alphaService.save2();
        });

        // 数据会回滚，所以用户和帖子都不会插入到对应的数据库中
        Assertions.assertNull(userService.findUserByEmail("beta@qq.com"));
        Assertions.assertNull(discussPostService.findDiscussPostById(666));
    }
}
