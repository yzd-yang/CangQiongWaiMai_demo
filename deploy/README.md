# deploy

基础设施与可选的业务容器编排。完整启动顺序见仓库根目录 [README.md](../README.md) 和 [阶段6.md](../doc/升级/阶段6.md)。

```bash
cp .env.example .env
docker compose up -d                 # MySQL Redis Nacos Sentinel Seata
docker compose --profile nginx up -d # 可选：80 → 宿主机网关 8080
docker compose --profile apps up -d --build  # 可选：全部微服务（先 mvn package）
```
