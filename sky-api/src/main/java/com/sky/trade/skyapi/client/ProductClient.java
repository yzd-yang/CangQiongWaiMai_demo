package com.sky.trade.skyapi.client;

import com.sky.trade.entity.Dish;
import com.sky.trade.entity.Setmeal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sky-product")
public interface ProductClient {
    @GetMapping("/inner/dish/{id}")
    Dish getDishById(@PathVariable Long id);

    @GetMapping("/inner/setmeal/{id}")
    Setmeal getSetmealById(@PathVariable Long id);
}

