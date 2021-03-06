package com.xinbo.cloud.service.admin.statistics.tasks;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.json.JSONUtil;
import com.xinbo.cloud.common.constant.RocketMQTopic;
import com.xinbo.cloud.common.domain.statistics.*;
import com.xinbo.cloud.common.dto.RocketMessage;
import com.xinbo.cloud.common.dto.sport.SportBetDetailDto;
import com.xinbo.cloud.common.dto.sport.SportBetDto;
import com.xinbo.cloud.common.dto.statistics.ActiveUserOperationDto;
import com.xinbo.cloud.common.dto.statistics.UserBalanceOperationDto;
import com.xinbo.cloud.common.enums.MoneyChangeEnum;
import com.xinbo.cloud.common.enums.RocketMessageIdEnum;
import com.xinbo.cloud.common.enums.SportBetTypeEnum;
import com.xinbo.cloud.common.service.statistics.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 汉斯
 * @date 2020/3/19 11:37
 * @desc 体育统计消费Task
 */
@Slf4j
@Service
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = RocketMQTopic.STATISTICS_TOPIC, consumerGroup = "${rocketmq.producer-group}")
public class StatisticsConsumerTask implements RocketMQListener<String> {
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


    @Autowired
    @Qualifier("lotteryActiveUserStatisticsServiceImpl")
    private LotteryActiveUserStatisticsService lotteryActiveUserService;

    @Autowired
    @Qualifier("lotteryTypeStatisticsServiceImpl")
    private LotteryTypeStatisticsService lotteryTypeService;

    @Autowired
    @Qualifier("lotteryMerchantStatisticsServiceImpl")
    private LotteryMerchantStatisticsService lotteryMerchantService;

    @Autowired
    @Qualifier("lotteryUserStatisticsServiceImpl")
    private LotteryUserStatisticsService lotteryUserService;

