package com.sky.trade.skyuser.service;

import com.sky.trade.dto.EmployeeDTO;
import com.sky.trade.dto.EmployeeLoginDTO;
import com.sky.trade.dto.EmployeePageQueryDTO;
import com.sky.trade.entity.Employee;
import com.sky.trade.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);
    /**
     * 新增员工
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);
    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    /**
     * 启用禁用员工账号
     * @return
     * @param status
     * @param  id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    void update(EmployeeDTO employeeDTO);
}
