package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 根据条件查询订单数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

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
    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);
    /**
     * 修改订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status=#{status}")
    Integer countStatus(Integer status);

    /**
     *  查询超时订单
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status,LocalDateTime orderTime);
}

