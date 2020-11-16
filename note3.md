## Spring Boot 开发社区核心功能

#### 一：过滤敏感词

- 前缀树
  - 名称：Trie，字典树，查找树
  - 特点：查找效率高，消耗内存大
  - 应用：字符串检索，词频统计，字符串排序等
- 敏感词过滤器
  - 定义前缀树
  - 根据敏感词，初始化前缀树
  - 编写过滤敏感词的方法



假设敏感词为：

```
[abc,bf,be]
```

用户输入为：

```
xwabfabcff
```

根据敏感词构造的前缀树：

```
       root
     /      \
    a         b 
   /        /    \
  b      f(end)   e(end)
 /   
c(end)    
```

从root开始到一个分支的end节点，构成的即是一个敏感词

将敏感词替换为：`***`,我们将用户输入替换的结果为：

```
xwa******ff
```



#### 二：发布帖子

- AJAX

  - Asynchronous JavaScript and XML
  - 异步的JavaScript与XML，不是一门新的技术，只是一个新的术语
  - 使用AJAX，网页能够将增量更新呈现在页面上，而不需要刷新整个页面
  - 虽然X代表XML，但是目前JSON的使用比XML更加普遍
  - 文档：https://developer.mozilla.org/zh-CN/docs/Web/Guide/AJAX

- 示例

  - 使用jQuery发送AJAX请求

    Controller:

    ```java
    		@RequestMapping(path = "ajax",method = RequestMethod.POST)
        @ResponseBody
        public String testAjax(String name,int age){
            // 0 代表成功 {"code":"0","msg":"操作成功"}
            return CommunityUtil.getJSONString(0,"操作成功");
        }
    ```

    Ajax:

    ```html
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>AJAX</title>
    </head>
    <body>
    <p>
        <input type="button" value="发送" onclick="send();">
    </p>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js" crossorigin="anonymous"></script>
    <script>
        // 使用jQuery 发送一个异步请求
      	// 按照如下格式发送一个Ajax异步请求
      	// 1. 访问路径 
      	// 2. 传入的数据 如示例中，传入的数据为{"name":"张三","age":"23"}，提交方式为post
        // 3. 回调函数 controller返回的为JSON格式的字符串，回调函数将JSON字符串变成一个对象异步返回给页面
        function send() {
            $.post(
                "/community/alpha/ajax",
                {"name":"张三","age":"23"},
                function (data) {
                    console.log(typeof(data)) //  string
                    console.log(data) // {"code":0,"msg":"操作成功"}
    
                    data = $.parseJSON(data) 
                    console.log(typeof(data)) // object
                    console.log(data.code)
                    console.log(data.msg)
                }
            );
        }
    </script>
    </body>
    </html>
    ```

- 实践

  - 采用AJAX请求，实现发布帖子的功能

    

#### 三：帖子详情

- DiscussPostMapper
- DiscussPostService
- DiscussPostController
- Index.html
  - 在帖子标题上增加访问详情页面的链接
- discuss-detail.html
  - 处理静态之源的访问路径
  - 复用index.html的header区域
  - 显示标题，作者，发布时间，帖子正文等内容

#### 四：事务管理

- 什么是事务
  - 事务是由N步数据库操作序列组成的逻辑执行单元，这系列操作要么全执行，要么全放弃执行
- 事务的特性(ACID)
  - 原子性(Atomicity):事务是应用中不可再分的最小执行体。
  - 一致性(Consistency):事务执行的结果，须使数据从一个一致性状态，变为另一个一致性状态。
  - **隔离型(Isolation)**：各个事务的执行互不干扰，任何事务的内部操作对其他的事物都是隔离的。(多线程，并发的时候互不干扰)
  - 持久性(Durability)：事务一旦提交，对数据所做的任何改变都要记录到永久存储器中。

##### 事务的隔离性

- 常见的并发异常
  - 第一类丢失更新，第二类丢失更新。
  - 脏读，不可重复读，幻读。
- 常见的隔离级别
  - Read Uncommitted: 读取未提交的数据 
  - Read Committed：读取已提交的数据
  - Repeatable Read：可重复读
  - Serializable：串行化



事务的隔离型常见的并发异常解释（**面试常考**）：

###### 1. 第一类丢失更新

某一个事务的回滚，导致另外一个事务已更新的数据丢失

| 时刻 | 事务1           | 事务2        |
| ---- | --------------- | ------------ |
| T1   | Read:N = 10     |              |
| T2   |                 | Read:N = 10  |
| T3   |                 | Write:N = 9  |
| T4   |                 | Commit:N = 9 |
| T5   | Write:N = 11    |              |
| T6   | Rollback:N = 10 |              |

###### 2. 第二类丢失更新

某一个事务的提交，导致另外一个事务已更新的数据丢失

| 时刻 | 事务1         | 事务2        |
| ---- | ------------- | ------------ |
| T1   | Read:N = 10   |              |
| T2   |               | Read:N = 10  |
| T3   |               | Write:N = 9  |
| T4   |               | Commit:N = 9 |
| T5   | Write:N = 11  |              |
| T6   | Commit:N = 11 |              |

###### 3. 脏读

某一个事务，读取了另外一个事务未提交的数据

| 时刻 | 事务1           | 事务2       |
| ---- | --------------- | ----------- |
| T1   | Read:N = 10     |             |
| T2   | Write:N = 11    |             |
| T3   |                 | Read:N = 11 |
| T4   | Rollback:N = 10 |             |

