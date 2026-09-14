# 苍穹外卖（sky-take-out）

Spring Boot **3.4.3** + JDK **21** 微服务练习项目：网关统一入口、Nacos 注册、OpenFeign、Sentinel、Seata（支付改单）。

升级与拆分过程见 [doc/升级/开发方案.md](doc/升级/开发方案.md)。阶段清单：[0](doc/升级/阶段0.md) → [1](doc/升级/阶段1.md) → [2](doc/升级/阶段2.md) → [3](doc/升级/阶段3.md) → [4](doc/升级/阶段4.md) → [5](doc/升级/阶段5.md) → [6](doc/升级/阶段6.md)。

## 架构与端口

```text
管理端 / 小程序 / Knife4j
        │  只访问网关（或 Nginx → 网关）
        ▼
 sky-gateway :8080
        ├─ /admin/employee/**  /user/user/**  /user/addressBook/**  → sky-user    :8081
        ├─ /admin|user /category|dish|setmeal|shop/**               → sky-product :8082
        ├─ /user/shoppingCart/**  /admin|user /order/**  /ws/**     → sky-trade   :8083
        ├─ /admin/workspace/**  /admin/report/**                    → sky-report  :8084
        ├─ /pay/**                                                  → sky-pay     :8085
        └─ /admin/common/upload                                     → sky-file    :8086
```

| 基础设施 | 端口 | 默认账号 |
| -------- | ---- | -------- |
| MySQL | 3306 | root / 123（库：`sky_user` `sky_product` `sky_trade` `sky_pay`） |
| Redis | 6379 | 无密码 |
| Nacos | 8848（gRPC 9848） | nacos / nacos |
| Sentinel Dashboard | 8090 | sentinel / sentinel |
| Seata TC | 8091 | file 模式 |

管理端初始账号：`admin` / `123456`。

## 环境要求

- JDK 21、Maven 3.8+
- Docker Compose v2（推荐用来起中间件）
- 或已有可达的 MySQL / Redis / Nacos（当前 `application-local.yaml` 默认主机 `192.168.6.128`）

## 启动顺序

**先基础设施，再业务服务，最后网关。前端只打网关。**

### 1. 基础设施

```bash
cd deploy
cp .env.example .env    # 按本机修改 SEATA_IP（IDE 跑业务时填宿主机 IP，不要填 seata）
docker compose up -d
```

等到 Nacos 控制台 `http://<主机>:8848/nacos` 能打开（约 30～60 秒）。

若 Compose 跑在本机而 yaml 仍写 `192.168.6.128`，把各模块 `application-local.yaml` 的数据源 / Redis host 改成 `127.0.0.1`，`.env` 里 `SEATA_IP=127.0.0.1`。

已有中间件时不要重复抢端口，可只启动缺的服务，例如：`docker compose up -d nacos sentinel seata`。

分库脚本说明：[doc/升级/sql/README.md](doc/升级/sql/README.md)。

### 2. 编译

```bash
mvn clean install -DskipTests
```

### 3. 业务服务（IDE 或命令行）

建议顺序：

1. `sky-user`（8081）
2. `sky-product`（8082）
3. `sky-file`（8086）
4. `sky-trade`（8083）
5. `sky-pay`（8085）
6. `sky-report`（8084）

启动类：`UserApplication` / `ProductApplication` / `FileApplication` / `TradeApplication` / `PayApplication` / `ReportApplication`。

Nacos 里应陆续出现上述实例。

### 4. 网关

最后启动 `sky-gateway`（8080，`GatewayApplication`）。

### 5. 前端

管理端、小程序、Postman、Knife4j **只配网关**：`http://<主机>:8080`。

- 管理端 Header：`token`
- 用户端 Header：`authentication`
- 接口文档：各服务自己的 `/doc.html`，联调主路径走网关

可选：Nginx 把 80 反代到网关。

```bash
cd deploy
docker compose --profile nginx up -d nginx
```

配置说明：[frontend/前端页面.md](frontend/前端页面.md)、[deploy/nginx](deploy/nginx)。

## 可选：业务服务也容器化

```bash
mvn -pl sky-user,sky-product,sky-trade,sky-report,sky-pay,sky-file,sky-gateway -am package -DskipTests
# deploy/.env 中 SEATA_IP=seata
cd deploy
docker compose --profile apps up -d --build
```

不要和 IDE 里已占用的 8080～8086 混跑，也不要同时加 `--profile nginx`（会和容器内 Nginx 抢 80 端口）。细节见 [doc/升级/阶段6.md](doc/升级/阶段6.md)。

## 冒烟

1. `POST http://localhost:8080/admin/employee/login`  body：`{"username":"admin","password":"123456"}`
2. 后续管理端请求带 Header `token: <jwt>`
3. 用户端浏览 / 加购 / 下单 / 支付走 `/user/**`，Header 为 `authentication`
4. 来单提醒 WebSocket：`ws://localhost:8080/ws/{sid}`

## 模块

| 模块 | 职责 |
| ---- | ---- |
| `sky-common` / `sky-pojo` / `sky-api` | 公共能力、模型、Feign 契约 |
| `sky-gateway` | 路由 + JWT 鉴权 + 传 `user-info` |
| `sky-user` | 员工、C 端用户、地址簿 |
| `sky-product` | 分类 / 菜品 / 套餐 / 店铺状态 |
| `sky-trade` | 购物车、订单、WebSocket、定时关单 |
| `sky-report` | 工作台与报表（Feign 聚合） |
| `sky-pay` | 支付流水 + Seata 改订单状态 |
| `sky-file` | 文件上传 |
| `sky-server` | 历史单体，对照用，不要再作为入口 |

更完整的业务说明：[doc/项目文档.md](doc/项目文档.md)。
