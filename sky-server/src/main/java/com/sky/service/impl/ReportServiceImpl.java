package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;
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
     * 导出营业数据
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);
        BusinessDataVO businessDataVo = workspaceService.getBusinessData(beginTime, endTime);

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //填充数据时间
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue(dateBegin + "至" + dateEnd);
            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVo.getTurnover());
            row.getCell(4).setCellValue(businessDataVo.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVo.getNewUsers());
            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVo.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVo.getUnitPrice());

            //准备明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row=sheet.getRow(7+i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

          //通过输出流将文件下载到客户端浏览器中
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.flush();
            out.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
