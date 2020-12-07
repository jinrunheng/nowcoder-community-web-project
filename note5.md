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



