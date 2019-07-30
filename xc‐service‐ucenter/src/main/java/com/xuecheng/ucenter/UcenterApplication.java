package com.xuecheng.ucenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
@EnableDiscoveryClient//支持发现客户端
@EnableFeignClients//开启FeignClient支持
@MapperScan("com.xuecheng.ucenter.dao")//开启扫描持久层接口
@EntityScan("com.xuecheng.framework.domain.ucenter")//实体扫描
@ComponentScan(basePackages = "com.xuecheng.api")//接口扫描
@ComponentScan(basePackages = "com.xuecheng.framework")//通用类扫描
@SpringBootApplication
public class UcenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UcenterApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

}
