package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 点赞
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        // redis处理事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyGenerator.getUserLikeKey(entityUserId);

                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });


    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态  如果没有 则显示 赞 ; 如果点过赞 则显示 已赞
    // 因为除了赞和未赞 还有 踩这个功能，所以返回int 1 为点过赞， 0 为未点过赞，-1为踩
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyGenerator.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 查询某个用户获得的赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyGenerator.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
