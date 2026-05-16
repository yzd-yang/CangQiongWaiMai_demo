package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
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
import java.util.stream.Collectors;

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
     * 统计订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 创建日期集合
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //每天订单总数集合
        ArrayList<Integer> orderCountList = new ArrayList<>();
        //每天有效订单数集合
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        dateList.add( begin);
        while (!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        for (LocalDate localDate : dateList) {
            //查询每天的总订单数 select count(id) from orders where order_time > ? and order_time < ?
            LocalDateTime beDateTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime enDateTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer orderCount =getOrderCount(beDateTime, enDateTime,null);
            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = ?
            Integer validOrderCount = getOrderCount(beDateTime, enDateTime, Orders.COMPLETED);
            //封装数据
            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //时间区间内的总有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率 = 有效订单数 / 总订单数(不等于0)
        double orderCompletionRate =0.0;
        if(totalOrderCount != 0){
            orderCompletionRate=  validOrderCount / (double) totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList))
                .orderCountList(StringUtils.join(orderCountList))
                .validOrderCountList(StringUtils.join(validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build()
                ;
    }

    /**
     * 根据时间区间统计指定状态的订单数量
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime beginTime, LocalDateTime endTime, Integer status){
        Map map = new HashMap<>();
        map .put("begin",beginTime);
        map.put("end",endTime);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /**
     * 统计销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //SELECT od.name ,SUM(od.number) number FROM order_detail od,orders o
        // WHERE od.order_id=o.id
        // and o.order_time > '2026-5-10'
        // and order_time < '2026-5-17'
        // and o.`status`=5
        // GROUP BY od.`name`
        // ORDER BY number desc
        // LIMIT 10
        
        ArrayList<GoodsSalesDTO> salesTop10=orderMapper.getSalesTop10(beginTime,endTime);
        //名称列表
        List<String> collect = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(collect, ",");

        List<Integer> collect1 = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(collect1, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build()
                ;
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
    /**
     * 根据时间区间统计用户数量
     * @param beginTime
     * @param endTime
     * @return
     */
    private Integer getUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        Map map = new HashMap();
        map.put("begin",beginTime);
        map.put("end", endTime);
        return userMapper.countByMap(map);
    }
}
