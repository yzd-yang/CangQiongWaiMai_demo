package com.sky.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.entity.Payment;
import com.sky.mapper.PaymentMapper;
import com.sky.service.PayService;
import com.sky.skyapi.client.OrderClient;
import com.sky.vo.OrderPaymentVO;
import lombok.RequiredArgsConstructor;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;

    @GlobalTransactional(rollbackFor = Exception.class)
    public OrderPaymentVO pay(OrdersPaymentDTO dto) {
        // ① 本库：写 payment
        Payment payment = new Payment();
        payment.setOrderNumber(dto.getOrderNumber());
        payment.setPayMethod(dto.getPayMethod() != null ? dto.getPayMethod() : 1);
        payment.setPayStatus(1);
        payment.setAmount(new BigDecimal("0.01")); // 学习写死
        payment.setPayTime(LocalDateTime.now());
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());
        paymentMapper.insert(payment);

        // ② 远程：改订单
        orderClient.paySuccess(dto.getOrderNumber());

        // ③ mock 给前端（保持现状即可）
        JSONObject mock = new JSONObject();
        mock.put("code", "ORDERPAID");
        mock.put("package", "prepay_id=mock_prepay_id_123456");
        OrderPaymentVO vo = mock.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(mock.getString("package"));
        return vo;
    }

}