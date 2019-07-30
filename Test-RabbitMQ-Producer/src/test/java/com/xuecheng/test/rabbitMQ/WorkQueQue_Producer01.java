package com.xuecheng.test.rabbitMQ;

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
 *  1.此处首先进行测试第一种工作队列模式
 */
public class WorkQueQue_Producer01 {

    //工作队列模式之队列名称
    private static  final   String WorkQueQue="WorkQueQue";

    public static void main(String[] args) {


        /**
         * 关于此处的工作队列模式是最简单的一种模式
         * */
        ConnectionFactory  factory  = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        Connection connection =null;
        Channel channel =null;
      try {
          connection = factory.newConnection();
          channel =    connection.createChannel();
          //注意，此处不需要进行声明交换器，但是此处需要进行声明队列
//String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
          //声明队列
          channel.queueDeclare(WorkQueQue,true,false,false,null);
//String exchange, String routingKey, BasicProperties props, byte[] body
          String message="hello  world 2018   "+System.currentTimeMillis();
          //测试完成单信息发送，下面进行多信息发送
          //注意此处的路由我们需要指定为工作模式下的队列名称
          /**
           * 总结一点：
           *        首先我们需要明确一点，我们的工作队列模式下：
           *                无论是生产者还是消费者都是可以的，但是都需要指定即声明相同的队列，另外我们
           *                在此处的工作队列模式下不需要指定交换机，此处的路由我们指定为工作队列名称即可，此处是一个细节
           *                另外我们进行单身生产者和单消费者情况下是一对一的，但是如果我们的一个生产者生产很多的消息对应的多消费者的情况。
           *                其基本是轮询的机制，即交替执行消费消息，一个轮一个，但是此处我们需要明确一点
           *                如果需要演示效果，需要先进行开启消费者端，因为毕竟你没有时间来得及去开启第二个消费端即被首先开启的消费端消费完毕。
           *                此处需要注意。
           *                2018年6月29日
           *
           * */
          for (int x=0;x<10;x++) {
              channel.basicPublish("", WorkQueQue, null, message.getBytes());
              System.err.println("工作队列模式下：生产者发送一个消息：" + message);
          }
      }catch (Exception e){
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
