# 简介

> Reptile是一个具有高拓展性的可支持单机与集群部署Java多线程爬虫框架，该框架可简化爬虫的开发流程。该框架各个组件高内聚松耦合的特性让用户可以对不同组件进行定制来满足不同的需求。


# 架构

![Reptile.png](https://upload-images.jianshu.io/upload_images/4750376-3f10253975343c38.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 特性

+ 模块化设计，具有高度拓展性
+ 单机多线程部署
+ 支持集群多线程部署
+ 配置简单清晰
+ 单机部署时，请求爬取完毕并且无其他线程产生新请求时会自动停止并清除理资源
+ 整合Jsoup，支持HTML页面解析
+ 支持设置UserAgent池与Proxy池，并且可设置请求时随机选择或循环顺序选择UserAgent与Proxy

# 快速开始

## 使用Maven

1. clone项目并构建发布到本地仓库
```
git clone git@github.com:xiepuhuan/reptile.git
cd reptile
mvn -Dmaven.test.skip=true
```
2. 在项目中使用Maven引入对应的依赖

```
<dependency>
    <groupId>com.xiepuhuan</groupId>
    <artifactId>reptile</artifactId>
    <version>0.2</version>
</dependency>
```

# 使用方式

1. 实现`ResponseHandler`接口，重写`isSupport`与`handle`方法。
    + `isSupport`方法根据`request`和`response`参数判断是否需要处理该响应，是则返回`true`，否则返回`false`。
    + `handle`方法处理该响应，并将处理结果存储到`result`，如果从响应中有提取到要爬取的新请求则将其作为返回值返回。
    + 如果没有找到支持处理该响应的处理器则响应会被忽略。
2. 实现`Consumer`接口，重写`consume`方法，执行对数据的消费，可在该方法中对响应处理结果进行持久化等操作，目前提供了`ConsoleConsumer`与`JsonFileConsumer`等实现，默认使用`ConsoleConsumer`。

# 示例

## 单机部署
``` java
public class ZhihuPageHandler implements ResponseHandler {

    private static String[] URLS = new String[] {
            "https://www.zhihu.com/api/v4/search_v3?t=general&q=java"
    };

    @Override
    public List<Request> handler(Response response, Result result) {
        Content content = response.getContent();
        JSONObject jsonObject = JSON.parseObject(content.getContent(), JSONObject.class);
        result.setResults(jsonObject.getInnerMap());

        JSONObject paging = jsonObject.getJSONObject("paging");

        if (!paging.getBoolean("is_end")) {
            List<Request> requests = new ArrayList<>();
            requests.add(new Request(paging.getString("next")));
            return requests;
        }
        return null;
    }

    @Override
    public boolean isSupport(Request request, Response response) {
        return true;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        //　构建Reptile爬虫配置类，
        ReptileConfig config = ReptileConfig.Builder.cutom()
                .setThreadCount(8)
                .appendResponseHandlers(new ZhihuPageHandler())
                .setDeploymentMode(DeploymentModeEnum.SINGLE)
                .setConsumer(new ConsoleConsumer())
                .build();
        
        //　构建Reptile爬虫对象并添加爬去的URL
        Reptile reptile = Reptile.create(config).addUrls(URLS);
        // 启动爬虫
        reptile.start();
    }
}
```
