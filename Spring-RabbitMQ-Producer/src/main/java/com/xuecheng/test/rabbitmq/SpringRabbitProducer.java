package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hotwater on 2018/6/30.
 * 此处的测试需要依赖于SpringBoot的引导类启动加载Spring容器
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringRabbitProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;
        @Test
        public  void testProducer() {
            String message="";
            for (int x = 0; x < 10; x++) {
                message="2018年度生产者："+System.currentTimeMillis();
                rabbitTemplate.convertAndSend(RabbitMQConfig.Exchange_SpringBoot_Topics,"info.smg.email",message );
                System.err.println("发送消息："+message);
            }
        }

}
