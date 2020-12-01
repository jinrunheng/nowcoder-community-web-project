## Redis,一站式高性能存储方案

#### 1. Redis入门

- Redis是一款基于键值对的NoSQL数据库，它的值支持多种数据结构：
  - 字符串：strings
  - 哈希：hashes
  - 列表：lists
  - 集合：sets
  - 有序集合：sorted sets
- Redis将所有的数据都存放在内存中，所以它的读写性能十分惊人
- Redis还可以将内存中的数据以快照或者日志的形式保存到硬盘上，以保证数据的安全性
- Redis典型的应用场景包括：缓存，排行榜，计数器，社交网络，消息队列等

Redis官网：https://redis.io

##### Redis的下载安装与启动

- 在官网下载Redis的压缩包，并解压至到目录下
- 移动到：`sudo mv redis-6.0.9 /usr/local`
- 切换到：`cd /usr/local/redis-6.0.9/`
- 编译测试：`make test`
- 编译安装：`make install`

要使用redis，首先要开启redis服务端，在iTerm终端下输入`redis-server`

在成功看到redis服务端默认在6379端口成功开启后，不要关闭此窗口，并重新打开一个终端，输入`redis-cli`命令，打开redis客户端

然后我们就可以在redis客户端交互式地使用redis的一些命令了

##### redis的一些简单命令

选择数据库：

```
select 0~15
```

清空数据库：

```
flushdb
```

**strings：**

键值对的存取：在redis中的命名，如果一个key有两个单词，那么通常使用冒号将两个单词连接

```
set test:count 1

get test:count
```

让key对应的值加一减一

```
incr test:count

decr test:count
```

**hash:**

键值对的存取：

```
hset test:user id 1
hset test:user username zhangsan

hget test:user id
hget test:user username
```

**list:**

redis中的列表比较灵活，既支持左进右出（队列），也支持左进左出（栈）

```
lpush test:ids 101 102 103 //  103 102 101 先压入101 再压入102 再压入 103
llen test:ids // 看列表的长度
lindex test:ids 0 // 看列表中索引为0的那个值 103
lrange test:ids 0 2 //看列表中索引范围为 0 ～ 2位置的值

rpop test:ids // 从右侧弹出一个值 这时候弹出的值应该为 最右边的值为 101
```

**set：**

```
sadd test:teachers aaa bbb ccc ddd eee
scard test:teachers // 统计集合中有多少个元素
spop test:teachers // 从集合中随机弹出一个元素
smembers test:teachers // 查看集合中还剩什么元素
```

**zset:**

```
zadd test:students 10 aaa 20 bbb 30 ccc 40 ddd 50 eee
zcard test:students // 统计有序集合中有多少个数据
zscore test:students ccc // 查询某个key的值 这里面为查询ccc的值
zrank test:students ccc // 返回某个值的排名 排名从0开始
zrange test:students 0 2 // 返回某个排名区间内的数据 这里面为返回 排名 0 ～ 2的值
```

**全局命令:**

```
keys * // 查看全局所有的key
keys test* // 查看全局的所有以test开头的key
type test:user // 看某个key的类型 这里对以返回的是hash
exists test:user // 查看某个key是否存在  1 表示存在 0 表示不存在 这里面返回 1
del test:user // 删除某个key
exists test:user // 删除之后再次查询 返回结果为 0
expire test:students 10 // 指定某个key存在的时间，这里面为指定 test:students这个key的生存时间为 10秒
```

#### 2. Spring整合Redis

- 引入依赖

  - `spring-boot-starter-data-redis`

- 配置Redis

  - 配置数据库参数
  - 编写配置类，构造RedisTemplate

- 访问Redis

  - `redisTemplate.opsForValue()`
  - `redisTemplate.opsForHash()`
  - `redisTemplaet.opsForList()`
  - `redisTemplate.opsForSet()`
  - `redisTemplate.opsForZSet()`

