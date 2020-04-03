package com.xinbo.cloud.service.admin.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
@EnableSwagger2
@EnableDiscoveryClient
@MapperScan(basePackages = "com.xinbo.cloud")
@EnableFeignClients
public class CloudStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsApplication.class ,args);
    }
}