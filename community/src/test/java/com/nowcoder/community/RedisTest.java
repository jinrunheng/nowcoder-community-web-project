package com.nowcoder.community;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@Slf4j
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        // 存数据
        redisTemplate.opsForValue().set(redisKey, 1);

        // 取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));

        // 增加
        System.out.println(redisTemplate.opsForValue().increment(redisKey));

        // 减少
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        System.out.println(redisTemplate.opsForList().size(redisKey)); // 3
        System.out.println(redisTemplate.opsForList().index(redisKey, 0)); // 103
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2)); // 103 102 101

        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 103
        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 102
        System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 101
    }

    @Test
    public void testSets() {
        String redisKey = "test:teachers";
        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey)); // 随机弹出一个数据
        System.out.println(redisTemplate.opsForSet().members(redisKey)); // 统计集合中的元素都是什么
    }

    @Test
    public void testSortedSets() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 60);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 100);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 40);
        redisTemplate.opsForZSet().add(redisKey, "白龙马", 90);

        System.out.println(redisTemplate.opsForZSet().zCard(redisKey)); // 统计有多少个值
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒")); // 统计八戒多少分
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "八戒")); // 统计八戒的排名,默认是有小到大排序
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒")); // 统计八戒的排名,由大到小排序
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2)); // 统计由小到大(默认)的排名 从 0 - 2
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2)); // 统计由大到小的排名 从 0 - 2


    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user"); // 删除一个key
        System.out.println(redisTemplate.hasKey("test:user"));  // 判断某个key是否存在
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    // 多次访问同一个key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        // BoundHashOperations BoundSetOperations ...
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        for (int i = 0; i < 10; i++) {
            operations.increment();
        }
        System.out.println(operations.get());
    }

    // 编程式事务,redis事务是将命令都放进队列里，然后提交事务的时候 一起执行
    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";
                redisOperations.multi(); // 启用事务

                redisOperations.opsForSet().add(redisKey, "张三");
                redisOperations.opsForSet().add(redisKey, "李四");
                redisOperations.opsForSet().add(redisKey, "王五");

                return redisOperations.exec(); // 提交事务
            }
        });
        System.out.println(obj);
    }
}
