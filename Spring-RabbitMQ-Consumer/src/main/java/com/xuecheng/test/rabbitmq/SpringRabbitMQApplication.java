package xuecheng.test.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by hotwater on 2018/6/30.
 *
 * 关于SpringBoot集成RabbitMQ 进行测试，发现如果现在我们采用通配符模式进行
 * 那么我们在producer通配符：info.msg.email
 * 对于我们的consumer通配符：
 *                  info.#.msg.#
 *                  info.#.email.#
 *      与上述的通配进行测试，首先通过路由，队列绑定的情况下，我们的消费端均可以收到上述的
 *      消息队列，并且都是独占一份，但是测试时候如果我们先启动了producer的话，此时进行生产，消费者
 *      启动的时候会有问题，会出现丢包的现象，另外如果我们首先先启动消费端的话，就不会丢包，会全部分别执行
 *      各自的队列数据，此时我们需要注意这个现象，另外上述测试均是实现了消费端和生产端对于队列的绑定声明，而
 *      对于路由则是指定的生产端指定info.msg.email，对于消费端则是进行采用通配符配置：
 *      比如：Email通配符：info.#.email.#
 *      比如：MSG通配符：  info.#.msg.#
 *      其中#代表的是任意多个词
 *      其中*代表的是任意单个词
 *      大概就这样，
 *      另外需要注意我们进行测试的环境是SpringBoot，此环境下是启动是在tomcat容器内启动，内部嵌套的有一个
 *      服务器，此时我们需要注意的是，如果我们进行junit测试的时候，或者想访问服务器资源的时候，
 *      那么必须使用SpringBoot启动，而SpringBoot需要引导类，因此junit测试或者访问资源的时候，SpringBoot是需要的
 *      ，此处需要明白。。。。。。。。。
 *
 */
@SpringBootApplication
public class SpringRabbitMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRabbitMQApplication.class,args);
    }
}
