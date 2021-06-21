## 一个简单的股票爬虫程序，爬的是东方财富的数据

### 环境
> jdk 11

> springboot 2.5

### 说明
> 数据直接存redis，接着直接输出，没有保存mysql，也没有保存office文档

> 使用Controller，是因为SpringBootTest使用多线程时候需要阻塞main线程

> 爬虫逻辑看代码块的注释吧，入门级的爬虫思路，很简单的

