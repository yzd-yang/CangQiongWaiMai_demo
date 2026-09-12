package com.sky.trade.mapper;

import com.github.pagehelper.Page;
import com.sky.trade.annotation.AutoFill;
import com.sky.trade.dto.DishPageQueryDTO;
import com.sky.trade.entity.Dish;
import com.sky.trade.enumeration.OperationType;
import com.sky.trade.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);


    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);


    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 根据主键查询菜品
     *
     * @param id
     * @return
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    /**
     * 菜品删除
     *
     * @param id
     * @return
     */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量删除
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id动态修改菜品数据
     *
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 用SetmealId联查dish
     * @param id
     * @return
     */
    @Select("SELECT d.* FROM dish as d JOIN  setmeal_dish as s on d.id=s.dish_id where s.setmeal_id=#{id}")
    List<Dish> getBySetmealId(Long id);
    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}

