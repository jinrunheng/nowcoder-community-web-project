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

