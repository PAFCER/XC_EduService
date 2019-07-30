package com.xuecheng.test.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by hotwater on 2018/6/30.
 */
@SpringBootApplication
public class SpringRabbitMQApplication {
    //需要指定SpringBoot引导类，否则无法启动
    public static void main(String[] args) {
        SpringApplication.run(SpringRabbitMQApplication.class,args);
    }

}