###### 4. 不可重复读

某一个事务，对同一个数据前后读取的结果不一致

| 时刻 | 事务1         | 事务2       |
| ---- | ------------- | ----------- |
| T1   | Read:N = 10   |             |
| T2   |               | Read:N = 10 |
| T3   | Write:N = 11  |             |
| T4   | Commit:N = 11 |             |
| T5   |               | Read:N = 11 |

###### 5. 幻读

某一个事务，对同一个表前后查询到的行数不一致

| 时刻 | 事务1              | 事务2                    |
| ---- | ------------------ | ------------------------ |
| T1   |                    | Select:id < 10 (1,2,3)   |
| T2   | Insert:id = 4      |                          |
| T3   | Commit:id(1,2,3,4) |                          |
| T4   |                    | Select:id < 10 (1,2,3,4) |



##### 事务的隔离级别

| 隔离级别         | 第一类丢失更新 | 脏读 | 第二类丢失更新 | 不可重复读 | 幻读 |
| ---------------- | -------------- | ---- | -------------- | ---------- | ---- |
| Read Uncommitted | Y              | Y    | Y              | Y          | Y    |
| Read Committed   | N              | N    | Y              | Y          | Y    |
| Repeatable Read  | N              | N    | N              | N          | Y    |
| Serializable     | N              | N    | N              | N          | N    |

##### 数据库保障事务的机制

实现机制

###### 1. 悲观锁(数据库)

- 共享锁(S锁)
  - 事务A对某数据加了共享锁后，其他事务职能对该数据加共享锁，但不能加排他锁头
- 排它锁(X锁)
  - 事务A对某数据加了排他锁后，其他事务对该数据既不能加共享锁，也不能加排他锁。

###### 2. 乐观锁(自定义)

- 版本号，时间戳等
  - 在更新数据前，检查版本号是否发生变化。若发生变化则取消本次更新，否则就更新数据(版本号 + 1)



##### Spring事务管理

###### 1. 声明式事务

- 通过XML配置，声明某方法对事务特征。
- 通过注解(@Transactional)，声明某方法对事务特征。

###### 2. 编程式事务

- 通过TransactionTemplate 管理事务
- 并且通过TransactionTemplate执行数据库操作

##### 事务传播特性

###### 1. REQUIED

默认的传播特性，业务方法需要在一个事务中运行，如果一个方法已经处在一个事务中，那么就加入到这个事务，否则就会创建一个事务

###### 2. NEVER

指定的业务方法绝对不能在事务范围内运行，如果业务方法在某个事务中执行，就会抛出异常，只有业务方法没有任何事务才执行。

###### 3. MANDATORY

该属性指定业务方法只能在一个已经存在的事务中执行，业务方法不能自己发起自己的事务，如果业务方法不存在事务，容器就会抛出异常。

###### 4. SUPPORTS

如果业务方法中已经在某个事务中被调用，则方法就称为事务的一部分，如果外部业务方法没有开启事务，supports该方法也会在没有事务的环境中执行。

###### 5. NOT_SUPPORTED

如果该业务方法在一个事务中被调用，那么当前的事务会被挂起，执行该业务方法，方法执行完毕唤醒被挂起的事务，如果业务方法不在一个事务中执行，该方法也不会开事务。不管是否在有无事务的环境中执行都不开启事务。

###### 6. REQUIRES_NEW

不管是否存在事务，业务方法总会自己开启一个事务，如果在已有事务的环境中调用，已有事务会被挂起，新的事务会被创建，直到业务方法调用结束，已有事务才被唤醒。

###### 7. NESTED

如果业务方法在一个事务中执行，就在这个事务中嵌套，如果没有事务按着required执行，开启单独的事务，这种事务有多个事务的保存点，内部事务的回滚对外部事务没有影响。



##### 示例

###### 1. 声明式事务示例

```java

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

        // 人为制造一个错误 会返回 NumberFormatException 
        Integer.valueOf("abc");
        return "ok";
    }
```

测试程序：

```java
		@Test
    public void testSave1() {
        Assertions.assertThrows(NumberFormatException.class,()->{
            alphaService.save1();
        });

        // 数据会回滚，所以用户和帖子都不会插入到对应的数据库中
        Assertions.assertNull(userService.findUserByEmail("alpha@qq.com"));
        Assertions.assertNull(discussPostService.findDiscussPostById(999));
    }
```



###### 2. 编程式事务示例

```java
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
```

测试程序

```java
		@Test
    public void testSave2() {
        Assertions.assertThrows(NumberFormatException.class,()->{
            alphaService.save2();
        });

        // 数据会回滚，所以用户和帖子都不会插入到对应的数据库中
        Assertions.assertNull(userService.findUserByEmail("beta@qq.com"));
        Assertions.assertNull(discussPostService.findDiscussPostById(666));
    }
```



#### 五：显示评论

- 数据层
  - 根据实体查询一页评论数据
  - 根据实体查询评论
- 业务层
  - 处理查询评论的业务
  - 处理查询评论数量的业务
- 表现层
  - 显示帖子详情数据时，同时显示该帖子所有的评论数据

#### 六：添加评论

- 数据层
  - 添加评论数据
  - 修改帖子的评论数量
- 业务层
  - 处理添加评论的业务
  - 先增加评论，再更新帖子的评论数量
- 表现层
  - 处理添加评论数据的请求
  - 设置添加评论的表单







