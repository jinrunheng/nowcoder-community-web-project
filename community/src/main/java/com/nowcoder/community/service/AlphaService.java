package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
// @Scope("prototype")
// Scope默认为"singleton",如果将Scope变为"prototype"那么这个Bean就不是单例的
// 在使用 applicationContext.getBean(AlphaService.class)时，每次都会创建一个新的对象
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

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
}
