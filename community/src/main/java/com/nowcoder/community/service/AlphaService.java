package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
@Slf4j
// @Scope("prototype")
// Scope默认为"singleton",如果将Scope变为"prototype"那么这个Bean就不是单例的
// 在使用 applicationContext.getBean(AlphaService.class)时，每次都会创建一个新的对象
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public String find() {
        return alphaDao.select();
    }

    public AlphaService() {
        log.info("instantiation AlphaService");
    }

    @PostConstruct // 会在构造器之后调用这个方法
    public void init() {
        log.info("init AlphaService");
    }

    @PreDestroy // 在销毁对象前调用这个方法
    public void destroy() {
        log.info("destroy AlphaService");
    }

    // Propagation 事务传播特性
    // REQUIRED : 支持外部事务，如果不存在则创建新的事务（A调用B,B则采用A的事务，如果没有则创建一个事务）
    // REQUIRES_NEW : 创建一个新事务，并且暂停外部事务 （A调用B，B不管A有没有事务，都自己创建一个新的事务）
    // NESTED : 如果当前存在事务(外部事务)，则嵌套在该事务中执行 （A调用B，B嵌套在A的事务下，但是有自己独立的提交和回滚)，否则就喝REQUIRED一样
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image,nowcoder.com/head/99.png");
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = DiscussPost.builder()
                .id(999)
                .userId(user.getId())
                .title("Hello")
                .content("新人报道~")
                .createTime(new Date())
                .build();
        discussPostMapper.insertDiscussPost(post);

        // 人为制造一个错误
        Integer.valueOf("abc");
        return "ok";
    }

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute((TransactionCallback<Object>) transactionStatus -> {
            // 新增用户
            User user = new User();
            user.setUsername("beta");
            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
            user.setEmail("beta@qq.com");
            user.setHeaderUrl("http://image,nowcoder.com/head/66.png");
            user.setCreateTime(new Date());

            userMapper.insertUser(user);

            // 新增帖子
            DiscussPost post = DiscussPost.builder()
                    .id(666)
                    .userId(user.getId())
                    .title("Hello")
                    .content("新人报道~")
                    .createTime(new Date())
                    .build();
            discussPostMapper.insertDiscussPost(post);

            // 人为制造一个错误
            Integer.valueOf("abc");
            return "ok";
        });
    }
}
