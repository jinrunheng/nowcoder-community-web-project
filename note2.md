# Spring Boot开发社区登录模块

## 一：发送邮件

- 邮箱设置	

  - 启用客户端的SMTP服务

- Spring Email

  - 导入jar包

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
        <version>2.2.6.RELEASE</version>
    </dependency>
    ```

  - 邮箱参数配置

    ```properties
    # MailProperties
    # 我的配置参数如下
    spring.mail.host=smtp.qq.com
    spring.mail.port=465
    spring.mail.username=2246232728@qq.com
    spring.mail.password=gosfqgusnnszdjdf
    spring.mail.protocol=smtps
    spring.mail.default-encoding=utf-8
    spring.mail.properties.mail.smtp.ssl.enable=true
    ```

  - 使用JavaMailSender发送邮件

    ```java
    package com.nowcoder.community.util;
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.mail.javamail.JavaMailSender;
    import org.springframework.mail.javamail.MimeMessageHelper;
    import org.springframework.stereotype.Component;
    
    import javax.mail.MessagingException;
    import javax.mail.internet.MimeMessage;
    
    @Component
    public class MailClient {
        private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    
        @Autowired
        private JavaMailSender javaMailSender;
    
        @Value("${spring.mail.username}")
        private String from;
    
        public void sendMail(String to, String subject, String content) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            try {
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content,true);
                javaMailSender.send(helper.getMimeMessage());
            } catch (MessagingException e) {
                logger.error("发送邮件失败:" + e.getMessage());
            }
        }
    }
    ```

    测试程序：

    ```java
    package com.nowcoder.community;
    
    import com.nowcoder.community.util.MailClient;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.context.ContextConfiguration;
    
    @SpringBootTest
    @ContextConfiguration(classes = CommunityApplication.class)
    public class MailTests {
        @Autowired
        private MailClient mailClient;
    
        @Test
        public void testTextMail() {
            mailClient.sendMail("1175088275@qq.com", "TEST", "this is a test email");
        }
    
    }
    ```

    

- 模版引擎

  - 使用Thymeleaf发送HTML邮件

    ```html
    <!DOCTYPE html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>邮件示例</title>
    </head>
    <body>
        <p>你好,<span style="color: hotpink" th:text="${username}"></span>!</p>
    </body>
    </html>
    ```

    对应的测试程序如下：

    ```java
    		package com.nowcoder.community;
    
    import com.nowcoder.community.util.MailClient;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.test.context.ContextConfiguration;
    import org.thymeleaf.TemplateEngine;
    import org.thymeleaf.context.Context;
    
    @SpringBootTest
    @ContextConfiguration(classes = CommunityApplication.class)
    public class MailTests {
        @Autowired
        private MailClient mailClient;
    
        @Autowired
        private TemplateEngine templateEngine;
    
        @Test
        public void testTextMail() {
            mailClient.sendMail("1175088275@qq.com", "TEST", "this is a test email");
        }
    
        @Test
        public void testHtmlMail() {
            Context context = new Context();
            context.setVariable("username", "kim");
    
            String content = templateEngine.process("/mail/demo", context);
            System.out.println(content);
    
            mailClient.sendMail("1175088275@qq.com", "HTML", content);
        }
    }
    ```

    