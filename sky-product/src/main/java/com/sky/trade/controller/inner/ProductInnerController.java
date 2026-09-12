package com.sky.trade.controller.inner;

import com.sky.trade.entity.Dish;
import com.sky.trade.entity.Setmeal;
import com.sky.trade.mapper.DishMapper;
import com.sky.trade.mapper.SetmealMapper;
import com.sky.trade.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner")
public class ProductInnerController {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @GetMapping("/dish/{id}")
    public Result<Dish> getDishById(@PathVariable Long id) {
        return Result.success(dishMapper.getById(id));
    }

    @GetMapping("/setmeal/{id}")
    public Result<Setmeal> getSetmealById(@PathVariable Long id) {
        return Result.success(setmealMapper.getById(id));
    }
}