package com.xinbo.cloud.task.statistics.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.xinbo.cloud")
@SpringBootApplication(scanBasePackages = "com.xinbo.cloud")
public class CloudStatisticsConsumer {
    public static void main(String[] args) {
        SpringApplication.run(CloudStatisticsConsumer.class ,args);
    }
}