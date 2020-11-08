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




## 二：注册部分

#### 注册流程

- 访问注册页面
  - 点击顶部区域内的链接，打开注册页面
- 提交注册数据
  - 通过表单提交数据
  - 服务端验证账号是否已经存在，邮箱是否已经注册
  - 服务端发送激活邮件
- 激活注册账号
  - 点击邮件中的链接，访问服务端的激活服务

## 三：会话管理

- HTTP的基本性质

  - HTTP是简单的
  - HTTP是可扩展的
  - HTTP是无状态的，有会话的；HTTP本质是无状态的，使用Cookies可以创建有状态的会话

- Cookie

  - Cookie是服务器发送到浏览器，并保存在浏览器端端一小块数据
  - 浏览器下次访问该服务器端时候，会自动携带该小块数据，将其发送给服务器

- Session

  - Session是JavaEE端标准，用于在服务端记录客户端的信息
  - 数据存放在服务端更加安全，但是也会增加服务端的内存压力

- Session在分布式部署中有什么缺点？或者说为什么在分布式应用中，我们一般都会使用Cookie而不使用Session

  分布式应用中，我们会使用多台服务器同时为客户提供服务，在多台服务器前，我们会使用一个层来进行浏览器请求的分发，这个层一般都是负载均衡的服务器例如nginx；负载均衡的策略一般是看哪一台服务器比较“闲”。

  例如：

  小张的浏览器发送的请求，被nginx分发到了服务器A，因为此时服务器A比较闲，这时候，在服务器A就会生成一个Session，并在响应头中发送给小张的浏览器一个Cookie，Cookie中存储sessionId；那么试想，当小张的浏览器过一段时间又向服务端发送一个请求，这时候服务器A处于busy的状态，所以nginx将小张的请求分发给了服务器B，这时候，服务器B并没有小张的Session信息，问题就暴露出来了。

  那么是否有解决方法呢？

  1. 粘性Session

     设置负载均衡的处理策略，小张发送的请求就一直由于服务器A来处理；不过这也是有一定问题的，这样我们就无法保证nginx的分发策略一定是负载均衡的

  2. 同步Session

     小张浏览器的请求被服务器A处理之后，在服务器A内部创建了Session，同步Session的思路就是，将服务器A内部存储的Session信息同步到其他的服务器上，就相当于含有小张信息的Session被复制了多份给所有的服务器；但是这样暴露出来的问题就更加明显了：首先是造成了不必要的内存上的浪费，其次就是服务器和服务器之间产生了耦合。

  3. 共享Session

     共享Session的思路是：使用一台新的服务器，这台服务器不是用来处理业务的，而是存放所有服务器的共享信息。这样所有浏览器的请求产生的Session都存放在这台共享服务器上，等到浏览器访问服务器的时候，所有的服务器都向这台共享服务器上查看Session信息。这样看似完美，不过也是有一定的隐患，因为所有的Session信息都保存在这台服务器上，如果这台服务器挂掉了，所有的服务器都依赖于这台共享服务器，那么就丢失了所有的Session信息。

     

  所以，现在的主流策略就是，能用Cookie存储就用Cookie存储，而一些用户的敏感信息，就存放到数据库中。数据库可以做一些集群备份，不至于丢失信息。数据库的查询会造成压力，所以可以在数据库之前增加一个层，使用NoSql，例如redis。这种做法就是当前比较主流的做法，也比较成熟。

  

## 四：验证码

