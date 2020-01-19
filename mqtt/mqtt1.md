
MQTT Broker的需求和各大Broker对比
最后更新于2019年03月09日 / 10205次浏览 / 12次点赞 / 9条评论
broker对比，moquette ，emq
一、MQTT Broker的需求
1、基本需求

1）支持 mqtt3.1 / mqtt3.1.1协议（可选 mqtt5.0）

3.1和3.1.1是最常见的协议版本，几乎目前生产的IoT设备都支持，所以Broker也必须支持。至于5.0版本，目前各大Broker都在努力支持，不过还需要一些时间才会普及。

2）支持QoS0、QoS1（可选QoS2）

各大厂商都至少支持了QoS1，保证消息到达。一般的场景下不会用到QoS2，所以可以选择性地考虑支持QoS2

3）支持遗嘱消息

这是必须支持的功能，通常设备断开都不是主动断开的，而是没有电了才断开，属于异常断开，需要设置遗嘱消息来通知后端服务或者其他设备进行后续处理。

4）支持持久化

一些数据如QoS1消息、持久Session，需要支持持久化，这是MQTT协议规定的。

5）支持多种连接方式

MQTT over TCP：最基础的连接方式

MQTT over Websocket：在Websocket之上做MQTT封装，对APP这种客户端来说很友好

MQTT over TCP/SSL：基础连接方式做通信加密，通常SSL采用TLS

MQTT over Websocket/SSL：Websocket做通信加密，通常SSL采用TLS

6）（可选 保留消息）

保留消息的利用场景几乎可以忽略，而带来的查询成本会很高（每次订阅主题都要查一遍有没有保留消息，再加上通配符匹配，时延很高），所以不一定需要支持，具体应用具体分析。

7）支持集群

Broker要支持保持海量MQTT连接，需要做集群。集群的难点在于Session的持久化和集群通信。我们既要持久化Session的各项数据，例如正在发送但未收到ACK的QoS1消息，又要保证提取速度，这就是矛盾的事情。而根据订阅信息在内存中构建的订阅树，需要整个集群同步，如何做集群同步也是一个难点。任何一个简单的功能，像发现相同ClientID则踢掉旧会话，一旦做到集群里，就是不容易处理的事情。  

8）支持自定义验证方式

验证客户端的合法性有三点：CONNECT阶段验证是否允许连接、PUBLISH阶段验证是否允许发布、SUBSCRIBE阶段验证是否允许订阅。

CONNECT阶段需要验证ClientID、Username、Password、IP四项，不过大部分开源Broker都只支持Username和Password的验证。

PUBLISH、SUBSCRIBE的验证的目的是防止非法客户端订阅别人的主题，向别人的主题发布消息。但每台设备每次订阅、发布都要验证一次频率巨高，所以需要设计Cache和高效查询机制。
2、高级功能：支持共享订阅

共享订阅的具体含义是，多个客户端订阅同一个主题，消息只会被分发给其中的一个客户端。

共享订阅主要针对的是需要客户端负载均衡的场景，比如后端服务多个Worker，需要共享订阅来只让一个Worker得到数据。但仔细地想一想，后端服务一定有大量消息扇入，在Broker端用共享订阅可能会导致内存爆炸，还不如直接发到Kafka，利用Kafka的负载均衡来做。不过现在的Broker都在逐渐支持共享订阅，所以也是一个趋势吧。
二、MQTT Broker官方资料

官方相关链接：

mqtt官方整理的开源Broker简要列表

mqtt官方整理的开源Broker详细介绍

mqtt官方整理的开源Broker特性和性能对比
三、体验过的MQTT Broker及其对比

我以为物联网已经很成熟了，事实上最近才有大量产品上线，网上可以参考的内容不多。体验了很多开源Broker，开源的Broker根本不能直接上生产环境，只有商业版的HiveMQ和商业版的EMQ才满足了所有的需求。先列个表，这些已经算是比较优秀的Broker了，分析主要特性：

    ✔  -  支持
    ✘  -  不支持
    ？ -  不了解
     §  -  支持但做得不好（有限制）