    @Override
    public void onMessage(String str) {
        RocketMessage message = JSONUtil.toBean(str, RocketMessage.class);
        List<Object> listValues = EnumUtil.getFieldValues(RocketMessageIdEnum.class, "code");
        if (listValues.contains(message.getMessageId())) {
            RocketMessageIdEnum messageIdEnum = RocketMessageIdEnum.valueOf(message.getMessageId());
            ActiveUserOperationDto dto = JSONUtil.toBean(message.getMessageBody().toString(), ActiveUserOperationDto.class);
            Date day = DateUtil.parse(DateUtil.format(dto.getOperationTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
            switch (messageIdEnum) {
                case Sport_ActiveUserInto://投注
                case Sport_ActiveUserLoginCount://投注
                    SportActiveUserStatistics objSportActiveUserStatistics = SportActiveUserStatistics.builder().merchantName(dto.getMerchantName())
                            .merchantCode(dto.getMerchantCode()).dataNode(dto.getDataNode()).userName(dto.getUserName())
                            .regIp(dto.getIp()).regTime(dto.getOperationTime()).loginCount(0)
                            .device(dto.getDevice()).day(day).build();
                    //如果是更新登录次数，要加入最后登录时间和最后登录IP
                    if (messageIdEnum == RocketMessageIdEnum.Sport_ActiveUserLoginCount) {
                        objSportActiveUserStatistics.setLoginCount(1);
                        objSportActiveUserStatistics.setLastLoginIp(dto.getIp());
                        objSportActiveUserStatistics.setLastLoginTime(dto.getOperationTime());
                    }
                    sportActiveUserService.save(objSportActiveUserStatistics);
                    break;
                case Lottery_ActiveUserInto://投注
                case Lottery_ActiveUserLoginCount://投注
                    LotteryActiveUserStatistics objLotteryActiveUserStatistics = LotteryActiveUserStatistics.builder().merchantName(dto.getMerchantName())
                            .merchantCode(dto.getMerchantCode()).dataNode(dto.getDataNode()).userName(dto.getUserName())
                            .regIp(dto.getIp()).regTime(dto.getOperationTime()).loginCount(0)
                            .device(dto.getDevice()).day(day).build();
                    //如果是更新登录次数，要加入最后登录时间和最后登录IP
                    if (messageIdEnum == RocketMessageIdEnum.Sport_ActiveUserLoginCount) {
                        objLotteryActiveUserStatistics.setLoginCount(1);
                        objLotteryActiveUserStatistics.setLastLoginIp(dto.getIp());
                        objLotteryActiveUserStatistics.setLastLoginTime(dto.getOperationTime());
                    }
                    lotteryActiveUserService.save(objLotteryActiveUserStatistics);
                    break;
            }
        } else {
            MoneyChangeEnum moneyChangeEnum = MoneyChangeEnum.valueOf(message.getMessageId());
            switch (moneyChangeEnum) {
                case Lucky://投注
                    SportBetDto sportBetDto = JSONUtil.toBean(message.getMessageBody().toString(), SportBetDto.class);
                    Date day = DateUtil.parse(DateUtil.format(sportBetDto.getSettleTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
                    updateSportBetTypeStatistics(sportBetDto, day);
                    updateSportUserBetStatistics(sportBetDto, day);
                    updateSportMerchantBetStatistics(sportBetDto, day);
                    break;
                case MoneyIn://投注
                case MoneyOut://投注
                    UserBalanceOperationDto balanceOperationDto = JSONUtil.toBean(message.getMessageBody().toString(), UserBalanceOperationDto.class);
                    updateSportUserTransferStatistics(balanceOperationDto);
                    updateSportMerchantTransferStatistics(balanceOperationDto);
                    break;
            }
        }
    }

    /**
     * 更新体育用户报表中的转入转出金额信息
     *
     * @param dto
     */
//    @Async
    void updateSportUserTransferStatistics(UserBalanceOperationDto dto) {
        try {
            SportUserStatistics objSportUser = SportUserStatistics.builder().merchantCode(dto.getMerchantCode()).dataNode(dto.getDataNode())
                    .merchantName(dto.getMerchantName()).betMoney(0).payoutMoney(0).winMoney(0).transferOutMoney(0).userName(dto.getUserName())
                    .transferInMoney(dto.getOperationMoney()).day(dto.getOperationDate()).build();
            if (dto.getOperationType() == MoneyChangeEnum.MoneyOut.getCode())
                objSportUser.setTransferOutMoney(dto.getOperationMoney());
            if (dto.getOperationType() == MoneyChangeEnum.MoneyIn.getCode())
                objSportUser.setTransferInMoney(dto.getOperationMoney());
            sportUserService.save(objSportUser);
        } catch (Exception ex) {
            log.error("更新体育用户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }

    /**
     * 更新体育商户报表中的转入转出金额信息
     *
     * @param dto
     */
//    @Async
    void updateSportMerchantTransferStatistics(UserBalanceOperationDto dto) {
        try {
            SportMerchantStatistics objSportMerchant = SportMerchantStatistics.builder().merchantCode(dto.getMerchantCode()).dataNode(dto.getDataNode())
                    .merchantName(dto.getMerchantName()).betMoney(0).payoutMoney(0).winMoney(0).transferOutMoney(0)
                    .transferInMoney(dto.getOperationMoney()).day(dto.getOperationDate()).build();
            if (dto.getOperationType() == MoneyChangeEnum.MoneyOut.getCode())
                objSportMerchant.setTransferOutMoney(dto.getOperationMoney());
            if (dto.getOperationType() == MoneyChangeEnum.MoneyIn.getCode())
                objSportMerchant.setTransferInMoney(dto.getOperationMoney());
            sportMerchantService.save(objSportMerchant);
        } catch (Exception ex) {
            log.error("更新体育商户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }


    /**
     * 更新体育用户报表中的投注信息
     *
     * @param dto
     */
    void updateSportUserBetStatistics(SportBetDto dto, Date day) {
        try {
            SportUserStatistics objSportUser = SportUserStatistics.builder().merchantCode(dto.getMerchantNo()).dataNode(dto.getDataNode())
                    .merchantName(dto.getMerchantName())
                    .userName(dto.getUserName()).day(day).transferOutMoney(0).transferInMoney(0)
                    .betMoney(dto.getBetMoney())
                    .payoutMoney(dto.getPayoutMoney())
                    .winMoney(dto.getWinMoney())
                    .build();
            sportUserService.save(objSportUser);

        } catch (Exception ex) {
            log.error("更新体育用户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }

    /**
     * 更新体育商户报表中的投注信息
     *
     * @param dto
     */
    void updateSportMerchantBetStatistics(SportBetDto dto, Date day) {
        try {
            SportMerchantStatistics objSportMerchant = SportMerchantStatistics.builder().merchantCode(dto.getMerchantNo()).dataNode(dto.getDataNode())
                    .merchantName(dto.getMerchantName())
                    .day(day).transferOutMoney(0).transferInMoney(0)
                    .betMoney(dto.getBetMoney())
                    .payoutMoney(dto.getPayoutMoney())
                    .winMoney(dto.getWinMoney())
                    .build();
            sportMerchantService.save(objSportMerchant);
        } catch (Exception ex) {
            log.error("更新体育商户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }


    /**
     * 更新体育用户报表中的投注信息
     *
     * @param dto
     */
    void updateSportBetTypeStatistics(SportBetDto dto, Date day) {
        try {
            List<SportBetDetailDto> listDetailDto = dto.getList();
            Map<Integer, List<SportBetDetailDto>> betTypeGroup = listDetailDto.stream().collect(Collectors.groupingBy(SportBetDetailDto::getBetType));
            //当前只计算 单式投注
            if (betTypeGroup.size() == listDetailDto.size()) {
                Integer betType = betTypeGroup.keySet().stream().findFirst().get();
                SportBetTypeStatistics objSportBetType = SportBetTypeStatistics.builder().merchantCode(dto.getMerchantNo()).dataNode(dto.getDataNode())
                        .merchantName(dto.getMerchantName())
                        .day(day).betType(betType.toString()).betTypeName(SportBetTypeEnum.valueOf(betType).getMsg())
                        .betUserCount(1).betUserNumber(betTypeGroup.size())
                        .betMoney(0)
                        .payoutMoney(dto.getPayoutMoney())
                        .winMoney(dto.getWinMoney())
                        .avgMoney(dto.getWinMoney())
                        .build();
                sportBetTypeService.save(objSportBetType);
            }
        } catch (Exception ex) {
            log.error("更新体育用户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }
}