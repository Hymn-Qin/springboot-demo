[EMQX](https://github.com/emqx/emqx/blob/master/README-CN.md)

1. 拉取最新镜像 
在docker中执行
```docker
1. 在阿里的官方镜像网站上去找EMQ镜像地址 https://dev.aliyun.com/search.html
docker pull registry.cn-hangzhou.aliyuncs.com/synbop/emqttd:2.3.6
2. docker run --restart=always -d --name emqx -p 1883:1883 -p 8083:8083 -p 8883:8883 -p 8084:8084 -p 18083:18083 emqx/emqx
```
2. 运行镜像
```docker
docker run --name emq -p 18083:18083 -p 1883:1883 -p 8084:8084 -p 8883:8883 -p 8083:8083 -d registry.cn-hangzhou.aliyuncs.com/synbop/emqttd:2.3.6
lsof -i:8080 检测端口占用
-name给镜像命名 
–p 指定端口 
启动registry.cn-hangzhou.aliyuncs.com/synbop/emqttd:2.3.6这个镜像容器 
启动成功之后会得到一个容器ID 
docker ps : 查看所有正在运行的容器 
docket ps –a : 查看所有的容器

镜像配置说明:
- 防火墙开启1883(MQTT)
- 8083(WebSocket)
- 18083(Dashboard控制台)端口
- Dashboard 控制台,默认管理员: admin, 密码: public
- 在浏览器中输入 http:192.168.99.100:18083进入Web控制台，用户名：admin 密码：public
映像默认允许1万线 MQTT 连接，最大可配置到10万线。映像内存占用: 5万连接/1G内存(from官方文档)
```
3. 在浏览器中输入 http:192.168.99.100: 18083 
登录的用户名：admin 密码：public 这是默认的

4. mySql 配置
docker exec -it emq20 /bin/sh 进入控制台

1.首先先关闭匿名认证(默认是开启的谁都能够登录)

```$xslt
    vi /opt/emqttd/etc/eqm.conf

    ## Allow Anonymous authentication
    mqtt.allow_anonymous = false
```

2.用户和权限的mysql表

```mysql
    CREATE TABLE mqtt_user (
    id int(11) unsigned NOT NULL AUTO_INCREMENT,
    username varchar(100) DEFAULT NULL,
    password varchar(100) DEFAULT NULL,
    salt varchar(20) DEFAULT NULL,
    is_superuser tinyint(1) DEFAULT 0,
    created datetime DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY mqtt_username (username)
    ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
```

```mysql
    CREATE TABLE mqtt_acl (
    id int(11) unsigned NOT NULL AUTO_INCREMENT,
    allow int(1) DEFAULT NULL COMMENT ‘0: deny, 1: allow’,
    ipaddr varchar(60) DEFAULT NULL COMMENT ‘IpAddress’,
    username varchar(100) DEFAULT NULL COMMENT ‘Username’,
    clientid varchar(100) DEFAULT NULL COMMENT ‘ClientId’,
    access int(2) NOT NULL COMMENT ‘1: subscribe, 2: publish, 3: pubsub’,
    topic varchar(100) NOT NULL DEFAULT ” COMMENT ‘Topic Filter’,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

3.修改mysql配置文件
```$xslt
    vi /opt/emqttd/etc/plugins/emq_auth_mysql.conf
    auth.mysql.server = xxxxxxxxx:3306
    auth.mysql.username = root
    auth.mysql.password = xxxxxxxx
    auth.mysql.database = emq
```

可以配置超级管理员(超级管理员会无视ACL规则对所有的topic都有订阅和推送的权限)

    update mqtt_user set is_superuser=1 where id=1;

4.重启

    /opt/emqttd/bin
    emqttd stop
    /opt/emqttd/bin
    emqttd start
    emqttd_ctl plugins load emq_auth_mysql

5. 到此我们的EMQ服务器就搭建好了
```docker
docker restart 容器ID
```
重启容器