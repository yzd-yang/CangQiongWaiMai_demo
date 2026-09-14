package com.sky.skyapi.client;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.skyapi.client.fallback.ProductClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author sky
 * @description: 产品微服务的客户端
 *
 **/
@FeignClient(
        name = "sky-product",
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {
    @GetMapping("/inner/dish/{id}")
    Dish getDishById(@PathVariable Long id);

    @GetMapping("/inner/setmeal/{id}")
    Setmeal getSetmealById(@PathVariable Long id);

    @GetMapping("/inner/dish/count")
    Integer countDish(@RequestParam Map<String, Object> map);

    @GetMapping("/inner/setmeal/count")
    Integer countSetmeal(@RequestParam Map<String, Object> map);


}

