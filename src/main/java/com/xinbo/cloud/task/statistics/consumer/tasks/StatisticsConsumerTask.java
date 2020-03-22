package com.xinbo.cloud.task.statistics.consumer.tasks;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.json.JSONUtil;
import com.xinbo.cloud.common.constant.RocketMQTopic;
import com.xinbo.cloud.common.domain.common.Merchant;
import com.xinbo.cloud.common.domain.statistics.SportActiveUserStatistics;
import com.xinbo.cloud.common.domain.statistics.SportMerchantStatistics;
import com.xinbo.cloud.common.domain.statistics.SportUserStatistics;
import com.xinbo.cloud.common.dto.RocketMessage;
import com.xinbo.cloud.common.dto.common.UserBalanceOperationDto;
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
            switch (messageIdEnum) {
                case Sport_ActiveUserInto:
                    SportActiveUserStatistics objSportActiveUserStatistics = JsonUtil.fromJsonToObject(message.getMessageBody().toString(), SportActiveUserStatistics.class);
                    sportActiveUserService.insert(objSportActiveUserStatistics);
                    break;
                case Sport_ActiveUserLoginCount:
                    SportActiveUserStatistics objUpdateLoginInfo = JsonUtil.fromJsonToObject(message.getMessageBody().toString(), SportActiveUserStatistics.class);
                    sportActiveUserService.updateLoginInfo(objUpdateLoginInfo);
                    break;
            }
        } else {
            MoneyChangeEnum moneyChangeEnum = MoneyChangeEnum.valueOf(message.getMessageId());
            switch (moneyChangeEnum) {
                case Lucky://投注
                    break;
                case MoneyIn://投注
                case MoneyOut://投注
                    UserBalanceOperationDto balanceOperationDto = JsonUtil.fromJsonToObject(message.getMessageBody().toString(), UserBalanceOperationDto.class);
                    Merchant merchant = merchantService.getByMerchantCode(balanceOperationDto.getMerchantCode());
                    updateSportUserTransferStatistics(balanceOperationDto, merchant);
                    updateSportMerchantTransferStatistics(balanceOperationDto, merchant);
                    break;
            }
        }

    }

    /**
     * 更新体育用户报表中的转入转出金额信息
     *
     * @param dto
     * @param merchant
     */
//    @Async
    void updateSportUserTransferStatistics(UserBalanceOperationDto dto, Merchant merchant) {
        try {
            SportUserStatistics objSportUser = SportUserStatistics.builder().merchantCode(merchant.getMerchantCode()).dataNode(merchant.getDataNode())
                    .merchantName(merchant.getMerchantCode()).betMoney(0).payoutMoney(0).winMoney(0).transferOutMoney(0).userName(dto.getUserName())
                    .transferInMoney(dto.getOperationMoney()).build();
            if (dto.getOperationType() == MoneyChangeEnum.MoneyOut.getCode())
                objSportUser.setTransferOutMoney(dto.getOperationMoney());
            if (dto.getOperationType() == MoneyChangeEnum.MoneyIn.getCode())
                objSportUser.setTransferInMoney(dto.getOperationMoney());
            int a = sportUserService.toStatistic(objSportUser);
        } catch (Exception ex) {
            log.error("更新体育用户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }

    /**
     * 更新体育商户报表中的转入转出金额信息
     *
     * @param dto
     * @param merchant
     */
//    @Async
    void updateSportMerchantTransferStatistics(UserBalanceOperationDto dto, Merchant merchant) {
        try {
            SportMerchantStatistics objSportMerchant = SportMerchantStatistics.builder().merchantCode(merchant.getMerchantCode()).dataNode(merchant.getDataNode())
                    .merchantName(merchant.getMerchantCode()).betMoney(0).payoutMoney(0).winMoney(0).transferOutMoney(0)
                    .transferInMoney(dto.getOperationMoney()).build();
            if (dto.getOperationType() == MoneyChangeEnum.MoneyOut.getCode())
                objSportMerchant.setTransferOutMoney(dto.getOperationMoney());
            if (dto.getOperationType() == MoneyChangeEnum.MoneyIn.getCode())
                objSportMerchant.setTransferInMoney(dto.getOperationMoney());
            int a = sportMerchantService.toStatistic(objSportMerchant);
        } catch (Exception ex) {
            log.error("更新体育商户报表中的转入转出金额信息失败，原始数据：" + JSONUtil.toJsonStr(dto), ex);
        }
    }
}