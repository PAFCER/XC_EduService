package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by hotwater on 2018/6/29.
 * 订阅发布模式之消费者之发送短信
 */
public class PublishSubScribe_Consumer_SendMSG {
    //交换机
    private static  String  Exchange_Pub_Sub_FANOUT="Exchange_Pub_Sub_FANOUT";
    //发送短信队列
    private static  final   String QueQue_Pub_Sub_SendMSG="QueQue_Pub_Sub_SendMSG";
    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();

        Connection connection =null;
        Channel channel=null;
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();

            //声明交换机
            channel.exchangeDeclare(Exchange_Pub_Sub_FANOUT, BuiltinExchangeType.FANOUT);
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            //声明邮件队列
            channel.queueDeclare(QueQue_Pub_Sub_SendMSG,true,false,false,null);
            //绑定队列与交换机
//            String queue, String exchange, String routingKey, Map<String, Object> arguments
            channel.queueBind(QueQue_Pub_Sub_SendMSG,Exchange_Pub_Sub_FANOUT,"",null);
//            String queue, boolean autoAck, Consumer callback
            DefaultConsumer  consumer=new DefaultConsumer((channel)){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.err.println("短信发送者（消费者）获取消息："+new String(body));
                }
            };
            channel.basicConsume(QueQue_Pub_Sub_SendMSG,true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }
}