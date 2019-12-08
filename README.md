
## nginx 配置
#### app.conf
```
server {
    listen 80;
    charset utf-8;
    access_log off;

    location / {
    //配置请求转发，将80端口的请求转发到服务 app 的8080端口。
    //其中proxy_pass http://app:8080这块的配置信息需要解释一下，
    //这里使用是app而不是localhost，是因为他们没有在一个容器中，
    //在一组 compose 的服务通讯需要使用 services 的名称进行访问。
        proxy_pass http://app:8080;
        proxy_set_header Host $host:$server_port;
        proxy_set_header X-Forwarded-Host $server_name;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    location /static {
        access_log   off;
        expires      30d;

        alias /app/static;
    }
}
```

## docker 配置
#### Dockerfile
```
FROM maven:3.5-jdk-8
只有一句，依赖于基础镜像maven3.5和jdk 1.8。因为在docker-compose.yaml文件设置了项目启动命令，这里不需要再添加启动命令。
```