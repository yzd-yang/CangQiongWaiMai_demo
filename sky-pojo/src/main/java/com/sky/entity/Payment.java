package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment implements Serializable {

    /** 支付方式 1微信 2支付宝 */
    public static final Integer WECHAT = 1;
    public static final Integer ALIPAY = 2;

    /** 支付状态 0未支付 1已支付 2退款 */
    public static final Integer UN_PAID = 0;
    public static final Integer PAID = 1;
    public static final Integer REFUND = 2;

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 订单号（关联 sky_trade.orders.number） */
    private String orderNumber;
    /** 订单主键（逻辑外键） */
    private Long orderId;
    private Long userId;
    private Integer payMethod;
    private Integer payStatus;
    private BigDecimal amount;
    /** 第三方交易号 */
    private String transactionId;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}