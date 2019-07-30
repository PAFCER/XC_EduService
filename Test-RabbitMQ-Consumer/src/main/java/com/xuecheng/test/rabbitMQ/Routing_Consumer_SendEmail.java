package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by hotwater on 2018/6/29.
 *  路由模式之发送邮件消费者
 */
public class Routing_Consumer_SendEmail {
    //交换机
    private static  String  Exchange_Routing_Direct="Exchange_Routing_Direct";
    //发送邮件队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Routing_SendEmail="QueQue_Routing_SendEmail";
    //发送短信队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Routing_SendMSG="QueQue_Routing_SendMSG";
    public static void main(String[] args) {

        ConnectionFactory  factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection  connection =null;
        Channel channel=null;
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();

            //声明交换机
            channel.exchangeDeclare(Exchange_Routing_Direct, BuiltinExchangeType.DIRECT);
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            //声明邮件队列
            channel.queueDeclare(QueQue_Routing_SendEmail,true,false,false,null);
            //绑定队列与交换机
//            String queue, String exchange, String routingKey, Map<String, Object> arguments
            channel.queueBind(QueQue_Routing_SendEmail,Exchange_Routing_Direct,QueQue_Routing_SendEmail,null);
//            String queue, boolean autoAck, Consumer callback
            DefaultConsumer  consumer=new DefaultConsumer((channel)){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.err.println("邮件发送者（消费者）获取消息："+new String(body));
                }
            };
            channel.basicConsume(QueQue_Routing_SendEmail,true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}
