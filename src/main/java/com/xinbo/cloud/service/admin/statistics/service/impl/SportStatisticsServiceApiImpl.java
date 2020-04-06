package com.xinbo.cloud.service.admin.statistics.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xinbo.cloud.common.domain.statistics.SportActiveUserStatistics;
import com.xinbo.cloud.common.domain.statistics.SportBetTypeStatistics;
import com.xinbo.cloud.common.domain.statistics.SportMerchantStatistics;
import com.xinbo.cloud.common.domain.statistics.SportUserStatistics;
import com.xinbo.cloud.common.dto.PageDto;
import com.xinbo.cloud.common.dto.statistics.SportActiveUserStatisticsDto;
import com.xinbo.cloud.common.dto.statistics.SportBetTypeStatisticsDto;
import com.xinbo.cloud.common.dto.statistics.SportMerchantStatisticsDto;
import com.xinbo.cloud.common.dto.statistics.SportUserStatisticsDto;
import com.xinbo.cloud.common.mapper.statistics.SportActiveUserStatisticsMapper;
import com.xinbo.cloud.common.mapper.statistics.SportBetTypeStatisticsMapper;
import com.xinbo.cloud.common.mapper.statistics.SportMerchantStatisticsMapper;
import com.xinbo.cloud.common.mapper.statistics.SportUserStatisticsMapper;
import com.xinbo.cloud.common.service.api.SportStatisticsServiceApi;
import com.xinbo.cloud.common.utils.MapperUtil;
import com.xinbo.cloud.common.vo.PageVo;
import com.xinbo.cloud.common.vo.statistics.SportActiveUserStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportBetTypeStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportMerchantStatisticsSearchVo;
import com.xinbo.cloud.common.vo.statistics.SportUserStatisticsSearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

/**
 * @author 汉斯
 * @date 2020/4/6 12:14
 * @desc 体育统计API实现
 */
@Service(version = "1.0.0")
public class SportStatisticsServiceApiImpl implements SportStatisticsServiceApi {

    @Autowired
    @SuppressWarnings("all")
    private SportActiveUserStatisticsMapper sportActiveUserStatisticsMapper;
    @Autowired
    @SuppressWarnings("all")
    private SportBetTypeStatisticsMapper sportBetTypeStatisticsMapper;
    @Autowired
    @SuppressWarnings("all")
    private SportMerchantStatisticsMapper sportMerchantStatisticsMapper;
    @Autowired
    @SuppressWarnings("all")
    private SportUserStatisticsMapper sportUserStatisticsMapper;

    /**
     * 分页获取活跃用户登录报表数据
     * @param pageVo
     * @return
     */
    @Override
    public PageDto<SportActiveUserStatisticsDto> getSportActiveUserStatisticsPage(PageVo<SportActiveUserStatisticsSearchVo> pageVo) {
        Example example = new Example(SportActiveUserStatistics.class);
        SportActiveUserStatisticsSearchVo vo = pageVo.getModel();
        if (vo != null) {
            if (StrUtil.isNotBlank(vo.getUserName()))
                example.and().andEqualTo("userName", vo.getUserName());
            if (StrUtil.isNotBlank(vo.getMerchantCode()))
                example.and().andEqualTo("merchantCode", vo.getMerchantCode());
            if (StrUtil.isNotBlank(vo.getMerchantName()))
                example.and().andLike("merchantName", vo.getMerchantName());
            if (vo.getStartDate() != null)
                example.and().andGreaterThanOrEqualTo("day", vo.getStartDate());
            if (vo.getEndDate() != null)
                example.and().andLessThan("day", vo.getEndDate());
        }
        if (StrUtil.isBlank(pageVo.getSort()))
            pageVo.setSort("day desc");
        example.setOrderByClause(pageVo.getSort());
        PageHelper.startPage(pageVo.getPageNum(), pageVo.getPageSize());
        PageInfo<SportActiveUserStatistics> pageInfo = new PageInfo<>(sportActiveUserStatisticsMapper.selectByExample(example));
        return PageDto.<SportActiveUserStatisticsDto>builder().list(MapperUtil.many(pageInfo.getList(), SportActiveUserStatisticsDto.class)).total(pageInfo.getTotal()).build();
    }

