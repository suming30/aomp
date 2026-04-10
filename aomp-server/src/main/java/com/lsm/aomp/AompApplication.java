package com.lsm.aomp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lsm.aomp.mapper")
@EnableAsync
@EnableScheduling
public class AompApplication {

    public static void main(String[] args) {
        SpringApplication.run(AompApplication.class, args);
    }
}
