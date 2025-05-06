package com.monitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
@MapperScan("com.monitor.system.mapper")
public class DeyuanMonitorPlatformApplication  {
    public static void main(String[] args) {
        SpringApplication.run(DeyuanMonitorPlatformApplication.class, args);
    }
}