    /**
     * 分页获取体育投注类型报表数据
     * @param pageVo
     * @return
     */
    @Override
    public PageDto<SportBetTypeStatisticsDto> getSportBetTypeStatisticsPage(PageVo<SportBetTypeStatisticsSearchVo> pageVo) {
        Example example = new Example(SportBetTypeStatistics.class);
        SportBetTypeStatisticsSearchVo vo = pageVo.getModel();
        if (vo != null) {
            if (StrUtil.isNotBlank(vo.getBetType()))
                example.and().andEqualTo("betType", vo.getBetType());
            if (StrUtil.isNotBlank(vo.getMerchantCode()))
                example.and().andEqualTo("merchantCode", vo.getMerchantCode());
            if (StrUtil.isNotBlank(vo.getMerchantName()))
                example.and().andLike("merchantName", vo.getMerchantName());
            if (vo.getStartDate() != null)
                example.and().andGreaterThanOrEqualTo("day", vo.getStartDate());
            if (vo.getEndDate() != null)
                example.and().andLessThan("day", vo.getEndDate());
        }
        if (StrUtil.isBlank(pageVo.getSort()))
            pageVo.setSort("day desc");
        example.setOrderByClause(pageVo.getSort());
        PageHelper.startPage(pageVo.getPageNum(), pageVo.getPageSize());
        PageInfo<SportBetTypeStatistics> pageInfo = new PageInfo<>(sportBetTypeStatisticsMapper.selectByExample(example));
        return PageDto.<SportBetTypeStatisticsDto>builder().list(MapperUtil.many(pageInfo.getList(), SportBetTypeStatisticsDto.class)).total(pageInfo.getTotal()).build();
    }

    /**
     * 分页获取体育商户报表数据
     * @param pageVo
     * @return
     */
    @Override
    public PageDto<SportMerchantStatisticsDto> getSportMerchantStatisticsPage(PageVo<SportMerchantStatisticsSearchVo> pageVo) {
        Example example = new Example(SportMerchantStatistics.class);
        SportMerchantStatisticsSearchVo vo = pageVo.getModel();
        if (vo != null) {
            if (StrUtil.isNotBlank(vo.getMerchantCode()))
                example.and().andEqualTo("merchantCode", vo.getMerchantCode());
            if (StrUtil.isNotBlank(vo.getMerchantName()))
                example.and().andLike("merchantName", vo.getMerchantName());
            if (vo.getStartDate() != null)
                example.and().andGreaterThanOrEqualTo("day", vo.getStartDate());
            if (vo.getEndDate() != null)
                example.and().andLessThan("day", vo.getEndDate());
        }
        if (StrUtil.isBlank(pageVo.getSort()))
            pageVo.setSort("day desc");
        example.setOrderByClause(pageVo.getSort());
        PageHelper.startPage(pageVo.getPageNum(), pageVo.getPageSize());
        PageInfo<SportMerchantStatistics> pageInfo = new PageInfo<>(sportMerchantStatisticsMapper.selectByExample(example));
        return PageDto.<SportMerchantStatisticsDto>builder().list(MapperUtil.many(pageInfo.getList(), SportMerchantStatisticsDto.class)).total(pageInfo.getTotal()).build();
    }

    /**
     * 分页获取体育用户报表数据
     * @param pageVo
     * @return
     */
    @Override
    public PageDto<SportUserStatisticsDto> getSportUserStatisticsPage(PageVo<SportUserStatisticsSearchVo> pageVo) {
        Example example = new Example(SportUserStatistics.class);
        SportUserStatisticsSearchVo vo = pageVo.getModel();
        if (vo != null) {
            if (StrUtil.isNotBlank(vo.getUserName()))
                example.and().andEqualTo("userName", vo.getUserName());
            if (StrUtil.isNotBlank(vo.getMerchantCode()))
                example.and().andEqualTo("merchantCode", vo.getMerchantCode());
            if (StrUtil.isNotBlank(vo.getMerchantName()))
                example.and().andLike("merchantName", vo.getMerchantName());
            if (vo.getStartDate() != null)
                example.and().andGreaterThanOrEqualTo("day", vo.getStartDate());
            if (vo.getEndDate() != null)
                example.and().andLessThan("day", vo.getEndDate());
        }
        if (StrUtil.isBlank(pageVo.getSort()))
            pageVo.setSort("day desc");
        example.setOrderByClause(pageVo.getSort());
        PageHelper.startPage(pageVo.getPageNum(), pageVo.getPageSize());
        PageInfo<SportUserStatistics> pageInfo = new PageInfo<>(sportUserStatisticsMapper.selectByExample(example));
        return PageDto.<SportUserStatisticsDto>builder().list(MapperUtil.many(pageInfo.getList(), SportUserStatisticsDto.class)).total(pageInfo.getTotal()).build();
    }
}
