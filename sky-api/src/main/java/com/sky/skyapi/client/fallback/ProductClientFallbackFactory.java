package com.sky.skyapi.client.fallback;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.skyapi.client.ProductClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public Dish getDishById(Long id) {
                log.error("查询菜品降级, id={}, cause={}", id, cause.toString());
                // 学习项目二选一：
                // A) 返回 null，并在 ShoppingCartServiceImpl 判空抛业务异常
                // B) 直接抛业务异常（需 sky-common 里已有异常类）
                return null;
            }

            @Override
            public Setmeal getSetmealById(Long id) {
                log.error("查询套餐降级, id={}, cause={}", id, cause.toString());
                return null;
            }

            @Override
            public Integer countDish(Map<String, Object> map) {
                log.error("统计菜品降级, cause={}", cause.toString());
                return 0;
            }

            @Override
            public Integer countSetmeal(Map<String, Object> map) {
                log.error("统计套餐降级, cause={}", cause.toString());
                return 0;
            }
        };
    }
}