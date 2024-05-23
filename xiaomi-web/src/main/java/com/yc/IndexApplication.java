package com.yc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient   //启动服务注册发现的客户端
public class IndexApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndexApplication.class,args);
    }
}
