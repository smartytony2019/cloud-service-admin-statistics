package com.xinbo.cloud.service.statistics.service.fallback;

import com.xinbo.cloud.common.constant.FallbackMessage;
import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.JwtUser;
import com.xinbo.cloud.common.dto.ResultFactory;
import com.xinbo.cloud.service.statistics.service.JwtService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * @author 汉斯
 * @date 2020/4/1 11:13
 * @desc 熔断器
 */
@Component
public class JwtServiceFallback implements JwtService {

    @Override
    public ActionResult generateToken(JwtUser jwtParams) {
        return ResultFactory.fallback(MessageFormat.format(FallbackMessage.MSG_FORMAT, JwtService.class.getSimpleName()));
    }

    @Override
    public ActionResult parseToken(String token) {
        return ResultFactory.fallback(MessageFormat.format(FallbackMessage.MSG_FORMAT, JwtService.class.getSimpleName()));
    }
}
