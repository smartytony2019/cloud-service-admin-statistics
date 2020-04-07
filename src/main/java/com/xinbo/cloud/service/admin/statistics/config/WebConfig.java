package com.xinbo.cloud.service.admin.statistics.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author 熊二
 * @date 2020/3/15 20:17
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
