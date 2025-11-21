AI Voice News

简介

- 语音新闻应用，后端使用 Spring Boot 提供用户与鉴权服务；管理端采用 React，App 采用 Flutter。
- 当前仓库已包含后端模块 `back/`，其余模块可后续补充。

技术栈

- 后端：Java 17（Spring Boot 3.5.x）、Spring Security、JPA、Redis、Swagger（springdoc）
- 数据库：开发环境 H2（内存库，MySQL 兼容模式）、生产环境 MySQL
- 前端：React（管理端，待建设）、Flutter（App 端，待建设）

快速开始（后端）

- 开发环境（dev）
  - 进入后端目录：`cd back`
  - 启动：`./mvnw.cmd spring-boot:run`
  - 端口：`http://localhost:8083/`
  - H2 控制台：`http://localhost:8083/h2-console`（JDBC URL：`jdbc:h2:mem:newsdb`，用户名 `sa`，密码空）
  - Swagger UI：`http://localhost:8083/swagger-ui/index.html`

- 生产环境（prod）
  - 构建：`./mvnw.cmd -DskipTests package`
  - 运行 Jar：`java -jar .\target\app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
  - 如端口占用，可覆盖端口：`--server.port=8085`

配置说明

- Profile 默认在 `back/src/main/resources/application.properties` 中设置为 `dev`
- 开发环境配置：`back/src/main/resources/application-dev.properties`
  - 已启用 H2 内存库、端口 `8083`、并开启 H2 控制台
- 生产环境配置：`back/src/main/resources/application-prod.properties`
  - MySQL 连接示例：
    - `spring.datasource.url=jdbc:mysql://<host>:<port>/<db>?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false`
    - `spring.datasource.username=<user>`
    - `spring.datasource.password=<password>`
  - 端口默认 `8080`，可通过启动参数覆盖
- JWT 配置键（dev/prod 分别在各自的 `application-*.properties` 中）：
  - `jwt.secret`、`jwt.issuer`、`jwt.access-token-expire-seconds`、`jwt.refresh-token-expire-seconds`
- 刷新/登出依赖 Redis（`StringRedisTemplate`），请在生产环境提供 Redis 服务并配置连接参数（默认 `localhost:6379`）。

API 速览（后端）

- 注册：`POST /api/auth/register`
  - Body 示例：
    ```json
    { "username": "alice", "password": "pass123", "email": "alice@example.com", "phone": "13800000001" }
    ```
- 登录：`POST /api/auth/login`
  - Body 示例：
    ```json
    { "username": "alice", "password": "pass123" }
    ```
  - 响应：`accessToken` 与 `refreshToken`
- 刷新：`POST /api/auth/refresh`（需传 `refreshToken`）
- 登出：`POST /api/auth/logout`（撤销传入的 `refreshToken`）
- 用户列表（受保护）：`GET /api/users`（需在请求头添加 `Authorization: Bearer <accessToken>`）

目录说明（当前）

- `back/`：Spring Boot 后端
  - `src/main/java/com/voice/news/app/controller/AuthController.java` 登录与注册接口
  - `src/main/java/com/voice/news/app/security/JwtUtil.java` JWT 生成与解析
  - `src/main/java/com/voice/news/app/security/TokenService.java` 刷新令牌与撤销逻辑（依赖 Redis）
  - `src/main/java/com/voice/news/app/controller/UserController.java` 用户 CRUD 接口
  - `src/main/resources/application-*.properties` 环境配置

后续规划

- 管理端（React）与 App（Flutter）目录初始化与联调
- CI/CD、日志与监控、容器化与部署脚本等