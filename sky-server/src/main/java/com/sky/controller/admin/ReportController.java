package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 报表
 */
@Slf4j
@RestController
@Api("管理端报表接口")
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
    @ApiOperation("营业额数据统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
         TurnoverReportVO vo =reportService.getTurnover(begin, end);
        return Result.success(vo);
    }
    /**
     * 用户数据统计
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户数据统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern ="yyyy-MM-dd")LocalDate begin,
                                               @DateTimeFormat(pattern ="yyyy-MM-dd")LocalDate end){
        log.info("开始查询用户数据：{}到{}",begin,end);
        return Result.success(reportService.getUserStatistics(begin,end));
    }
}
