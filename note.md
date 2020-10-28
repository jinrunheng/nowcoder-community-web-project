

## 一：搭建开发环境

#### Apache Maven

- 功能：构建项目，管理项目中的jar包
- Maven仓库：
  - 本地仓库：默认是～/.m2/repository
  - 远程仓库：中央仓库，镜像仓库，私服仓库

- Maven常用命令

  - compile

    ```
    mvn clean compile
    ```

  - test

    ```
    mvn clean test
    ```

  - clean package

    ```
    mvn clean package -Dmaven.test.skip=true
    ```

#### IntelliJ IDEA

#### Spring initializr

- 构建Spring Boot 项目骨架的引导工具
- https://start.spring.io

#### Spring Boot 入门示例程序

- 一个简单的处理客户端请求案例：

  ```java
  package com.nowcoder.community.controller;
  
  import org.springframework.stereotype.Controller;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.ResponseBody;
  
  @Controller
  @RequestMapping("/alpha")
  public class AlphaController {
  
      @RequestMapping("/hello")
      @ResponseBody
      public String sayHello() {
          return "Hello Spring Boot";
      }
  }
  ```

  

## 二：Spring 入门

#### Spring 全家桶

- Spring Framework
- Spring Boot
- Spring Cloud
- Spring Cloud Data Flow

#### Spring Framework

- Spring Core
  - IoC,AOP
- Spring Data Access
  - Transactions,Spring MyBatis
- Web Servlet
  - Spring MVC
- Integration
  - Email,Scheduling,AMQP,Security

#### Spring IoC

- Inversion of Control
  - 控制反转，是一种面向对象的设计思想
- Dependency Injection
  - 依赖注入，是IoC思想的实现方式
- IoC Container
  - IoC容器，是实现依赖注入的关键，本质上是一个工厂

 ## 三：Spring MVC入门

#### HTTP

HTTP：Hyper Text Transfer Protocol

#### HTTP流

客户端与服务端进行交互时，过程表现为以下几个步骤

1. 打开一个TCP连接

2. 发送一个HTTP报文(HTTP Request)

   HTTP请求信息由三部分组成：

   1. method  Path  version of the protocol
   2. 请求头（Request Header）
   3. 请求正文

   ```http
   GET / HTTP/1.1
   Accept:image/gif.image/jpeg,*/*
   Accept-Language:zh-cn
   Connection:Keep-Alive
   Host:localhost
   User-Agent:Mozila/4.0(compatible;MSIE5.01;Window NT5.0)
   Accept-Encoding:gzip,deflate
   
   username=root&password=1234
   ```

3. 接收服务端返回的报文信息（HTTP Response）

   HTTP响应信息由三部分组成

   1. Version of protocol / status code /status message
   2. 响应头（Response Header）
   3. 响应正文

   ```http
   HTTP/1.1 200 OK
   Server:Apache Tomcat/5.0.12
   Date:Mon,6Oct2003 13:23:42 GMT
   Content-Length:112
    
      <html>
        <head>
        <title>HTTP响应示例<title>
    </head>
    <body>
         Hello HTTP!
    </body>
   </html>
   ```

4. 关闭连接或者为后续请求重用连接

#### Spring MVC

- 三层架构
  - 表现层，业务层，数据访问层

- MVC（表现层）
  - Model
  - View
  - Controller
- 核心组件
  - 前端控制器：DispatcherServlet
- Spring MVC 的执行流程
  1. 用户浏览器发送请求至前端控制器DispatcherServlet
  2. DispatcherServlet收到请求后调用HandlerMapping处理器映射器
  3. HandlerMapping根据请求url找到具体的处理器，生成处理器执行链(HandlerExecutionChain)包括处理器对象以及处理器拦截器(有则生成),一并返回给DipatcherServlet
  4. DispatcherServlet通过HandlerAdapter(处理器适配器)调用处理器，执行处理器
  5. 处理器执行完成后返回ModelAndView，并返回给HandlerAdapter，HandlerAdapter将结果返回给DispatcherServlet
  6. DispatcherServlet将ModelAndView传给ViewResolver视图解析器，ViewResolver解析后返回具体的View给DispatcherServlet
  7. DispatcherServlet对View进行渲染(即将模型数据填充至视图中)后返回给客户端口

#### Thymeleaf

- 模版引擎
  - 生成动态的HTML
- Thymeleaf
  - 倡导自然模版，即以HTML文件为模版
- 常用语法
  - 标准表达式，判断与循环，模版的布局

## MyBatis入门

#### MyBatis

- 核心组件
  - SqlSessionFactory:用于创建SqlSession的工厂类
  - SqlSession:MyBatis的核心组件，用于向数据库执行SQL
  - 主配置文件:XML配置文件，可以对MyBatis的底层行为做出详细的配置
  - Mapper接口:就是DAO接口，在MyBatis中习惯性的称之为Mapper
  - Mapper映射器:用于编写SQL，并将SQL和实体类映射的组件，采用XML，注解的方式均可以实现
- 示例
  - 使用MyBatis对用户表进行CRUD操作



