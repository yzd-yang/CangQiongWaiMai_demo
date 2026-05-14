package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {
    /**
     *
     * 保存订单数据
     * @param order
     */
    void insert(Orders order);

    /**
     *
     * @param orderStatus
     * @param orderPaidStatus
     * @param checkoutTime
     * @param currentOrderId
     */
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkoutTime, String currentOrderId);
}
