package com.sky.trade.service;

import com.sky.trade.dto.CategoryDTO;
import com.sky.trade.dto.CategoryPageQueryDTO;
import com.sky.trade.entity.Category;
import com.sky.trade.result.PageResult;
import java.util.List;

public interface CategoryService {

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    void save(CategoryDTO categoryDTO);
    /**
     * 根据id删除分类
     * @param id
     */
    void deleteById(Long id);
    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    void update(CategoryDTO categoryDTO);
    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    void startOrStop(Integer status, Long id);
    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
