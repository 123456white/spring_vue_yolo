package com.teng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.teng.mapper")
@SpringBootApplication
public class SpringbootProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootProjectApplication.class, args);
    }

}
