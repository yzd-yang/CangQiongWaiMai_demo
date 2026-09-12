package com.sky.controller.inner;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
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