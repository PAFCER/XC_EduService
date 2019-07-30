package com.xuecheng.test.rabbitMQ;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by hotwater on 2018/6/29.
 * 消费者代码测试：
 * 关于RabbitMQ一共涉及到6种模式：
 1、Work queues
 2、Publish/Subscribe
 3、Routing
 4、Topics
 5、Header
 6、RPC
 *          1.工作队列模式---Work QueQues
 *          2.订阅发布模式--Publish/Subcribe
 *          3.路由模式------Routing
 *          4.通配符模式---Topics
 *          5.响应头模式---Header
 *          6.远程调用模式--RPC
 *
 *  3.此处首先进行测试第三种路由模式
 */
public class Routing_Producer01 {

    /**
     *
     *
     */
    //交换机
      private static  String  Exchange_Routing_Direct="Exchange_Routing_Direct";
    //发送邮件队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Routing_SendEmail="QueQue_Routing_SendEmail";
    //发送短信队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Routing_SendMSG="QueQue_Routing_SendMSG";

    public static void main(String[] args) {


        /**
         * 关于此处的路由模式
         * */
        ConnectionFactory  factory  = new ConnectionFactory();
        Connection  connection=null;
        Channel  channel=null;
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");

        try {
            connection=factory.newConnection();
            channel=connection.createChannel();
//            (String exchange, BuiltinExchangeType type)
            //声明交换机
            channel.exchangeDeclare(Exchange_Routing_Direct,BuiltinExchangeType.DIRECT);
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            //声明邮件队列
            channel.queueDeclare(QueQue_Routing_SendEmail,true,false,false,null);
            //声明短信队列
            channel.queueDeclare(QueQue_Routing_SendMSG,true,false,false,null);

            //利用通道将交换机和队列进行绑定
//            String queue, String exchange, String routingKey, Map<String, Object> arguments
            //绑定Email
            channel.queueBind(QueQue_Routing_SendEmail,Exchange_Routing_Direct,QueQue_Routing_SendEmail,null);
            //绑定MSG
            channel.queueBind(QueQue_Routing_SendMSG,Exchange_Routing_Direct,QueQue_Routing_SendMSG,null);

            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息（发送邮件）："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处使用的routingKey和队列等价
                channel.basicPublish(Exchange_Routing_Direct,QueQue_Routing_SendEmail,true,null,message.getBytes());
                System.err.println("生产者生产了发送邮件的消息："+message);
            }
            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息（发送短信）："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处使用的routingKey和队列等价
                channel.basicPublish(Exchange_Routing_Direct,QueQue_Routing_SendMSG,true,null,message.getBytes());
                System.err.println("生产者生产了发送短信的消息："+message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }finally {
            if(channel!=null){
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}
