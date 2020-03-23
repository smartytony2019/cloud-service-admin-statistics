package com.xinbo.cloud.task.statistics.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@EnableScheduling
@EnableSwagger2
@MapperScan(basePackages = "com.xinbo.cloud")
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
public class CloudStatisticsConsumer {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsConsumer.class ,args);
    }
}