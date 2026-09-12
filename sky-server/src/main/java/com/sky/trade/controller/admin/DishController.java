package com.sky.trade.controller.admin;

import com.sky.trade.dto.DishDTO;
import com.sky.trade.dto.DishPageQueryDTO;
import com.sky.trade.entity.Dish;
import com.sky.trade.result.PageResult;
import com.sky.trade.result.Result;
import com.sky.trade.service.DishService;
import com.sky.trade.vo.DishVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 *
 */
@Tag(name = "菜品管理接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品，参数：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);

        //清理缓存
         cleanCache("dish_"+dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 菜品管理分页查询
     */
    @Operation(summary = "菜品管理分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品管理分页查询，参数：{}",dishPageQueryDTO);
        PageResult pageResult = dishService. pageQuery(dishPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Operation(summary = "菜品删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品删除，参数：{}", ids);
        dishService.deleteBatch(ids);

        //清理缓存,将所有菜品的缓存数据清理掉
        cleanCache("dish_*");


        return Result.success();
    }
    /**
     * id查询菜品
     */
    @Operation(summary = "id查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("id查询菜品，参数：{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);

        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     *
     * @param dishDTO
     * @return
     */
    @Operation(summary = "修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，参数：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //清理缓存,将所有菜品的缓存数据清理掉
        cleanCache("dish_*");

        return Result.success();
    }
    @Operation(summary = "根据分类di查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类id查询菜品，参数：{}",categoryId);
        List<Dish> list = dishService.list(categoryId);

        return Result.success(list);
    }

    @Operation(summary = "起售停售")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){

        //清理缓存,将所有菜品的缓存数据清理掉
        cleanCache("dish_*");

        log.info("起售停售，参数：{}",status,id);
        dishService.startOrStop(status,id);

        return Result.success();
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = stringRedisTemplate.keys(pattern);
        stringRedisTemplate.delete(keys);
    }

}
