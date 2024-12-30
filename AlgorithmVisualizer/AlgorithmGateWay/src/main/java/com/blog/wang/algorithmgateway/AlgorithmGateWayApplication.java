package com.blog.wang.algorithmgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.blog.wang")
public class AlgorithmGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlgorithmGateWayApplication.class, args);
    }

}