Broker 	开源	语言	连接方式	QoS	共享订阅	持久化	集群
mosquitto
	

 ✔
	 C/C++	 4种	 全部	  ✘	？	✘
hui6075/mosquitto
	 ✔	 C/C++	 4种	 全部	  ✘	？	✔   
moquette0.10
	 ✔	 Java	 4种	 全部	  ✘	 ✔	✔
moquette0.12
	 ✔	 Java	 4种	 全部	  ✘	 §	

✘
EMQ2.0+
	 ✔	 Erlang	 4种	 全部	  ✘	 ✔	✔
EMQ3.0+
	 ✔	 Erlang	 4种	 全部	  ✔	 ✔	✔
EMQ PLUS
	 ✘	 Erlang	 4种	 全部	  ✔	 ✔	✔  
Jmqtt1.1.0
	 ✔	 Java	 无SSL	 全部	  ✘	 ✔	✘
MqttWk
	 ✔	 Java	 4种	 全部	  ✘	 §	§  
HiveMQ
	 ✘	 Java	 4种	 全部	  ✔	 ✔	✔


1、mosquitto

【简介】

mosquitto是ecplise出的开源Broker，由C/C++语言编写，目前最新版v1.5.8，是一个开源MQTT Broker。

【官方文档宣称的特性】

协议：支持mqtt 3.1 / mqtt 3.1.1

【实际的使用限制分析】

“趁着年轻”大佬早在2013年就开始研究了，当时的版本是1.2.2，那时候还有一些基础的性能问题，比如用的poll而没有用epoll，内存方面没有优化，多线程主要靠加锁等等，预计可支持10W左右链接。于是大佬自己修改了一个版本kulv2012/mosquitto，优化了那些性能，这已经是五年前了……

同样的，“逍遥子”2015年在CSDN上分析了mosquitto1.2的源码，指出epoll需要优化、订阅树需要优化。将订阅树改为了HASH表，直接查找，限制了通配符订阅功能但速度提升明显。

mosquitto可以通过桥接的方式进行集群，桥接就是靠一个mosquitto实例去做转发，其他的broker可以转发给它而已，如果客户端切换节点，session就会消失，并且一旦中转Broker挂掉，整个集群就挂了，这是一种伪集群。“hui6075”在mosquitto上做了真集群hui6075/mosquitto-cluster，也就是自定义一些消息，session可以通过这些消息进行转移。

最新版本早就没有了那些性能问题，也早就从poll改为epoll了（忘了在哪儿看到的），目前正在努力支持mqtt5，出了一个MQTT5测试版，暂时还不支持共享订阅。

无论如何，总结一句话，mosquitto是为了嵌入式设备而生，正如官方的介绍，mosquitto足够轻量，可以运行在任何低功率单片机上，包括嵌入式传感器、手机设备、嵌入式微处理器，mosquitto用C语言编写、集群做的如此简单就是证明，它不适合用来做云服务的MQTT Broker。

【推荐延伸阅读】

mosquitto github开源代码

mosquitto 官方网站

趁着年轻：《Mosquitto pub/sub服务实现代码浅析-主体框架》  

小诺Z《Mosquitto集群搭建》

逍遥子《mosquitto源码分析（一）》简介

逍遥子《mosquitto源码分析（二）》数据结构

逍遥子《mosquitto源码分析（三）》订阅树

逍遥子《mosquitto源码分析（四）》订阅树

逍遥子《mosquitto源码分析（五）》Poll和消息收发

逍遥子《mosquitto源码分析（六）》日志

逍遥子《Mosquito的优化——epoll优化（七）》

逍遥子《Mosquito的优化——订阅树优化（八）》

逍遥子《Mosquito的优化——其他优化（九）》

 
2、EMQ （emqttd）

【简介】

