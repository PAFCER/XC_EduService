package com.xuecheng.manage_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by hotwater on 2018/6/30.
 *  完成了生产者和消费者的测试，测试成功，很明显出了很多意外，但是意外的背后是成长的历程
 *  下面完成关于今日的第二部分代码的实现，记住苦的时候有难度的时候就是成长的时候，应该感到兴奋才对。
 */
@SpringBootApplication
public class CMSRabbitMQConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CMSRabbitMQConsumerApplication.class,args);
    }
}
