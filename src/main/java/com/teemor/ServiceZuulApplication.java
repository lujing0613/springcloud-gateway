package com.teemor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 路由中心服务启动类
 *
 * @author
 * @date 2019/4/19
 */

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan
public class ServiceZuulApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceZuulApplication.class, args);
    }

    
}
