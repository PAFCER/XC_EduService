package com.xuecheng.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hotwater on 2018/7/8.
 */
@EnableFeignClients//此处是利用feign进行支持Spring扫描注解@FeignClient，执行生成接口的代理对象，进行支持从服务中心抽取服务列表
@EnableDiscoveryClient//标记为eureka客户端
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.search")//实体扫描
@ComponentScan("com.xuecheng.api")       //扫描接口
@ComponentScan("com.xuecheng.framework")//扫描通用组件
@ComponentScan("com.xuecheng.search")   //扫描当前工程
public class ElasticSearchApplication {


    public static void main(String[] args) {
        SpringApplication.run(ElasticSearchApplication.class,args);
    }

}
