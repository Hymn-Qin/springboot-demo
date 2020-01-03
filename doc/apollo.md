## docker 部署apollo服务
1. 添加环境变量
   本次部署一个Dev和Pro双配置环境，在Apollo生产环境中需要注意的是portal（默认端口：8070）服务只需要在生产环境部署一个即可，而每个环境的都需要部署独立的admin和config服务。 
   镜像中默认四个环境以及其config和admin端口分别为DEV（8080-8090），FAT（8081-8091），UAT（8082-8092），PRO（8083-8093），启动之前请确保要启动的端口未被占用。
  ``` lsof -i:8080```
2. 在MySql中创建ApolloPortalDB数据库
 执行初始化脚本 apollo_portal_v1.0.0_initialization.sql
   因为Apollo一个环境需要一个ApolloConfigDB所以此次安装我们还要创建两个数据库
   1. ApolloConfigDBDev(Dev环境配置),
   2. ApolloConfigDBPro（Pro环境配置）；
   3. ApolloConfig初始化脚本 apollo_config_v1.0.0_initialization.sql
3. docker 安装
```docker
docker run --net="host" --name apollo -d \ 
 -e PORTAL_DB = 'jdbc:mysql://xxx.xxx.xxx.xxx:3306/ApolloPortalDB?characterEncoding=utf8' \
 -e PORTAL_DB_USER = 'root' \
 -e PORTAL_DB_PWD = '123456' \
 -e DEV_DB = 'jdbc:mysql://xxx.xxx.xxx.xxx:3306/ApolloConfigDBDev?characterEncoding=utf8' \
 -e DEV_DB_USER = 'root' \
 -e DEV_DB_PWD = '123456' \
 -e PRO_DB = 'jdbc:mysql://xxx.xxx.xxx.xxx:3306/ApolloConfigDBPro?characterEncoding=utf8' \
 -e PRO_DB_USER = 'root' \
 -e PRO_DB_PWD = '123456' idoop/docker-apollo:latest
```
4. 打开localhost:8070地址用默认的用户名密码 apollo admin登录新建一个demo项目发现项目环境只有DEV
虽然Apollo安装完毕，但是还是有很多坑~等待着我们一个个的解决。
    1. 因为初始化ApolloConfigDBPro是默认连接的Eureka是dev环境的Eureka修改ApolloConfigDBPro数据库表ServerConfig中eureka.service.url的端口为8083。
    2. 因为ApolloPortalDB默认只有一个dev环境，修改ApolloPortalDB数据库表ServerConfig中apollo.portal.envs值为dev,pro(多环境用逗号隔开不区分大小写)
    3. 输入localhost:8080,localhost:8083如图:
5. springboot 和 Apollo 集成


