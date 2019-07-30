package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hotwater on 2018/6/30.
 *
 * 利用Spring整合 RabbitMQ
 */
@Configuration
public class RabbitMQConfig {

    //主要涉及到
    // 队列，
    // 交换机，
    // 绑定
    //邮件队列
    public  static  final  String  QueQue_SpringBoot_Topics_Email="QueQue_SpringBoot_Topics_Email";
    //短信队列
    public  static  final  String  QueQue_SpringBoot_Topics_SMG="QueQue_SpringBoot_Topics_SMG";
    //交换机
    public  static  final  String  Exchange_SpringBoot_Topics="Exchange_SpringBoot_Topics";

    //声明队列QueQue_SpringBoot_Topics_Email
    @Bean(QueQue_SpringBoot_Topics_Email)
    public Queue QueQue_SpringBoot_Topics_Email(){

        return  new Queue(QueQue_SpringBoot_Topics_Email);
    }
    //声明队列QueQue_SpringBoot_Topics_SMG
    @Bean(QueQue_SpringBoot_Topics_SMG)
    public Queue QueQue_SpringBoot_Topics_SMG(){
        return  new Queue(QueQue_SpringBoot_Topics_SMG);
    }

    //声明交换机
    @Bean(Exchange_SpringBoot_Topics)
    public Exchange  Exchange_SpringBoot_Topics(){
        return ExchangeBuilder.topicExchange(Exchange_SpringBoot_Topics).durable(true).build();
    }

    //绑定队列到交换机
@Bean
    public Binding  bindingQueQue_SpringBoot_Topics_EmailToExchange(@Qualifier("QueQue_SpringBoot_Topics_Email")Queue QueQue_SpringBoot_Topics_Email ,
                                           @Qualifier("Exchange_SpringBoot_Topics") Exchange Exchange_SpringBoot_Topics
                                           ){
        return BindingBuilder.bind(QueQue_SpringBoot_Topics_Email).to(Exchange_SpringBoot_Topics).with("info.#.email.#").noargs();
    }
    //绑定队列到交换机
@Bean
    public Binding  bindingQueQue_SpringBoot_Topics_SMGToExchange(@Qualifier("QueQue_SpringBoot_Topics_SMG")Queue QueQue_SpringBoot_Topics_SMG ,
                                           @Qualifier("Exchange_SpringBoot_Topics") Exchange Exchange_SpringBoot_Topics
                                           ){
        return BindingBuilder.bind(QueQue_SpringBoot_Topics_SMG).to(Exchange_SpringBoot_Topics).with("info.#.smg.#").noargs();
    }

}
