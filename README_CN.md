# EZ-Cache

一个易于使用且功能完善的多级缓存框架


## 功能

1. 支持多种缓存类型
2. 杜绝缓存击穿、缓存穿透等其他可能存在的缓存问题 
3. 缓存配置热更新
4. 缓存一致性 
5. 热 key 检测 
6. 支持缓存批量操作 
7. 支持多种序列化方式 
8. "在途读"请求缓冲区: 当有多个请求同一个数据时，选举出一个 leader 去数据源加载数据，其它请求则等待其拿到的数据。并由 leader 将数据写入缓存
9. 缓存分布式自动刷新
10. 缓存异步刷新