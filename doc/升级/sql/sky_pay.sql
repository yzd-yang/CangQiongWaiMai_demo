-- sky-pay 服务库（可选）：支付流水
-- 阶段 3 若先 mock 支付，可不执行本脚本

CREATE DATABASE IF NOT EXISTS `sky_pay` DEFAULT CHARACTER SET utf8mb4;
USE `sky_pay`;

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_number` varchar(50) NOT NULL COMMENT '订单号（关联 sky_trade.orders.number）',
  `order_id` bigint DEFAULT NULL COMMENT '订单主键（逻辑外键）',
  `user_id` bigint DEFAULT NULL COMMENT '用户id（逻辑外键）',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1微信,2支付宝',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1已支付 2退款',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_number` (`order_number`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付流水';

-- Seata AT 回滚日志（阶段 5 方案 A）
CREATE TABLE IF NOT EXISTS `undo_log` (
  `branch_id`     BIGINT       NOT NULL,
  `xid`           VARCHAR(128) NOT NULL,
  `context`       VARCHAR(128) NOT NULL,
  `rollback_info` LONGBLOB     NOT NULL,
  `log_status`    INT          NOT NULL,
  `log_created`   DATETIME(6)  NOT NULL,
  `log_modified`  DATETIME(6)  NOT NULL,
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
