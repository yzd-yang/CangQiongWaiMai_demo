MySQL 首次启动会按文件名顺序执行 `/docker-entrypoint-initdb.d` 下的脚本。

本目录不放建表 SQL，以免与 `doc/升级/sql/` 重复。`docker-compose.yml` 把分库脚本挂载为：

| 容器内文件 | 来源 |
| ---------- | ---- |
| `01-sky_user.sql` | `doc/升级/sql/sky_user.sql` |
| `02-sky_product.sql` | `doc/升级/sql/sky_product.sql` |
| `03-sky_trade.sql` | `doc/升级/sql/sky_trade.sql`（含 `undo_log`） |
| `04-sky_pay.sql` | `doc/升级/sql/sky_pay.sql`（含 `undo_log`） |

只在 **数据卷为空** 时导入。若要重新初始化：`docker compose down -v` 后再次 `up`。
