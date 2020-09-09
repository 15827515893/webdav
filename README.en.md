# 分布式聊天系统

#### Description
分布式聊天系统
技术栈:zookeeper,redis,websocket,springboot,mysql,mongodb
客户端A->服务器A->服务器B->客户端B   服务器与服务器之间websocket 保持长连接;转发消息时如果消息归属地不在本服务器上,通过redis寻找到消息归属客户端所在的服务器ip通信该服务器;所有服务器启动时注册zookeeper创建子节点,并且订阅父节点,有新增子节点时感知变化,创建长连接到新增服务器

#### Software Architecture
Software architecture description

#### Installation

1.  xxxx
2.  xxxx
3.  xxxx

#### Instructions

1.  xxxx
2.  xxxx
3.  xxxx

#### Contribution

1.  Fork the repository
2.  Create Feat_xxx branch
3.  Commit your code
4.  Create Pull Request


#### Gitee Feature

1.  You can use Readme\_XXX.md to support different languages, such as Readme\_en.md, Readme\_zh.md
2.  Gitee blog [blog.gitee.com](https://blog.gitee.com)
3.  Explore open source project [https://gitee.com/explore](https://gitee.com/explore)
4.  The most valuable open source project [GVP](https://gitee.com/gvp)
5.  The manual of Gitee [https://gitee.com/help](https://gitee.com/help)
6.  The most popular members  [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
