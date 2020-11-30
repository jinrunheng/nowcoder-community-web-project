package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 关注 ： 用户关注了某个实体
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());


                redisOperations.exec();
                return null;

            }
        });
    }

    // 取消关注
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);

                redisOperations.multi();

                redisOperations.opsForZSet().remove(followeeKey, entityId);
                redisOperations.opsForZSet().remove(followerKey, userId);


                redisOperations.exec();
                return null;

            }
        });
    }

    // 查询关注的实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 查询实体的关注者的数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyGenerator.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyGenerator.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}
