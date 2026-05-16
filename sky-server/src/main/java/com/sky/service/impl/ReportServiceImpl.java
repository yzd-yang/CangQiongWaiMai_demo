package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 根据时间区间统计营业额
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public TurnoverReportVO getTurnover(LocalDate beginTime, LocalDate endTime) {
        // 创建日期集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(beginTime);
        while (!beginTime.equals(endTime)){
            beginTime = beginTime.plusDays(1);
            dateList.add(beginTime);
        }
        ArrayList<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            // 查询指定日期的营业额
            LocalDateTime bLocalDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime eLocalDateTime = LocalDateTime.of(localDate, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin",bLocalDateTime);
            map.put("end",eLocalDateTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add( turnover);
        }

        // 创建营业额集合
        String join = StringUtils.join(dateList, ",");
        String join1 = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList( join)
                .turnoverList( join1)
                .build();
    }

    /**
     * 统计用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> totalUserList = new ArrayList<>();//总用户集合
        List<Integer> newUserList = new ArrayList<>();//新增用户集合

        for (LocalDate localDate : dateList) {
           LocalDateTime beginDate=LocalDateTime.of(localDate,LocalTime.MIN);
           LocalDateTime endDate=LocalDateTime.of(localDate,LocalTime.MAX);

            //新增用户数量 select count(id) from user where create_time > ? and create_time < ?
            Integer newUserCount = getUserCount(beginDate, endDate);
           //总用户数量 select count(id) from user where create_time < ?
            Integer totalUserCount = getUserCount(null, endDate);

            //封装数据
            totalUserList.add(totalUserCount);
            newUserList.add(newUserCount);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }
}
