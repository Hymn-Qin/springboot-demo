#### docker 安装mysql
```$xslt
1. docker pull mysql
2. docker run --restart=always -di --name dev_mysql -p 33306:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql
docker run -d --restart=always -p 3306:3306 -v /home/qin/IntelliJIDEAProjects/springboot-demo/my.cnf:/etc/my.cnf -e MYSQL_ROOT_PASSWORD=123456 --name dev-mysql mysql:latest
3. docker exec -it dev_mysql /bin/bash
4. mysql -u root -p

保存路径 mysql.conf.d/mysqld.cnf datadir
使用mysql命令设置：
SHOW VARIABLES LIKE 'datadir';命令，返回数据库文件保存路径
show variables like "%char%";
如果仍有编码不是utf8的，请检查配置文件，也可使用mysql命令设置：

set character_set_client = utf8mb4;

set character_set_server = utf8mb4;

set character_set_connection = utf8mb4;

set character_set_database = utf8mb4;

set character_set_results = utf8mb4;

set collation_connection = utf8_general_ci;

set collation_database = utf8_general_ci;

set collation_server = utf8_general_ci;
```

1. mysql的安装

我是使用apt-get直接安装的 ：sudo apt-get install mysql-server

   sudo apt-get install mysql-client

 

2. 配置mysql管理员密码

sudo mysqladmin -u root 当前密码 新密码

安装的时候貌似也没遇到什么障碍

 

3. 查看mysql的状态

sudo netstat -tap | grep mysql

 

4. 启动/停止/重启mysql

sudo  /etc/init.d/mysql start

sudo  /etc/init.d/mysql stop

sudo  /etc/init.d/mysql restart

操作之后 某些版本会提示可以使用 sudo  /etc/init.d/start mysql

 

5. 登录mysql server

mysql -u root -p

会提示输入密码（ENTER PASSWORD:）

 

6. 远程登录mysql

mysql 主机名 -u root -p

 

7. 退出mysql server

mysql> ctrl c

 

8. 查看版本信息

mysql>s即\s

 

9. 查看帮助

mysql>help

 

10. 查看该mysql的版本 和 server的当前日期

全加上“\”

mysql>select version().current_date

 

11. 如果你遇到文件权限问题，例如：权限为root 而你的用户名为zhangsan，可以使用以下代码更改权限：

sudo chown username target

sudo chgrp username target

 

如果是文件夹而里面的文件也是被权限控制的 可以使用sudo chown/chgrp -r username target

当然 可以使用：sudo chmod u+x target 将只读更改为读写

注：username(zhangsan)  target(目标文件或者文件夹的相对路径)

 

12. 现在已经安装配置好了mysql client及mysql server（如果安装过程依然有问题的，可以搜索大神们的方案，强烈推荐我们强大的 www.iteye.com）

现在进行数据迁移。

因为这次我是将远程服务器上的数据库迁移到本地，作为内部测试开发使用，所以我选择使用mysqldump这个工具。

 

13. 连接、登录远程服务端

ssh 服务器名称@具体ip地址（例如：202.108.22.5）

ENTER PASSWORD：XXXXXXX

接着输入：sudo mysql -u root -p

接着再次输入远程数据库登录密码，至于上面那个root  也有可能是其他username

 

14. 操作远程服务端

mysql>show databases;

mysql>use 具体数据库名称;

mysql>show tables;

mysql>desc 具体表名称;

mysql>select * from 具体表名称;

操作远程服务端，主要是为了使用户确定需要迁移的数据库

 

15. 远端数据库迁出（导出）

网上一般有四种方法介绍数据库迁移的，我个人觉得还是使用mysqldump最直接 最方便 最效率

mysqldump工具主要是进行备份 和 回复 即 导出 与 导入

shell:mysqldump -u root -p 数据库密码 数据库名称 > 具体备份路径

例如：mysqldump -u root -p 123456  studentdata  > home/root/studentdatabak.sql

 

16. 本地数据库迁入（导入）

在本地系统中，两种方法。我觉得第二种更好。

方法一：

shell:mysql -u root -p

mysql>show databases;

mysql>CREATE DATABASENAME(即目标数据库，一般需要新建);

mysql>use DATABASENAME;

mysql>source 具体文件路径(即studentdatabak.sql);

 

方法二：

shell:mysql -u root -p

mysql>show databases;

mysql>CREATE DATABASENAME(即目标数据库，一般需要新建);

注意：新开一个终端

shell：mysqldump -u root -p 123456  studentdata < home/root/studentdatabak.sql

 

到此 就完成了远程 mysql数据库的迁移

 

 

上文完成了整个数据库的迁移，但是在数据库完整迁移之后，仍需要继续配置。因为远程服务端的ip变为了本地的ip。

下面，我们进行ip的具体配置。

 

1、首先，我们需要查看本地默认的3306端口是否开启。

shell: netstat -nat

显示如下：

 

Proto   Recv-Q    Send-Q   Local Address   Foreign Address     State

tcp          0            0           127.0.0.1          0.0.0.0:* LISTEN

 

因此，我们需要修改 etc/mysql下 my.cnf文件

找到 bind.address

将127.0.0.1  修改为本地ip地址，例如：192.168.0.100

当然，有可能my.cnf文件无法直接修改  请换到root权限进行修改