EMQ是国人出产的一个开源Broker，已经用于很多企业生产了，几乎是目前的全能Broker了，文档和资料也非常齐全，但它是用Erlang语言编写的，这是一个不常见的语言。有两个版本2.0和3.0，最大的区别是3.0的集群化更好，支持集群共享订阅功能，2.0只支持本地共享订阅功能。同时3.0支持mqtt5.0，其他的都是一些性能优化。

【官方文档宣称的特性】

MQTT 3.1 / 3.1.1 / 5.0（EMQ3.0）

完整QoS支持

单节点100万连接

分布式集群或桥接（还支持mosquitto桥接、rsmb桥接）、脑裂自动愈合

LDAP, MySQL, PostgreSQL, Redis, MongoDB等验证插件

完整连接方式支持

API、Web监控界面

本地共享订阅（EMQ2.0）、集群共享订阅（EMQ3.0）

$SYS统计信息主题

自定义插件开发

【实际的使用限制分析】

几乎是完美的，只有一点限制，那就是开源版本不支持消息持久化：

    EMQ 1.0 版本不支持服务器内部消息持久化，这是一个架构设计选择。首先，EMQ 解决的核心问题是连接与路由；其次，我们认为内置持久化是个错误设计。 
    传统内置消息持久化的 MQ 服务器，比如广泛使用的 JMS 服务器 ActiveMQ，几乎每个大版本都在重新设计持久化部分。内置消息持久化在设计上有两个问题: 
    1）如何平衡内存与磁盘使用？消息路由基于内存，消息存储是基于磁盘。 
    2）多服务器分布集群架构下，如何放置 Queue 如何复制 Queue 的消息？ 
    Kafka 在上述问题上，做出了正确的设计：一个完全基于磁盘分布式 Commit Log 的消息服务器。 
    EMQ 2.0 版本将发布 EMQ X 平台产品，支持消息持久化到 Redis、Kafka、Cassandra、PostgreSQL 等数据库。 
    设计上分离消息路由与消息存储职责后，数据复制容灾备份甚至应用集成，可以在数据层面灵活实现。

这是MQTT的标准协议规定的啊，看完源码后发现它的普通Publish消息是持久化到分布式数据库Mnesia了（但是如果节点崩得多也会丢失），而离线消息队列是基于内存的，也就是Broker一崩消息就丢失了，很多人都在寻求解决方法，都没有好的方法。还有个问题是后端服务怎么接上去，EMQ的设计根本没提到后端服务的问题。大部分的解决方法都是编写一个插件，把MQTT消息丢到Kafka，后端服务处理Kafka的数据，但是后端服务除了收还要发呀，如果直接作为客户端连上去，Broker会内存爆炸因为后端服务要发送的消息太多了。如果你有好的想法，请一定要教教我。

【推荐延伸阅读】  

EMQ github 源码

EMQ wiki

EMQ 官网

知乎：分布式开源物联网MQTT消息服务器EMQ怎么做数据的存储？

Dr_C《EMQ集成Kafka插件编写过程 emq_plugin_kafka》

响亮响亮《EMQ扩展插件-emq_plugin_kafka》

无脑仔的小明《物联网架构成长之路(3)-EMQ消息服务器了解》

无脑仔的小明《物联网架构成长之路(4)-EMQ插件创建》

无脑仔的小明《物联网架构成长之路(5)-EMQ插件配置》

无脑仔的小明《物联网架构成长之路(6)-EMQ权限控制》

无脑仔的小明《物联网架构成长之路(7)-EMQ权限验证小结》

无脑仔的小明《物联网架构成长之路(8)-EMQ-Hook了解、连接Kafka发送消息》

无脑仔的小明《物联网架构成长之路(12)-物联网架构小结1》


3、HiveMQ

【简介】

HiveMQ是企业级的Broker，用Java编写，代码真的赏心悦目。由于是收费的，没有公开的源码可以看，我只从一个反编译的大佬那里看到几张截图而已，只是一些截图就能够看到编写者的Java水平真的很高……

【官方文档宣称的特性】

MQTT 3.1 / 3.1.1 / 5.0

