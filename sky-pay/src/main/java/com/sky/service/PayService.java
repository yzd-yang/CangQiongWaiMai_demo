package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.vo.OrderPaymentVO;

public interface PayService {
    OrderPaymentVO pay(OrdersPaymentDTO dto);
}
