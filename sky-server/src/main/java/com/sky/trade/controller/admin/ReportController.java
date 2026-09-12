package com.sky.trade.controller.admin;

import com.sky.trade.result.Result;
import com.sky.trade.service.ReportService;
import com.sky.trade.vo.OrderReportVO;
import com.sky.trade.vo.SalesTop10ReportVO;
import com.sky.trade.vo.TurnoverReportVO;
import com.sky.trade.vo.UserReportVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * 报表
 */
@Slf4j
@RestController
@Tag(name = "管理端报表接口")
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    /**
     * 营业额数据统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额数据统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
         TurnoverReportVO vo =reportService.getTurnover(begin, end);
        return Result.success(vo);
    }
    /**
     * 用户数据统计,
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @Operation(summary = "用户数据统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern ="yyyy-MM-dd")LocalDate begin,
                                               @DateTimeFormat(pattern ="yyyy-MM-dd")LocalDate end){
        log.info("开始查询用户数据：{}到{}",begin,end);
        return Result.success(reportService.getUserStatistics(begin,end));
    }
    /**
     * 订单数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单数据统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern ="yyyy-MM-dd")LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate  end){

        return Result.success(reportService.getOrderStatistics(begin,end));
    }

    /**
     * 销量排名统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @Operation(summary = "销量排名统计")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
                        ){

        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 导出运营数据报表
     * @param response
     */
    @GetMapping("/export")
    @Operation(summary = "导出运营数据报表")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);
    }
}
