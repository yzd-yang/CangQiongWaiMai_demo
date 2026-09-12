package com.sky.skyapi.client;


import com.sky.dto.OrdersPaymentDTO;
import com.sky.vo.OrderPaymentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sky-pay")  // 必须等于 spring.application.name
public interface PayClient {
    @PostMapping("/pay")
    OrderPaymentVO pay(@RequestBody OrdersPaymentDTO dto);
}