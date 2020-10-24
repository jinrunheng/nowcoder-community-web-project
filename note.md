

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

 