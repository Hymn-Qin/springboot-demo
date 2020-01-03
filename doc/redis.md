#### docker 安装redis
```$xslt
docker run --restart=always --name dev_redis -p 6379:6379 -d redis:latest redis-server
docker run -d -p 6379:6379 -v $PWD/conf/redis.conf:/usr/local/etc/redis/redis.conf -v $PWD/data:/data --name dev-redis docker.io/redis redis-server /usr/local/etc/redis/redis.conf --appendonly yes
    d：表示后台运行，不加-d执行上面的命令你就会看到redis启动的日志信息了

    -p：表示端口映射，冒号左面的是我们的宿主机的端口，也就是我们虚拟机的端口，而右侧则表示的是mysql容器内的端口

    --name：是我们给redis容器取的名字

    -v：表示挂载路径，$PWD表示当前目录下，冒号左面的表示我们宿主机的挂载目录，也就是我们虚拟机所在的文件路径，冒号右边则表是的是redis容器在容器内部的路径，上面的命令我分别挂载了redis.conf(redis的配置文件)，如需使用配置文件的方式启动redis，这里则需要加上，还有redis存放数据所在的目录

    --appendonly yes：表示redis开启持久化策略
```

1. 连接redis的几种方式
```$xslt
docker exec -ti d0b86 redis-cli

docker exec -ti d0b86 redis-cli -h localhost -p 6379 
docker exec -ti d0b86 redis-cli -h 127.0.0.1 -p 6379 
docker exec -ti d0b86 redis-cli -h 172.17.0.3 -p 6379 

使用redis镜像执行redis-cli命令连接到刚启动的容器,主机IP为172.17.0.1
docker exec -it redis_s redis-cli
如果连接远程：
docker exec -it redis_s redis-cli -h 192.168.1.100 -p 6379 -a your_password //如果有密码 使用 -a参数
```
2. 查看容器的ip
```$xslt
docker inspect redis_s | grep IPAddress
```

## Ubuntu 

1. 安装redis
```$xslt
sudo apt-get install redis-server
```
2. 配置
```$xslt
sudo vim /etc/redis/redis.conf
```
```$xslt
    1. 配置远程登录
    找到如下“bind 127.0.0.1 ::1”这一行：
    redis默认是只能本地进行访问的，在前面加上“#”号，把这一行注释掉，表示允许任意ip进行连接。
    (进行了这一步后，使用redis就不需要先输入命令“redis-server”打开服务了)
    2. 设置密码
    在进行上一步操作后即可远程连接了，但是为了安全性考虑，推荐给redis设置一个密码。
    在配置文件中加入：requirepass 你要设置的密码
    例如下图，把密码设为abc：
    3. 修改端口号
    修改Redis的默认端口
    port 6379
    4. 守护进程运行
    推荐改为yes，以守护进程运行
    daemonize no|yes
    5. 重启redis
    /etc/init.d/redis-server restart
    6. 链接redis
    redis-cli -a password
```
```$xslt
安装Redis服务器，会自动地一起安装Redis命令行客户端程序。命令行输入 redis-cli 如果设置了密码hzlarmredis-cli -a hzlarm
常用命令： Redis命令不区分大小写
ping返回PONG表示畅通
help 命令行的帮助
quit 或者Ctrl+d或者Ctrl+c退出
键的命令

    查找键，参数支持正则KEYS pattern例如keys *查看所有的key列表
    判断键是否存在，如果存在返回1，不存在返回0EXISTS key [key ...]
    查看键对应的value的类型TYPE key
    删除键及对应的值DEL key [key ...]
    设置过期时间，以秒为单位
    创建时没有设置过期时间则一直存在，直到使用使用DEL移除EXPIRE key seconds
    查看有效时间，以秒为单位TTL key
    修改 key 的名称RENAME key newkey
```

3. 主从配置
```$xslt
主从配置

    一个master可以拥有多个slave，一个slave又可以拥有多个slave，如此下去，形成了强大的多级服务器集群架构
    比如，将ip为192.168.1.10的机器作为主服务器，将ip为192.168.1.11的机器作为从服务器

设置主服务器的配置

sudo vi /etc/redis/redis.conf修改绑定ip
bind 192.168.1.10 重启redis服务器
设置从服务器的配置

sudo vi /etc/redis/redis.conf注意：在slaveof后面写主机ip，再写端口，而且端口必须写
bind 192.168.1.11
slaveof 192.168.1.10 6379
重启redis服务器

启动客户端redis-cli -h修改后的ip
在master和slave分别执行info命令，查看输出信息
在master上写数据
set hello world
在slave上读数据
get hello
```

sudo docker run -d --net=host --name redis-manager  \
-e DATASOURCE_DATABASE='redis_manager' \
-e DATASOURCE_URL='jdbc:mysql://127.0.0.1:3306/redis_manager?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2b8' \
-e DATASOURCE_USERNAME='root' \
-e DATASOURCE_PASSWORD='123456' \
reasonduan/redis-manager