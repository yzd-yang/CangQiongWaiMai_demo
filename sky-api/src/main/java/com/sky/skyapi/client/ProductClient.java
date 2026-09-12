package com.sky.skyapi.client;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
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

