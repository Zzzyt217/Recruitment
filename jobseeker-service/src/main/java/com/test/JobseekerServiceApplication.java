package com.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobseekerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobseekerServiceApplication.class, args);
    }
}