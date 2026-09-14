package com.sky.mapper;
import com.sky.entity.Payment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

// Payment.java — 实体
// PaymentMapper.java
@Mapper
public interface PaymentMapper {
    @Insert("insert into payment(order_number, pay_method, pay_status, amount, pay_time, create_time, update_time) " +
            "values(#{orderNumber}, #{payMethod}, #{payStatus}, #{amount}, #{payTime}, #{createTime}, #{updateTime})")
    void insert(Payment payment);
}