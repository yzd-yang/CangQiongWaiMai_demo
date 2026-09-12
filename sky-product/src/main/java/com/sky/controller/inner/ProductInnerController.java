package com.sky.controller.inner;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    @GetMapping("/dish/count")
    public Integer countDish(@RequestParam Map<String, Object> map) {
        return dishMapper.countByMap(map);
    }

    @GetMapping("/setmeal/count")
    public Integer countSetmeal(@RequestParam Map<String, Object> map) {

        return setmealMapper.countByMap(map);
    }
}