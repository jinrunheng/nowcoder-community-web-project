## Kafka,构建TB级异步消息队列

#### 1. 阻塞队列

- BlockingQueue
  - 解决线程通信的问题
  - 阻塞方法：put，take
- 生产者消费者模式
  - 生产者：产生数据的线程
  - 消费者：使用数据的线程
- 实现类
  - ArrayBlockingQueue
  - LinkedBlockingQueue
  - PriorityBlockingQueue,SynchronousQueue,DelayQueue

阻塞队列，生产者消费者示例程序：

```java
package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueDemo {

    public static void main(String[] args) {
        // 生产者和消费者共用一个阻塞队列；阻塞队列的最大长度为10
        BlockingQueue queue = new ArrayBlockingQueue(10);
      	// 1个生产者 ，生产数据，最多生产100个数据
        new Thread(new Producer(queue)).start();
				
      	// 三个消费者线程 消费数据
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

// 生产者
class Producer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费：" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```



程序运行结果：

```
Thread-0生产：1
Thread-0生产：2
Thread-0生产：3
Thread-0生产：4
Thread-0生产：5
Thread-0生产：6
Thread-0生产：7
Thread-0生产：8
Thread-0生产：9
Thread-0生产：10
Thread-0生产：10
Thread-3消费：9
Thread-2消费：9
... ...
Thread-3消费：9
Thread-0生产：10
Thread-1消费：9
Thread-3消费：8
Thread-1消费：7
Thread-1消费：6
Thread-2消费：5
Thread-3消费：4
Thread-1消费：3
Thread-3消费：2
Thread-2消费：1
Thread-1消费：0

```

可以看到，阻塞队列从一开始，因为生产者的生产速度比较快，间隔20ms就会生产一个数据，所以，前期生产者会一直生产数据直至填满阻塞队列，当阻塞队列长度为10以后，生产者就不会再生产数据了，继而是消费者消费，生产和三个消费者线程交替进行生产和消费。到最后，生产者生产够了100个数据，就停产，由消费者将数据消费完毕。

#### 2. Kafka入门

- Kafka简洁

  - Kafka是一个分布式的流媒体平台。
  - 应用：消息系统，日志收集，用户行为追踪，流式处理。

- Kafka特点

  - 高吞吐量，消息持久化，高可靠性，高扩展性。

- Kafka术语

  - Broker,Zookeeper
  - Topic,Partition,Offse t
  - Leader Replica,Follower Replica


kafka的启动：

1. 启动zookeeper

   在kafka的安装包的bin目录下运行命令：`sh zookeeper-server-start.sh ../config/zookeeper.properties` 启动zookeeper

2. 运行kafka

   启动zookeeper后，在kafka的安装包bin目录下运行命令：`sh kafka-server-start.sh ../config/server.properties`

kafka的使用：

1. kafka创建主题

   进入到kafka安装包的bin目录下运行命令：`sh kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test`

2. 查看刚刚创建的主题

   查看刚刚创建的test主题，使用命令：`sh kafka-topics.sh --list --bootstrap-server localhost:9092`

   可以看到返回结果 test

3. 调用生产者发送消息

   使用命令: `sh kafka-console-producer.sh --broker-list localhost:9092 --topic test`

   示例：

   发送两条消息分别为“hello”，“world”

4. 调用消费者接收消息

   再开启一个新的terminal，执行命令：`sh kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning`

   其中`--from-beginning` 表示从头开始接收消息

   运行这条命令后，我们可以看到终端显示了生产者生产的两条消息：

   ```
   hello
   world
   ```

   与此同时，我们再次调用生产者发送消息，消费者也能马上接收到

#### 3.  Spring整合Kafka

- 引入依赖

  - spring-kafka

    ```xml
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
    </dependency>
    ```

- 配置Kafka

  - 配置server, consumer

    在application.properties配置文件中：

    ```properties
    # KafkaProperties
    spring.kafka.bootstrap-servers=localhost:9092
    spring.kafka.consumer.group-id=test-consumer-group
    # 是否自动提交消费者的偏移量
    spring.kafka.consumer.enable-auto-commit=true
    # 提交的频率 单位为ms
    spring.kafka.consumer.auto-commit-interval=3000
    ```

- 访问Kafka

  - 生产者 : `kafkaTemplate.send(topic,data);`
  - 消费者

```java
@KafkaListener(topics = {"test"})
public void handleMessage(ConsumerRecord record){
		// ... ... 
}
```

示例程序

```java
package com.nowcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("test","你好");
        kafkaProducer.sendMessage("test","再见");

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

// 生产者
@Component
class KafkaProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

// 消费者
@Component
class KafkaConsumer {

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
```

该程序为生产者生产消息，消费者被动接收到消息。然后线程sleep10秒钟，程序结束运行

#### 4. 发送系统通知

- 触发事件
  - 评论后，发布通知
  - 点赞后，发布通知
  - 关注后，发布通知
- 处理事件
  - 封装事件对象
  - 开发事件的生产者
  - 开发事件的消费者

#### 5. 







































​	