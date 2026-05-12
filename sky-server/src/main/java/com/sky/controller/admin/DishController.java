package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 *
 */
@Api(tags = "菜品管理接口")
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
    @ApiOperation("新增菜品")
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
    @ApiOperation("菜品管理分页查询")
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
    @ApiOperation("菜品删除")
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
    @ApiOperation("id查询菜品")
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
    @ApiOperation("修改菜品")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品，参数：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //清理缓存,将所有菜品的缓存数据清理掉
        cleanCache("dish_*");

        return Result.success();
    }
    @ApiOperation("根据分类di查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("根据分类id查询菜品，参数：{}",categoryId);
        List<Dish> list = dishService.list(categoryId);

        return Result.success(list);
    }

    @ApiOperation("起售停售")
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
