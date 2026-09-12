package com.sky.controller.inner;


import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inner/order")
@RequiredArgsConstructor
public class OrderInnerController {
    private final OrderService orderService; // 或直接调你抽出来的方法
    private final OrderMapper orderMapper;
    @PutMapping("/paySuccess/{orderNumber}")
    public void paySuccess(@PathVariable String orderNumber) {
        orderService.paySuccess(orderNumber);
    }
    @GetMapping("/sumAmount")
    public Double sumAmount(
            @RequestParam Map<String, Object> map) {
        Double sum = orderMapper.sumByMap(map);
        return sum == null ? 0.0 : sum;
    }
    @GetMapping("/count")
    public Integer count(
            @RequestParam Map<String, Object> map) {
        return orderMapper.countByMap(map);
    }
    @GetMapping("/top10")
    public List<GoodsSalesDTO> getSalesTop10(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return orderMapper.getSalesTop10(beginTime, endTime);
    }

}
