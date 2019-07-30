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
 *  4.此处首先进行通配符验证
 */
public class Topics_Producer01 {

    /**
     *
     *
     */
    //交换机
      private static  String  Exchange_Topics="Exchange_Topics";
    //发送邮件队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Topics_SendEmail="QueQue_Topics_SendEmail";
    //发送短信队列----此处可以等同于routingKey使用
    private static  final   String QueQue_Topics_SendMSG="QueQue_Topics_SendMSG";

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
            channel.exchangeDeclare(Exchange_Topics,BuiltinExchangeType.TOPIC);
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            //声明邮件队列
            channel.queueDeclare(QueQue_Topics_SendEmail,true,false,false,null);
            //声明短信队列
            channel.queueDeclare(QueQue_Topics_SendMSG,true,false,false,null);

            //利用通道将交换机和队列进行绑定
//            String queue, String exchange, String routingKey, Map<String, Object> arguments
            //绑定Email-----此处的rountKey通配符绑定路由---如果不绑定指定的交换机那么就会使用默认的交换机，如果不指定队列，那么就会使用默认的队列
            //此处的路由不再使用我们的队列名称而是指定的统配的路由routingKey
            //统配Email
            channel.queueBind(QueQue_Topics_SendEmail,Exchange_Topics,"info.email",null);
            //统配msg
            channel.queueBind(QueQue_Topics_SendMSG,Exchange_Topics,"info.msg",null);
            //统配email.msg
            channel.queueBind(QueQue_Topics_SendMSG,Exchange_Topics,"info.email.msg",null);
        //下面进行路由绑定：
            // 第一个是路由：info.email
            // 第二个是路由：info.msg
            // 第三个是路由：info.msg.email


            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息（发送邮件）："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处使用的routingKey和队列等价
                channel.basicPublish(Exchange_Topics,"info.email",true,null,message.getBytes());
                System.err.println("生产者生产了发送邮件的消息："+message);
            }
            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息（发送短信）："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处使用的routingKey和队列等价
                channel.basicPublish(Exchange_Topics,"info.msg",true,null,message.getBytes());
                System.err.println("生产者生产了发送短信的消息："+message);
            }
            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息（发送短信,邮件）："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处使用的routingKey和队列等价---此处采用路由原则
                channel.basicPublish(Exchange_Topics,"info.email.msg",true,null,message.getBytes());
                System.err.println("生产者生产了发送短信,邮件的消息："+message);
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
