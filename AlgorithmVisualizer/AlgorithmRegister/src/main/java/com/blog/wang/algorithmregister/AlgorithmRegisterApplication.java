package com.blog.wang.algorithmregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class AlgorithmRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlgorithmRegisterApplication.class, args);
    }

}
