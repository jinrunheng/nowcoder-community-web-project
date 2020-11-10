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

    

