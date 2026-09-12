package com.sky.skyapi.client;

import com.sky.dto.GoodsSalesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FeignClient(name = "sky-trade")
public interface OrderClient {
    @PutMapping("/inner/order/paySuccess/{orderNumber}")
    void paySuccess(@PathVariable String orderNumber);

    @GetMapping("/inner/order/count")
    Integer count(
            @RequestParam Map<String, Object> status);
    @GetMapping("/inner/order/sumAmount")
    Double sumAmount(
            @RequestParam Map<String, Object> status);
    @GetMapping("/inner/order/top10")
    ArrayList<GoodsSalesDTO> getSalesTop10(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) ;

}
