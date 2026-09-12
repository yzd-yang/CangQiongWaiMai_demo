package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Tag(name = "店铺相关接口")
@Slf4j
public class ShopController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置店铺的营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @Operation(summary = "修改店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态：{}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set("SHOP_STATUS",String.valueOf(status));
        return Result.success();
    }

    /**
     * 获取店铺的营业状态
     * @return
     */
    @Operation(summary = "获取店铺的营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus(){
        Integer status = Integer.valueOf(redisTemplate.opsForValue().get("SHOP_STATUS"));
        //设置缓存
        redisTemplate.opsForValue().set("SHOP_STATUS",String.valueOf(status));

        log.info("获取店铺营业状态为:{}",status==1?"营业中":"打烊中");

        return Result.success(status);
    }

}
