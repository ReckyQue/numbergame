# README.md

## 项目简介

这是一个基于 Spring Boot 和 Nginx 的 Web 项目。玩家提交一个介于 1 到 100 之间的数字，系统会计算所有提交数字的平均值，并取其 2/3 作为目标数字（Winner Number）。最接近Winner Number的玩家获胜。

## 部署指南

### 本地部署

#### 1. 配置本地环境

- 安装 JDK17 或更高版本。
- 安装 Maven 3.6 或更高版本。
- 安装 Redis 服务。

#### 2. 下载项目文件

- 将项目代码克隆到本地：

  ```bash
  git clone https://github.com/ReckyQue/numbergame.git
  ```

- 注意项目结构：

  - 前端文件放置在 `src/main/resources/static` 目录下。
  - 后端文件放置在 `src/main/java` 目录下。

  ```
  [项目根目录]
  │
  ├── demo/                        # 项目代码目录
  │   ├── src/                     # 源代码目录
  │   │   ├── main/                # 主要代码目录
  │   │   │   ├── java/            # Java 源代码
  │   │   │   │   └── com/         # 包名
  │   │   │   │       └── monisuea/ # 域名
  │   │   │   │           └── numbergame/ # 项目名
  │   │   │   │               ├── NumberGameController.java  # 控制器层
  │   │   │   │               ├── MarkdownUtil.java          # 工具类
  │   │   │   │               ├── NumberGameService.java     # 服务层
  │   │   │   │               ├── NumbergameApplication.java # 主程序入口
  │   │   │   │               ├── NumberResponse.java        # 主程序入口
  │   │   │   │               ├── NumberRequest.java         # 主程序入口
  │   │   │   │               └── config/                    # 配置类
  │   │   │   │                   └── RedisConfig.java         # Redis 配置文件
  │   │   │   └── resources/      # 资源文件
  │   │   │       ├── application.properties # Spring Boot 配置文件
  │   │   │       └── static/     # 静态资源目录（前端文件）
  │   │   │           ├── index.html              # 主页面
  │   │   │           ├── player.html             # 玩家页面
  │   │   │           ├── host.html               # 主持人页面
  │   │   │           ├── A1.html                 # A1 页面
  │   │   │           ├── A2.html                 # A2 页面
  │   │   │           ├── A3.html                 # A3 页面
  │   │   │           ├── A4.html                 # A4 页面
  │   │   │           ├── README.html             # README 页面
  │   │   │           ├── markdown.html           # markdown 页面
  │   │   │           ├── script.js               # 主脚本文件
  │   │   │           ├── markdown/               # Markdown 文件目录
  │   │   │           │   ├── A1.md          # markdown文件
  │   │   │           │   ├── A2.md          # markdown文件
  │   │   │           │   ├── A3.md          # markdown文件
  │   │   │           │   ├── A4.md          # markdown文件
  │   │   │           │   ├── README.md      # markdown文件
  │   │   │           │   └── markdown.md    # markdown文件
  │   │   │           └── css/                    # CSS 文件目录
  │   │   │               ├── main.css               # 基础样式
  │   │   │               ├── markdown.css           # markdown 页面样式
  │   │   │               └── responsive.css         # 响应式样式
  │   └── pom.xml                 # Maven 配置文件
  └── README.md                   # 项目说明文件
  ```

#### 3. 启动服务

- 启动 Redis 服务：

  ```bash
  redis-server
  ```

- 启动 Redis 客户端服务：

  ```bash
  redis-cli
  ```

- 启动 Spring Boot 应用：

  ```bash
  cd [项目根目录]
  mvn spring-boot:run
  ```

- 打开浏览器，访问 `http://localhost:8080`，如果页面正常显示，则完成本地部署。

### 服务器部署

建议在本地部署成功后再进行服务器部署。

#### 1. 下载项目文件

- 将项目代码下载到本地：

  ```bash
  git clone https://github.com/ReckyQue/numbergame.git
  ```

- 项目结构同上

- 将前端文件上传到服务器的 `/www/wwwroot/yourweb` 目录下。

- 将 Spring Boot 项目在本地打包为 JAR 文件，并上传到服务器的 `/www/wwwroot/yourweb` 目录下：

  ```bash
  cd [/www/wwwroot/yourweb]
  mvn clean package
  scp target/your-application.jar [服务器用户名]@[服务器IP]:/path/to/your/directory
  ```

#### 2. 配置服务器环境

- 安装 JDK17 或更高版本。
- 安装 Maven 3.6 或更高版本。
- 安装 Redis 服务。
- 安装 Nginx 1.10 或更高版本。

#### 3. 启动服务

- 启动 Spring Boot 应用：

  ```bash
  java -jar /path/to/your/directory/your-application.jar
  ```

- 配置 Nginx 以代理请求到 Spring Boot 应用。编辑 Nginx 配置文件添加以下内容：

  ```nginx
  server {
      listen 80;
      server_name www.monisuea.top;
      index index.html index.htm default.htm default.html;
      root /www/wwwroot/1;
      #CERT-APPLY-CHECK--START
      # 用于SSL证书申请时的文件验证相关配置 -- 请勿删除
      include /www/server/panel/vhost/nginx/well-known/numbergame_jar.conf;
      #CERT-APPLY-CHECK--END
  
      # 添加重定向规则
      location = / {
          return 301 http://$host/index.html;
      }
  
      location / {
          proxy_pass http://127.0.0.1:8080;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header X-Forwarded-Proto $scheme;
      }
  
      #SSL-START SSL相关配置，请勿删除或修改下一行带注释的404规则
      #error_page 404/404.html;
      #SSL-END
  
      #REWRITE-START 伪静态相关配置
      include /www/server/panel/vhost/rewrite/java_numbergame_jar.conf;
      #REWRITE-END
  
      #禁止访问的文件或目录
      location ~ ^/(\.user.ini|\.htaccess|\.git|\.svn|\.project|LICENSE|package.json|package-lock.json|\.env) {
          return 404;
      }
  
      #一键申请SSL证书验证目录相关设置
      location /.well-known/ {
          root /www/wwwroot/java_node_ssl;
      }
  
      #禁止在证书验证目录放入敏感文件
      if ( $uri ~ "^/\.well-known/.*\.(php|jsp|py|js|css|lua|ts|go|zip|tar\.gz|rar|7z|sql|bak)$" ) {
          return 403;
      }
  
      #STATIC-START 静态资源相关配置
      
      #STATIC-END
  
      #PROXY-LOCAl-START 代理本地服务的相关配置
      
      #PROXY-LOCAl-END
  
      access_log  /www/wwwlogs/numbergame_jar.log;
      error_log  /www/wwwlogs/numbergame_jar.error.log;
  }
  ```

- 重新加载 Nginx 配置：

  ```bash
  sudo nginx -s reload
  ```

## 注意事项

- 确保服务器的防火墙和安全组允许访问 8080 端口。

- 确保域名已正确解析到服务器 IP。

- 由于项目没有配置 HTTPS，网站会面临一些安全风险。

  

由于时间仓促，作者水平也有限，存在诸多疏漏和错误，恳请各位玩家见谅并指出。



## 联系方式

- **项目维护者**：Recky
- **邮箱**：904751365@qq.com
- **GitHub**：[ReckyQue/numbergame](https://github.com/ReckyQue/numbergame)
