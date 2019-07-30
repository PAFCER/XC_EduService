package com.xuecheng.manage_media_processor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hotwater on 2018/7/13.
 * 消息队列的配置文件
 * 此处为处理视频文件的消息队列的配置文件类
 * 涉及到访问RabbitMQ的基本信息及其
 * 此处我们消息队列我们采取的模式是路由模式
 *  因此我们需要构建一个交换机，
 *      对于不同的媒资资源比如video和doc会采用不同的路由进行区分
 *      另外我们对应的消费端，通过绑定相同的交换机不同的路由进行区分
 *      如果需要实现多节点部署只需要消费端队列绑定指定的路由即可
 *      生产者不关心消费端的队列，只关心与交换机和路由绑定的队列是谁
 *      将绑定到当前的交换机和路由的队列发送消息即可
 *      对于消费者只需要让自己监听的队列进行绑定到对应的交换机和路由上即可。
 */
@Configuration
public class RabbitMQConfig {
    //连接MQ的基本信息注入
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtualHost}")
    private String virtualHost;

    //定义一个交换机
    public static  final  String  Exchange_MEDIA_PROCESSOR="Exchange_Media_Processor";
    //定义视频处理的队列
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    public String QUEUE_MEDIA_VIDEO;
    //定义视频处理路由
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String ROUTING_MEDIA_VIDEO;

    //消费者并发数量
    public static final int DEFAULT_CONCURRENT = 10;
    @Bean("customContainerFactory")
    public SimpleRabbitListenerContainerFactory
    containerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory
            connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(DEFAULT_CONCURRENT);
        factory.setMaxConcurrentConsumers(DEFAULT_CONCURRENT);
        configurer.configure(factory, connectionFactory);
        return factory;
    }



    //声明连接工厂对象
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(host);
        cachingConnectionFactory.setPort(port);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setVirtualHost(virtualHost);
          return cachingConnectionFactory;
    }
    //获取操作对象
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
    return new RabbitTemplate(connectionFactory);
    }
    //交换机配置
    @Bean(Exchange_MEDIA_PROCESSOR)
    public Exchange  Exchange_MEDIA_PROCESSOR(){
        return ExchangeBuilder.directExchange(Exchange_MEDIA_PROCESSOR).build();
    }
    //声明队列
    @Bean("${xc-service-manage-media.mq.queue-media-video-processor}")
    public Queue  QUEUE_MEDIA_VIDEO(){
        return  new Queue(QUEUE_MEDIA_VIDEO,true,false,false,null);
    }

    @Bean
    public Binding Bing_queue_media(
            @Qualifier("${xc-service-manage-media.mq.queue-media-video-processor}") Queue queue,
            @Qualifier(Exchange_MEDIA_PROCESSOR) Exchange  exchange
    ){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_MEDIA_VIDEO).noargs();
    }


}
