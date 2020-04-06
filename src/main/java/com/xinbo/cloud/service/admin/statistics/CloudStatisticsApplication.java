package com.xinbo.cloud.service.admin.statistics;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDubbo
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
@MapperScan(basePackages = "com.xinbo.cloud.common.mapper")
public class CloudStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsApplication.class ,args);
    }
}