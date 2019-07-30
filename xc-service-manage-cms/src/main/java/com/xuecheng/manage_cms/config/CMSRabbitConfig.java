package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hotwater on 2018/6/30.
 */
@Configuration
public class CMSRabbitConfig {
    //1.发布页面的交换机
    public  static final   String  ExChange_Routing_Portal="ExChange_Routing_Portal2018";
//    //2.发布页面的队列
//    @Value("${xuecheng.mq.queue}")
//    private  String CMS_Portal_Queue;
//    //3.发布页面的路由---站点id
//    @Value("${xuecheng.mq.routingKey}")
//    private String routingKey;

    //4.声明交换机
    @Bean(ExChange_Routing_Portal)
    public Exchange  ExChange_Routing_Portal(){
        return ExchangeBuilder.directExchange(ExChange_Routing_Portal).durable(true).build();
    }
//    //5.声明队列---此处需要是常量，因此需要采用此种注入的方式
//    @Bean("${xuecheng.mq.queue}")
//    public Queue  QueQue(){
//        return  new Queue(CMS_Portal_Queue);
//    }
//    //6.绑定队列与路由
//    @Bean
//    public Binding  bindingQueToExchange(@Qualifier("${xuecheng.mq.queue}")Queue queue, @Qualifier(ExChange_Routing_Portal)Exchange exchange){
//
//        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
//    }



}