kaptcha(https://code.google.com/archive/p/kaptcha)

- 导入jar包

  ```xml
  <dependency>
      <groupId>com.github.penggle</groupId>
      <artifactId>kaptcha</artifactId>
      <version>2.3.2</version>
  </dependency>
  ```

- 编写kaptcha 配置类

  ```java
  package com.nowcoder.community.config;
  
  import com.google.code.kaptcha.Producer;
  import com.google.code.kaptcha.impl.DefaultKaptcha;
  import com.google.code.kaptcha.util.Config;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  import java.util.Properties;
  
  @Configuration
  public class KaptchaConfig {
  
      @Bean
      public Producer kaptchaProducer() {
          Properties properties = new Properties();
          properties.setProperty("kaptcha.image.width", "100");
          properties.setProperty("kaptcha.image.height", "40");
          properties.setProperty("kaptcha.textproducer.font.size", "32");
          properties.setProperty("kaptcha.textproducer.font.color", "black");
          properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
          properties.setProperty("kaptcha.textproducer.char.length", "4");
          properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
  
  
          DefaultKaptcha kaptcha = new DefaultKaptcha();
          Config config = new Config(properties);
          kaptcha.setConfig(config);
          return kaptcha;
      }
  }
  
  ```

- 生成随机字符，生成图片

  ```java
  		@RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
      public void getKaptcha(HttpServletResponse response, HttpSession session) {
          // 生成验证码
          String text = kaptchaProducer.createText();
          BufferedImage image = kaptchaProducer.createImage(text);
  
          // 将验证码写入session中
          session.setAttribute("kaptcha", text);
          // 将图片输出给浏览器
          response.setContentType("image/png");
          try {
              OutputStream outputStream = response.getOutputStream();
              ImageIO.write(image, "png", outputStream);
          } catch (IOException e) {
              logger.error("响应验证码失败:" + e.getMessage());
          }
      }
  ```

  

  在我们的login.html文件中

  ```html
  <img th:src="@{/kaptcha}" id="kaptcha" style="width:100px;height:40px;" class="mr-2"/>
  <a href="javascript:refresh_kaptha();" class="font-size-12 align-bottom">刷新验证码</a>
  ```

  JS刷新验证码的功能如下：

  ```html
  <script>
      function refresh_kaptha() {
          var path = CONTEXT_PATH + "/kaptcha?p=" + Math.random()
          $("#kaptcha").attr("src", path)
      }
  </script>
  ```

  

## 五：开发登录与退出模块

- 访问登录页面
  - 点击顶部区域内的链接，打开登录页面
- 登录
  - 验证账号，密码，验证码
  - 成功时，生成登录凭证，发送给客户端
  - 失败时，跳转回登录页
- 退出
  - 将登录凭证修改为失效的状态
  - 跳转至网站首页

## 六：忘记密码

- 点击登录页面的“忘记密码”链接，打开忘记密码页面
- 在表单中输入注册的邮箱，点击获取验证码按钮，服务器为该邮箱发送一份验证码
- 在表单中填写收到的验证码以及新的密码，点击重置密码，服务器对密码进行修改



## 七：显示登录信息

- 拦截器示例

  - 定义拦截器，实现HandlerInterceptor

    ```java
    package com.nowcoder.community.controller.interceptor;
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.stereotype.Component;
    import org.springframework.web.servlet.HandlerInterceptor;
    import org.springframework.web.servlet.ModelAndView;
    
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    
    
    @Component
    public class AlphaInterceptor implements HandlerInterceptor {
    
    
        private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);
    
        // 在Controller之前执行
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    
            logger.debug("preHandle: " + handler.toString());
            return true;
        }
    
        // 在调用Controller之后执行
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            logger.debug("postHandle :" + handler.toString());
        }
    
        // 在TemplateEngine之后执行
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            logger.debug("afterCompletion: " + handler.toString());
        }
    }
    ```

  - 配置拦截器，为它置顶拦截，排除的路径

    ```java
    package com.nowcoder.community.config;
    
    import com.nowcoder.community.controller.interceptor.AlphaInterceptor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
    
    @Configuration
    public class WebMvcConfig implements WebMvcConfigurer {
    
        @Autowired
        private AlphaInterceptor alphaInterceptor;
    
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            // excludePathPatterns 代表不需要拦截的资源 /** 代表所有的目录
            // addPathPatterns 代表需要拦截的资源
            registry.addInterceptor(alphaInterceptor)
                    .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg")
                    .addPathPatterns("/register", "/login");
        }
    }
    ```

- 拦截器应用

  - 在请求开始时查询登录用户
  - 在本次请求中持有用户数据
  - 在模版视图上显示用户数据
  - 在请求结束时清理用户数据

在拦截器中选择存储用户的内存应该考虑到多线程的问题，使用ThreadLocal进行存储。

ThreadLocal采用线程隔离的方式存放数据，可以避免多线程之间出现数据访问的冲突；ThreadLocal提供set方法，能够以当前线程为key存放数据;ThreadLocal提供get方法，能够以当前线程为key获取数据；ThreadLocal提供remove方法，能够以当前线程为key删除数据。

## 八：账号设置

- 上传文件
  - 请求：必须是POST请求
  - 表单：`enctype="multipart/form-data"`
  - Spring MVC：通过MultipartFile 处理上传文件
- 开发步骤
  - 访问账号设置页面
  - 上传头像
  - 获取头像

## 九：修改密码

- 在账号设置页面，填写原密码以及新密码，点击保存时将数据提交给服务器
- 服务器检查原密码是否正确，若正确则将密码修改为新密码，并重定向到退出功能，强制用户重新登录。
- 若错误则返回到账号设置页面，给以相应提示信息。

## 十：检查登录状态

- 使用拦截器
  - 在方法前标注自定义注解
  - 拦截所有请求，只处理带有该注解的方法

- 自定义注解

  - 常用的元注解

    - @Target(要自定义注解务必使用的元注解)
      - **`@Target(ElementType.TYPE)`——接口、类、枚举、注解**
      - **`@Target(ElementType.FIELD)`——字段、枚举的常量**
      - **`@Target(ElementType.METHOD)`——方法**
      - **`@Target(ElementType.PARAMETER)`——方法参数**
      - **`@Target(ElementType.CONSTRUCTOR)` ——构造函数**
      - **`@Target(ElementType.LOCAL_VARIABLE)`——局部变量**
      - **`@Target(ElementType.ANNOTATION_TYPE)`——注解**
      - **`@Target(ElementType.PACKAGE)`——包**
    - @Retention(要自定义注解务必使用的元注解)
      - **`RetentionPolicy.SOURCE`:这种类型的`Annotations`只在源代码级别保留,编译时就会被忽略,在`class`字节码文件中不包含。**
      - **`RetentionPolicy.CLASS`:这种类型的`Annotations`编译时被保留,默认的保留策略,在`class`文件中存在,但`JVM`将会忽略,运行时无法获得**
      - **`RetentionPolicy.RUNTIME`:这种类型的`Annotations`将被`JVM`保留,所以他们能在运行时被`JVM`或其他使用反射机制的代码所读取和使用**
      - **`@Document`：说明该注解将被包含在`javadoc`中**
      - **`@Inherited`：说明子类可以继承父类中的该注解**

    - @Document
    - @Inherited

  - 如何读取注解:通过反射

    - `Method.getDeclaredAnnotations()`
    - `Method.getAnnotation(Class<T> annotationClass)`

  































































