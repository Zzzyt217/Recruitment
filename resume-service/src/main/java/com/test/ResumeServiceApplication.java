package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.test.Mapper")
@EnableFeignClients
public class ResumeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResumeServiceApplication.class, args);
    }
} 