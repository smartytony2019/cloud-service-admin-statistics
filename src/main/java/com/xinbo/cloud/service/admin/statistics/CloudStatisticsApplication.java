package com.xinbo.cloud.service.admin.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@EnableSwagger2
@MapperScan(basePackages = "com.xinbo.cloud.common.mapper")
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
public class CloudStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsApplication.class ,args);
    }
}