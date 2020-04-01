package com.xinbo.cloud.service.statistics.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author 汉斯
 * @date 2020/3/32 12:17
 * @desc WEB配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "web")
public class WebConfig {

    /**
     * 请求头Key
     */
    private String headerKey;

    /**
     * 前缀
     */
    private String prefix;

    /**
     * 放行url
     */
    private String ignoreUrl;

}
