package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@Slf4j
class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public void testApplicationContext() {
        log.info("applicationContext:{}", applicationContext);
        // test bean
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        log.info("alphaDao select result:{}", alphaDao.select());

        alphaDao = applicationContext.getBean("alphaDaoHibernateImpl", AlphaDao.class);
        log.info("alphaHibernate select result:{}", alphaDao.select());
    }

    @Test
    public void testBeanManagement() {
        // 在Spring容器中管理的Bean默认是单例的
        // 由于下面的程序可以看出，alphaService的只创建了一次，只初始化了一次，并且只销毁了一次
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        alphaService = applicationContext.getBean(AlphaService.class);
    }

    @Test
    public void testBeanConfig() {
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        log.info(simpleDateFormat.format(new Date()));
    }

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Test
    public void testDI() {
        log.info(alphaDao.select());
        log.info(alphaService.toString());
        log.info(simpleDateFormat.format(new Date()));
    }
}
