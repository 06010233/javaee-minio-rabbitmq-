# 机票实名销售系统 (Flight Ticket System)

本项目是一个前后端分离的机票实名销售系统，包含了完整的 Docker 容器化运行环境。
由于本项目是本人第一次用docker和git传代码，可能会有一些错误纰漏等，请见谅。

如果有报错等，可能是nacos，minio，rabbitmq，数据库等有问题，由于第一次写这个项目，比较青涩。
在orderservice.java中，有两个ip地址，我这边设置的是，只有在同一个网段下才能扫码，没有加太多组件，不好意思
这里的ip地址都是电脑自己的ip地址，请配置好。

## 项目结构
- `src/`: Spring Boot 后端源码
- `flight-frontend/`: Vue.js 前端源码
- `docker/`: 中间件环境配置 (MySQL, Nacos, MinIO, RabbitMQ)
- `docker/sql/init.sql`: 初始数据库结构及航班测试数据



环境准备
确保电脑已安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)。

启动中间件
进入 `docker` 目录，在命令行执行：
```bash
docker-compose up -d
MySQL: 端口 3306，密码 zc050601
Nacos: 端口 8848，控制台 http://localhost:8848/nacos
MinIO: 端口 9000 (API), 9001 (Console)
RabbitMQ: 端口 5672
3. 后端运行
在 IDEA 中直接运行 FlightTicketSystemApplication.java。
4. 前端运行
进入 flight-frontend 目录：
code
Bash
npm install
npm run dev
注意事项
关于视频文件: 登录页背景视频 bg.mp4 因超过 GitHub 100MB 限制未上传。
视频文件我放在c盘726宗宸文件夹下面的video中了，或许不止一个视频没有传到git上，请见谅。

配置同步: 如果首次运行，请确保在 Nacos 或 application.yml 中配置好数据库连接。
