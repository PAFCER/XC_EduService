package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by hotwater on 2018/6/29.
 * 工作模式之消费者1号
 */
public class WorkQueQue_Consumer01 {

    private static final  String WorkQueQue="WorkQueQue";
    public static void main(String[] args) {

        ConnectionFactory  factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        //消费者端不需要指定用户名密码以及虚拟目录
//        factory.setUsername("guest");
//        factory.setPassword("guest");
//        factory.setVirtualHost("/");
        Connection connection =null;
        Channel channel =null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            channel.queueDeclare(WorkQueQue,true,false,false,null);
//            String queue, boolean autoAck, Consumer callback
            DefaultConsumer  consumer=new DefaultConsumer((channel)){

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //此处可以查看对应的数据参数的数据。
//                    System.err.println("consumerTag："+consumerTag);
//                    System.err.println("envelope.getExchange():"+envelope.getExchange());
//                    System.err.println("envelope.getRoutingKey():"+envelope.getRoutingKey());
//                    System.err.println("envelope.getDeliveryTag():"+envelope.getDeliveryTag());
//                    System.err.println("properties.getContentType():"+properties.getContentType());
//                    System.err.println("new String(body):"+new String(body));
                    System.err.println("工作队列模式下：获取消费信息:"+new String(body));

                }
            };
            channel.basicConsume(WorkQueQue, true,consumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            //此处并没有进行关闭资源，为了进行保持其监听状态
        }

    }
}
