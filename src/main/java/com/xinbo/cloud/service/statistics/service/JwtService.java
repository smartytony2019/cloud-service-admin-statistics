package com.xinbo.cloud.service.statistics.service;

import com.xinbo.cloud.common.dto.ActionResult;
import com.xinbo.cloud.common.dto.JwtUser;
import com.xinbo.cloud.service.statistics.service.fallback.JwtServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 汉斯
 * @date 2020/4/1 11:13
 * @desc jwt 调用接口
 */

@FeignClient(name = "cloud-service-oauth", fallback = JwtServiceFallback.class)
public interface JwtService {


    @PostMapping(value = "/gw-oauth/oauth/generateToken")
    ActionResult generateToken(@RequestBody JwtUser jwtUser);


    @PostMapping(value = "/gw-oauth/oauth/parseToken")
    ActionResult parseToken(@RequestBody String token);

}
