package com.xinbo.cloud.service.admin.statistics.controller;

import com.xinbo.cloud.common.service.statistics.SportActiveUserStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportBetTypeStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportMerchantStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportUserStatisticsService;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.statistics.SportActiveUserStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportBetTypeStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportMerchantStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportUserStatisticsSearchVo;
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
 * @desc 体育相关报表
 */
@RestController
@RequestMapping("sportStatistics")
public class SportStatisticsController {

    @Autowired
    @Qualifier("sportActiveUserStatisticsServiceImpl")
    private SportActiveUserStatisticsService sportActiveUserService;

    @Autowired
    @Qualifier("sportBetTypeStatisticsServiceImpl")
    private SportBetTypeStatisticsService sportBetTypeService;

    @Autowired
    @Qualifier("sportMerchantStatisticsServiceImpl")
    private SportMerchantStatisticsService sportMerchantService;

    @Autowired
    @Qualifier("sportUserStatisticsServiceImpl")
    private SportUserStatisticsService sportUserService;

    @ApiOperation(value = "分页获取体育活跃用户登录报表数据", notes = "")
    @PostMapping("getActiveUserStatisticsPage")
    public ActionResult getActiveUserStatisticsPage(@RequestBody PageVo<SportActiveUserStatisticsSearchVo> pageVo) {
        return ResultFactory.success(sportActiveUserService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取体育投注类型报表数据", notes = "")
    @PostMapping("getBetTypeStatisticsPage")
    public ActionResult getBetTypeStatisticsPage(@RequestBody PageVo<SportBetTypeStatisticsSearchVo> pageVo) {
        return ResultFactory.success(sportBetTypeService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取体育商户报表数据", notes = "")
    @PostMapping("getMerchantStatisticsPage")
    public ActionResult getMerchantStatisticsPage(@RequestBody PageVo<SportMerchantStatisticsSearchVo> pageVo) {
        return ResultFactory.success(sportMerchantService.getListPage(pageVo));
    }

    @ApiOperation(value = "分页获取体育用户报表数据", notes = "")
    @PostMapping("getUserStatisticsPage")
    public ActionResult getUserStatisticsPage(@RequestBody PageVo<SportUserStatisticsSearchVo> pageVo) {
        return ResultFactory.success(sportUserService.getListPage(pageVo));
    }
}
