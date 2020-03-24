package com.xinbo.cloud.task.statistics.consumer.tasks;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.json.JSONUtil;
import com.xinbo.cloud.common.constant.RocketMQTopic;
import com.xinbo.cloud.common.domain.common.Merchant;
import com.xinbo.cloud.common.domain.statistics.SportActiveUserStatistics;
import com.xinbo.cloud.common.domain.statistics.SportMerchantStatistics;
import com.xinbo.cloud.common.domain.statistics.SportUserStatistics;
import com.xinbo.cloud.common.dto.RocketMessage;
import com.xinbo.cloud.common.dto.statistics.SportActiveUserOperationDto;
import com.xinbo.cloud.common.dto.statistics.UserBalanceOperationDto;
import com.xinbo.cloud.common.enums.MoneyChangeEnum;
import com.xinbo.cloud.common.enums.RocketMessageIdEnum;
import com.xinbo.cloud.common.service.common.MerchantService;
import com.xinbo.cloud.common.service.statistics.SportActiveUserStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportBetTypeStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportMerchantStatisticsService;
import com.xinbo.cloud.common.service.statistics.SportUserStatisticsService;
import com.xinbo.cloud.common.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.util.Date;
import java.util.List;

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
    @Qualifier("merchantServiceImpl")
    private MerchantService merchantService;

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

    @Override
    public void onMessage(String str) {
        RocketMessage message = JsonUtil.fromJsonToObject(str, RocketMessage.class);
        List<Object> listValues = EnumUtil.getFieldValues(RocketMessageIdEnum.class, "code");
        if (listValues.contains(message.getMessageId())) {
            RocketMessageIdEnum messageIdEnum = RocketMessageIdEnum.valueOf(message.getMessageId());
            SportActiveUserOperationDto dto = JsonUtil.fromJsonToObject(message.getMessageBody().toString(), SportActiveUserOperationDto.class);
            Date day = DateUtil.parse(DateUtil.format(dto.getOperationTime(), "yyyy-MM-dd"), "yyyy-MM-dd");
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
        } else {
            MoneyChangeEnum moneyChangeEnum = MoneyChangeEnum.valueOf(message.getMessageId());
            switch (moneyChangeEnum) {
                case Lucky://投注
                    break;
                case MoneyIn://投注
                case MoneyOut://投注
                    UserBalanceOperationDto balanceOperationDto = JsonUtil.fromJsonToObject(message.getMessageBody().toString(), UserBalanceOperationDto.class);
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
}