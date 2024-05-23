package com.yc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication  //启动类
@EnableDiscoveryClient  //打开服务发现客户端
public class GateWayApp {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApp.class,args);
    }
}
