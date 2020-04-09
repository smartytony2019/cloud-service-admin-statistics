package com.xinbo.cloud.service.admin.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;
/**
 * @author 汉斯
 * @date 2020/3/23 12:28
 * @desc 统计报表服务入口
 */
@EnableDiscoveryClient
@EnableSwagger2
@MapperScan(basePackages = "com.xinbo.cloud.common.mapper")
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
public class CloudStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsApplication.class ,args);
    }
}