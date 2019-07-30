package xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by hotwater on 2018/6/30.
 */
@Component
public class SpringRabbitMQConsumer {

    //第一个监听，
    @RabbitListener(queues = {
            RabbitMQConfig.QueQue_SpringBoot_Topics_Email
    })
    public  void   emailConsumer(String msg, Message message, Channel  channel){

        System.err.println("邮件获取到数据："+msg);

    }    //第二个监听
    @RabbitListener(queues = {
            RabbitMQConfig.QueQue_SpringBoot_Topics_SMG
    })
    public  void   msgConsumer(String msg, Message message, Channel  channel){

        System.err.println("短信获取到数据："+msg);

    }

}