完整QoS支持

分布式集群支持

持久化支持

流量控制支持

完整连接方式支持

IPv6支持

集群共享订阅

$SYS统计信息主题

JMX性能监控

日志打印

Docker部署

……

【实际的使用限制分析】

功能上齐全得让人想哭，唯一的限制就是收费，没有任何源码可以参考。它的集群是基于Jgroups的，持久化的数据都是本地+Jgroups同步，自己编写了一套一致性Hash和VectorClock解决冲突……订阅树也是完整的订阅树，优秀的缓存和并发访问控制，集群进行数据同步。多线程和并发等用的google的guava进行防御性编程，实在是太厉害了。如果你有源码，请多发给我一份，我只是用来学习，谢谢！

【推荐延伸阅读】  

HiveMQ官网

西安PP《MQTT---HiveMQ源码详解(一)概览》

西安PP《MQTT---HiveMQ源码详解(二)结构与启动》

西安PP《MQTT---HiveMQ源码详解(三)配置加载》

西安PP《MQTT---HiveMQ源码详解(四)插件加载》

西安PP《MQTT---HiveMQ源码详解(五)Netty-启动与Listeners加载》

西安PP《MQTT---HiveMQ源码详解(六)Netty-Handlers总览》

西安PP《MQTT---HiveMQ源码详解(七)Netty-SSL/NoSSL》

西安PP《MQTT---HiveMQ源码详解(八)Netty-WebSocket》

西安PP《MQTT---HiveMQ源码详解(九)Netty-Codec》

西安PP《MQTT---HiveMQ源码详解(十)Netty-Statistics》

西安PP《MQTT---HiveMQ源码详解(十一)Netty-Throttling》

西安PP《MQTT---HiveMQ源码详解(十二)Netty-MQTT消息、事件处理(流程)》

西安PP《MQTT---HiveMQ源码详解(十三)Netty-MQTT消息、事件处理(源码举例解读)》

西安PP《MQTT---HiveMQ源码详解(十四)Persistence-LocalPersistence》

西安PP《MQTT---HiveMQ源码详解(十五)Persistence-Cluster/Single》

西安PP《MQTT---HiveMQ源码详解(十六)TopicTree》

西安PP《MQTT---HiveMQ源码详解(十七)Cluster-Consistent Hashing Ring & Node Lifecycle》

西安PP《MQTT---HiveMQ源码详解(十八)Cluster-kryo与Serializer》

西安PP《MQTT---HiveMQ源码详解(十九)Cluster-Request/Response》

西安PP《MQTT---HiveMQ源码详解(二十)Cluster-Replicate/VectorClock》

西安PP《MQTT---HiveMQ源码详解(二十一)完结篇》

西安PP《MQTT---HiveMQ源码详解(外传)为什么使用Xodus》


4、MqttWk

【简介】

一个阿里大佬编写的基于 nutzboot + netty + redis + kafka 实现的MQTT服务开源broker，代码非常简洁干净，一看就懂。nutzboot是国人编写的类似于springboot的开源架构，它有一系列的产品，功能和代码外观都和spring全家桶很像，但比spring全家桶轻量。

【官方文档宣称的特性】

MQTT 3.1.1

完整的QoS服务

完整的连接方式

Kafka消息转发

集群功能

分发重试

【实际的使用限制分析】

1）MessageQueue没有排序：是直接插入Redis的key-value，并不是一个队列

2）消息分发重试很差：对于未确认的QoS1消息，只会在重新连接的时候下发，如果一直在线就会一直淤积

3）集群功能很差：用Redis的订阅发布当作消息总线来构建集群，而且我刚熟悉的时候还有问题（1.0.7版本），提交了issue后更新到1.0.8，不过集群这块还是不太好。

4）Kafka消息转发：只是单纯地转发而已，没有从后端服务接收消息的代码。而且用原始的代码去编写的转发（为了使用没有Kafka功能的nutzboot，没有用spring的Kafka相关注解）。

