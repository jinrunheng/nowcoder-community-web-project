## Elasticsearch，分布式搜索引擎

#### 1. Elasticsearch入门

- Elasticsearch简介

  - 一个分布式的，Restful风格的搜索引擎
  - 支持对各种类型的数据的检索
  - 搜索速度快，可以提供实时的搜索服务
  - 便于水平扩展，每秒可以处理PB级海量数据
  
- Elasticsearch术语

  - 索引，相当于数据库（6.0以后相当于数据表）
  - 类型，相当于数据表（6.0以后逐渐废弃了类型）
  - 文档，相当于数据表中的一行数据
  - 字段，相当于数据表中的一个字段
  - 从ES 6.0开始 就废弃掉了类型，目前索引对应数据表
  
- 集群，节点，分片，副本

- Elasticsearch下载
  
- 我个人下载的是ES6.4.3的macOS版本:[链接](https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-4-3)
  
- 我的配置
  - 在ES配置文件中
  - 集群名称:`cluster.name: nowcoder`
  - 存放数据路径:`path.data: /Users/macbook/Desktop/data/elasticsearch/data/`
  - 存放日志路径:`path.logs: /Users/macbook/Desktop/data/elasticsearch/logs/`
  - mac上配置elasticsearch的环境变量：
    - `vi ~/.bash_profile`
    - `export PATH=/Users/macbook/Downloads/elasticsearch-6.4.3/bin:$PATH`
    - `vi ~/.zshrc.pre-oh-my-zsh`
    - ``export PATH=/Users/macbook/Downloads/elasticsearch-6.4.3/bin:$PATH``
  
- 下载中文分词插件[elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik)

  - 将分词插件解压缩到`elasticsearch`安装路径下的`plugins`目录下的`ik`文件夹内
  - `IKAnalyzer.cfg.xml`文件中可以配置扩展词典和配置停止词

- 下载postman

- 启动elasticsearch服务器：进入到安装文件夹下的`bin`目录下，执行命令`elasticsearch`

  - 查看健康状况：在浏览器中访问：`http://localhost:9200/_cat/health?v`

  - 查看节点：`http://localhost:9200/_cat/nodes?v`

  - 查看当前服务器中索引：`localhost:9200/_cat/indices?v`

  - 创建索引"test"：`curl -X PUT "localhost:9200/test"`

  - 删除索引"test"：`curl -X DELETE "localhost:9200/test"`

  - 插入数据，使用PUT请求，示例：`localhost:9200/test/_doc/1`  Body中使用JSON格式`{"title":"Hello","content":"How are you"}`

  - 查刚刚插入的数据：GET : `localhost:9200/test/_doc/1`

  - 删除刚插入的数据：DELETE:`localhost:9200/test/_doc/1`

  - 不加条件的search:GET:`localhost:9200/test/_search`

  - 加条件的search:GET:`localhost:9200/test/_search?q=title:互联网`

  - 加条件的search:GET:`localhost:9200/test/_search?q=content:运营实习`

  - 加条件的search:GET:`localhost:9200/test/_search`；其余条件复杂的搜索查询在Body中以JSON格式发送,本示例为查询title和content都有互联网字段的查询结果：

    - ```json
      {
          "query":{
              "multi_match":{
                  "query":"互联网",
                  "fields":["title","content"]
              }
          }
      }
      ```

#### 2. 