- 示例程序

  Redis配置：

  ```java
  package com.nowcoder.community.config;
  
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.data.redis.connection.RedisConnectionFactory;
  import org.springframework.data.redis.core.RedisTemplate;
  import org.springframework.data.redis.serializer.RedisSerializer;
  
  @Configuration
  public class RedisConfig {
  
      @Bean
      public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
          RedisTemplate<String, Object> template = new RedisTemplate<>();
          template.setConnectionFactory(factory);
  
          // 设置key的序列化方式
          template.setKeySerializer(RedisSerializer.string());
          // 设置value的序列化方式
          template.setValueSerializer(RedisSerializer.json());
          // 设置hash的key的序列化方式
          template.setHashKeySerializer(RedisSerializer.string());
          // 设置hash的value的序列化方式
          template.setHashValueSerializer(RedisSerializer.json());
  
          template.afterPropertiesSet();
          return template;
      }
  }
  ```

  Spring访问Redis

  ```java
  package com.nowcoder.community;
  
  import lombok.extern.slf4j.Slf4j;
  import org.junit.jupiter.api.Test;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.boot.test.context.SpringBootTest;
  import org.springframework.dao.DataAccessException;
  import org.springframework.data.redis.core.BoundValueOperations;
  import org.springframework.data.redis.core.RedisOperations;
  import org.springframework.data.redis.core.RedisTemplate;
  import org.springframework.data.redis.core.SessionCallback;
  import org.springframework.test.context.ContextConfiguration;
  
  import java.util.concurrent.TimeUnit;
  
  @SpringBootTest
  @ContextConfiguration(classes = CommunityApplication.class)
  @Slf4j
  public class RedisTest {
  
      @Autowired
      private RedisTemplate redisTemplate;
  
      @Test
      public void testStrings() {
          String redisKey = "test:count";
          // 存数据
          redisTemplate.opsForValue().set(redisKey, 1);
  
          // 取数据
          System.out.println(redisTemplate.opsForValue().get(redisKey));
  
          // 增加
          System.out.println(redisTemplate.opsForValue().increment(redisKey));
  
          // 减少
          System.out.println(redisTemplate.opsForValue().decrement(redisKey));
      }
  
      @Test
      public void testHash() {
          String redisKey = "test:user";
          redisTemplate.opsForHash().put(redisKey, "id", 1);
          redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
  
          System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
          System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
      }
  
      @Test
      public void testLists() {
          String redisKey = "test:ids";
          redisTemplate.opsForList().leftPush(redisKey, 101);
          redisTemplate.opsForList().leftPush(redisKey, 102);
          redisTemplate.opsForList().leftPush(redisKey, 103);
  
          System.out.println(redisTemplate.opsForList().size(redisKey)); // 3
          System.out.println(redisTemplate.opsForList().index(redisKey, 0)); // 103
          System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2)); // 103 102 101
  
          System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 103
          System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 102
          System.out.println(redisTemplate.opsForList().leftPop(redisKey)); // 101
      }
  
      @Test
      public void testSets() {
          String redisKey = "test:teachers";
          redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "赵云", "诸葛亮");
          System.out.println(redisTemplate.opsForSet().size(redisKey));
          System.out.println(redisTemplate.opsForSet().pop(redisKey)); // 随机弹出一个数据
          System.out.println(redisTemplate.opsForSet().members(redisKey)); // 统计集合中的元素都是什么
      }
  
      @Test
      public void testSortedSets() {
          String redisKey = "test:students";
  
          redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
          redisTemplate.opsForZSet().add(redisKey, "悟空", 60);
          redisTemplate.opsForZSet().add(redisKey, "八戒", 100);
          redisTemplate.opsForZSet().add(redisKey, "沙僧", 40);
          redisTemplate.opsForZSet().add(redisKey, "白龙马", 90);
  
          System.out.println(redisTemplate.opsForZSet().zCard(redisKey)); // 统计有多少个值
          System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒")); // 统计八戒多少分
          System.out.println(redisTemplate.opsForZSet().rank(redisKey, "八戒")); // 统计八戒的排名,默认是有小到大排序
          System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "八戒")); // 统计八戒的排名,由大到小排序
          System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2)); // 统计由小到大(默认)的排名 从 0 - 2
          System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2)); // 统计由大到小的排名 从 0 - 2
  
  
      }
  
      @Test
      public void testKeys() {
          redisTemplate.delete("test:user"); // 删除一个key
          System.out.println(redisTemplate.hasKey("test:user"));  // 判断某个key是否存在
          redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
      }
  
      // 多次访问同一个key
      @Test
      public void testBoundOperations() {
          String redisKey = "test:count";
          // BoundHashOperations BoundSetOperations ...
          BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
          for (int i = 0; i < 10; i++) {
              operations.increment();
          }
          System.out.println(operations.get());
      }
  
      // 编程式事务,redis事务是将命令都放进队列里，然后提交事务的时候 一起执行
      @Test
      public void testTransactional() {
          Object obj = redisTemplate.execute(new SessionCallback() {
              @Override
              public Object execute(RedisOperations redisOperations) throws DataAccessException {
                  String redisKey = "test:tx";
                  redisOperations.multi(); // 启用事务
  
                  redisOperations.opsForSet().add(redisKey, "张三");
                  redisOperations.opsForSet().add(redisKey, "李四");
                  redisOperations.opsForSet().add(redisKey, "王五");
  
                  return redisOperations.exec(); // 提交事务
              }
          });
          System.out.println(obj);
      }
  }
  ```

  

#### 3. 点赞

点赞是一个会经常使用的功能，将数据存储到Redis中 以提升性能

点赞

- 支持对帖子，评论进行点赞
- 第一次点赞，第二次则取消点赞

首页点赞数量

- 统计帖子的点赞数量

详情页点赞数量

- 统计点赞数量
- 显示点赞状态



#### 4. 我收到的赞

- 重构点赞功能
  - 以用户为key，记录点赞数量
  - increment(key),decrement(key)
- 开发个人主页
  - 以用户为key，查询点赞数量

#### 5. 关注，取消关注

- 需求
  - 开发关注，取关功能
  - 统计用户的关注数，粉丝数
- 关键
  - 如果A关注了B，那么A则是B的Follower,B则是A的Folowee
  - 关注的目标可以是用户，帖子，题目等等，在实现的时候将这些目标抽象为实体

#### 6. 关注列表，粉丝列表

- 业务层
  - 查询某个用户关注的人，支持分页操作
  - 查询某个用户的粉丝，支持分页操作
- 表现层
  - 处理“查询关注的人”，“查询粉丝”请求
  - 编写“查询关注的人”，“查询粉丝”模版

