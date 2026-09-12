package com.sky.controller;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.skyapi.client.OrderClient;
import com.sky.vo.OrderPaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PayController {
    private final OrderClient orderClient;

    @PostMapping("/pay")
    public OrderPaymentVO pay(@RequestBody OrdersPaymentDTO dto) {
        JSONObject mock = new JSONObject();
        mock.put("code", "ORDERPAID");
        mock.put("package", "prepay_id=mock_prepay_id_123456");
        OrderPaymentVO vo = mock.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(mock.getString("package"));

        orderClient.paySuccess(dto.getOrderNumber());  // 让 trade 改库+WS
        return vo;
    }
}