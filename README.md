# 分布式聊天系统

#### 介绍
分布式聊天系统
技术栈:zookeeper,redis,websocket,springboot,mysql,mongodb
客户端A->服务器A->服务器B->客户端B   服务器与服务器之间websocket 保持长连接;转发消息时如果消息归属地不在本服务器上,通过redis寻找到消息归属客户端所在的服务器ip通信该服务器;所有服务器启动时注册zookeeper创建子节点,并且订阅父节点,有新增子节点时感知变化,创建长连接到新增服务器

#### 软件架构
软件架构说明


#### 安装教程

1.  本地启动redis  本地启动zk
2.  配置idea edit configuration    programargument参数设置： --spring.profiles.active=run1   设置三个 run1  run2 run3
3.  右上角启动多个服务器   run1  run2 run3
4.  main方式先后启动   ebsocketclient   WebsocketClient2

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
