package com.sky.service;


import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.enumeration.OperationType;

/**
 * 菜品管理
 */
public interface DishService {
    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);


}
