package com.xinbo.cloud.service.admin.statistics.controller;

import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.service.statistics.LotteryActiveUserStatisticsService;
import com.xinbo.cloud.common.service.statistics.LotteryTypeStatisticsService;
import com.xinbo.cloud.common.service.statistics.LotteryMerchantStatisticsService;
import com.xinbo.cloud.common.service.statistics.LotteryUserStatisticsService;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.statistics.LotteryActiveUserStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.LotteryTypeStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.LotteryMerchantStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.LotteryUserStatisticsSearchVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 汉斯
 * @date 2020/4/1 12:22
 * @desc 彩票相关报表
 */
@RestController
@RequestMapping("lotteryStatistics")
public class LotteryStatisticsController {

    @Autowired
    @Qualifier("lotteryActiveUserStatisticsServiceImpl")
    private LotteryActiveUserStatisticsService lotteryActiveUserService;

    @Autowired
    @Qualifier("lotteryTypeStatisticsServiceImpl")
    private LotteryTypeStatisticsService lotteryTypeStatisticsService;

    @Autowired
    @Qualifier("lotteryMerchantStatisticsServiceImpl")
    private LotteryMerchantStatisticsService lotteryMerchantService;

    @Autowired
    @Qualifier("lotteryUserStatisticsServiceImpl")
    private LotteryUserStatisticsService lotteryUserService;

    @ApiOperation(value = "分页获取彩票活跃用户登录报表数据", notes = "")
    @PostMapping("getActiveUserStatisticsPage")
    public ActionResult getActiveUserStatisticsPage(@RequestBody PageVo<LotteryActiveUserStatisticsSearchVo> pageVo) {
        return ResultFactory.success(lotteryActiveUserService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取彩种型报表数据", notes = "")
    @PostMapping("getBetTypeStatisticsPage")
    public ActionResult getBetTypeStatisticsPage(@RequestBody PageVo<LotteryTypeStatisticsSearchVo> pageVo) {
        return ResultFactory.success(lotteryTypeStatisticsService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取彩票商户报表数据", notes = "")
    @PostMapping("getMerchantStatisticsPage")
    public ActionResult getMerchantStatisticsPage(@RequestBody PageVo<LotteryMerchantStatisticsSearchVo> pageVo) {
        return ResultFactory.success(lotteryMerchantService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取彩票用户报表数据", notes = "")
    @PostMapping("getUserStatisticsPage")
    public ActionResult getUserStatisticsPage(@RequestBody PageVo<LotteryUserStatisticsSearchVo> pageVo) {
        return ResultFactory.success(lotteryUserService.getListPage(pageVo));
    }
}