5）主题：主题有一些限制，不能以/结尾，不支持通配符订阅+

我当时还测试出了一些其他的问题，但是忘记了，而且这个项目竟然是上生产的项目……经历过2万设备连接，我在commitlog里面看到作者还写“不知道Redis会不会有性能问题”这种提交信息……不过代码真的非常非常清晰简洁，有助于理解MQTT协议交互过程。

【推荐延伸阅读】  

MqttWk github 开源代码

开源中国：MqttWk介绍

MqttWk 码云 开源代码


5、Jmqtt

【简介】

jmqtt是一个大佬对开源Broker现状不满意，自己做出来的一个开源Broker。代码思路很清晰，尤其对CONNECT做了优化，而且Session的过期处理得也很好，编写了大量多线程代码，看得出是Java多线程高手。

【官方文档宣称的特性】

完整的QoS等级

支持MQTT、Websocket连接方式

支持RocksDB进行数据本地存储

【实际的使用限制分析】

不支持集群，不支持共享订阅，不支持SSL，MessageQueue不是队列……但是和大佬交流得最深，教了我很多东西，很感谢。

【推荐延伸阅读】

jmqtt github源码

jmqtt 中文自述

Ciciz：《MQTT Broker选型》

Ciciz：《IoT MQ设计篇：调研与协议选型》

Ciciz：《IoT MQ设计篇：开源or自研，系统复杂度分析》

Ciciz：《IoT MQ设计篇：基于开源项目二次开发的坑》

Ciciz：《IoT MQ设计篇：最终架构与jmqtt介绍》 


6、Moquette 0.10

【简介】

0.10和0.12是两个核心版本，功能变化巨大，这里分开叙述。Moquette是我参考得最多的一个Broker了，它是唯一的功能齐全、Java语言编写的开源Broker，网上很多人都是以Moquette为基础进行开发的。Moquette怎么样呢，以研究HiveMQ的“西安PP”大佬的原话说——就是一个玩具项目……看和HiveMQ截图的源码成熟度对比其实我也能感觉出来。但免费的玩具只有这一个啊，没得挑。

【官方文档宣称的特性】

完整QoS服务

完整连接认证方式

多种持久化存储支持

集群支持

性能监控支持

【实际的使用限制分析】

“专注的力量”用它的代码进行压测，发现有内存泄漏问题，于是自己修复了这些东西，还支持了Redis持久化，发布了一个开源版本irubant/moquette。moquette的集群只是用了hazelcast作消息总线，不支持共享订阅，而且所有的消息都是广播的，也没有在不同Broker节点上相同clinetID相互踢下线的功能。没有消息重发机制，只会在重连的时候重发。订阅树编写得非常复杂，还不断地以CAS（比较并替换）操作在并发环境下更换根节点，会带来很多性能问题。

【推荐延伸阅读】

moquttte源码

moquette官网

专注的力量《开源MQTT中间件：moquette》

袁志健《从moquette源码看IOT接入协议MQTT的实现》


7、Moquette 0.12

【简介】

Moquette0.12将整个项目简化了，Jar包管理方式从Maven改为Gradle，不再支持集群，说是为了让人1分钟就能快速上手，放弃了Hazelcast说这种方式做集群不好，准备先支持MQTT5.0，再考虑做集群的事情。不过这个版本改进了订阅树，还支持了重发未ACK的消息，MessageQueue也采用了Queue，去掉了大部分持久化方式，只保留H2。

【官方文档宣称的特性】

支持完整QoS

支持完整连接认证方式

【实际的使用限制分析】

完全地退化……不过代码更清晰一点了，各个功能模块划分得更清楚。但是单机是最大的缺陷，很难改成集群，几乎要全改。


8、其他

最近又出了一些新的Broker，例如基于moquette的cassandana，宣称已经用于生产，还有一些新特性，想去看看源码是怎么写的。

至于Apache ActiveMQ、Apache ActiveMQ Artemis这种基于消息队列制作的MQTT Broker还没有使用过，只是看了一些文章说有性能问题。