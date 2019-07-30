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
 *  2.此处首先进行测试第二种订阅发布模式
 */
public class PublishSubScribe_Producer01 {

    /**
     * 订阅发布模式：
     *      需要注意单生产者利用路由交换进行绑定分发到指定的队列，不用指定路由即不用指定RoutingKey
     *      下面利用当前生产者进行分发，对于消费端是对应的发送邮件和发送短信的消费者，下面以这个案例进行描述订阅发布模式下
     *      在订阅发布模式情况下分析：
     *      首先第一个就是在单生产者情况下，我们进行了通过一个交换机进行绑定多个队列的形式进行分发队列，对应的消费端是多个消费端（不同的功能实现）
     *      我们经过测试实现了我们的需求，另外我们需要知道如果在消费端（同一类）进行多个消费者，那么就会出现轮询的方式进行消费
     *      经过测试的确进行了轮询的方式进行处理，另外我们需要注意仍然是需要将我们的消费者端进行
     *      先开启。此处需要注意的是我们此处没有指定路由key即RountingKey
     *
     */
    //交换机
      private static  String  Exchange_Pub_Sub_FANOUT="Exchange_Pub_Sub_FANOUT";
    //发送邮件队列
    private static  final   String QueQue_Pub_Sub_SendEmail="QueQue_Pub_Sub_SendEmail";
    //发送短信队列
    private static  final   String QueQue_Pub_Sub_SendMSG="QueQue_Pub_Sub_SendMSG";

    public static void main(String[] args) {


        /**
         * 关于此处的订阅发布模式
         * */
        ConnectionFactory  factory  = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection  connection=null;
        Channel  channel=null;
        try {
            connection=factory.newConnection();
            channel=connection.createChannel();
//            (String exchange, BuiltinExchangeType type)
            //声明交换机
            channel.exchangeDeclare(Exchange_Pub_Sub_FANOUT,BuiltinExchangeType.FANOUT);
//            String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            //声明邮件队列
            channel.queueDeclare(QueQue_Pub_Sub_SendEmail,true,false,false,null);
            //声明短信队列
            channel.queueDeclare(QueQue_Pub_Sub_SendMSG,true,false,false,null);

            //利用通道将交换机和队列进行绑定
//            String queue, String exchange, String routingKey, Map<String, Object> arguments
            //绑定Email
            channel.queueBind(QueQue_Pub_Sub_SendEmail,Exchange_Pub_Sub_FANOUT,"",null);
            //绑定MSG
            channel.queueBind(QueQue_Pub_Sub_SendMSG,Exchange_Pub_Sub_FANOUT,"",null);

            for(int x=0;x<10;x++){
                String message="订阅发布模式下的消息："+System.currentTimeMillis();
//                String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
                //此处并没有指定对应的RountingKey,需要注意
                channel.basicPublish(Exchange_Pub_Sub_FANOUT,"",true,null,message.getBytes());
                System.err.println("生产者生产了消息："+message);
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
