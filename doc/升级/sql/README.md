# 分库建表脚本说明

对应 [阶段3.md](../阶段3.md) §3。源结构来自项目根目录 `sky.sql`。

| 文件 | 库名 | 表 |
| ---- | ---- | -- |
| [sky_user.sql](./sky_user.sql) | `sky_user` | `user`、`address_book`、`employee`（含 admin 初始数据） |
| [sky_product.sql](./sky_product.sql) | `sky_product` | `category`、`dish`、`dish_flavor`、`setmeal`、`setmeal_dish`（含分类/菜品/口味初始数据） |
| [sky_trade.sql](./sky_trade.sql) | `sky_trade` | `shopping_cart`、`orders`、`order_detail` |
| [sky_pay.sql](./sky_pay.sql) | `sky_pay` | `payment`（可选；mock 支付可跳过） |

## 导入示例

```bash
mysql -h192.168.6.128 -uroot -p123 < doc/升级/sql/sky_user.sql
mysql -h192.168.6.128 -uroot -p123 < doc/升级/sql/sky_product.sql
mysql -h192.168.6.128 -uroot -p123 < doc/升级/sql/sky_trade.sql
# 可选
mysql -h192.168.6.128 -uroot -p123 < doc/升级/sql/sky_pay.sql
```

旧库 `sky_take_out` 可继续保留作对照，勿直接删。
