package com.xuecheng.manage_course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hotwater on 2018/6/30.
 */
//1、 启动类添加
//@EnableFeignClients注解，Spring会扫描标记了@FeignClient注解的接口，并生成此接口的代理对象
//2、 @FeignClient(value = XcServiceList.XC_SERVICE_MANAGE_CMS)即指定了cms的服务名称，Feign会从注册中
//心获取cms服务列表，并通过负载均衡算法进行服务调用。
//3、在接口方法 中使用注解@GetMapping(API_PRE+"/get/{id}")，指定调用的url，Feign将根据url进行远程调用。


//SpringCloud对Feign进行了增强兼容了SpringMVC的注解 ，我们在使用SpringMVC的注解时需要注意：
//1、feignClient接口 有参数在参数必须加
//@PathVariable("XXX")和@RequestParam("XXX")
//2、feignClient返回值为复杂对象时其类型必须有无参构造函数。

/**
 * 关于ribbon与feign的对比
 * 在SpringCloud中，restTemplate是对OKHTTP的封装，
 *      此时我们利用ribbon进行实现调用负载均衡的话，需要利用注解@loadBalanced进行修饰我们的resttemplate这个对象生成方法上
 *      此处我们可以通过进行测试负载均衡的效果出来
 * 在SpringCloud中，我们可以使用feign进行简化我们的操作，首先需要在接口上利用注解进行修饰@FeignClient("xxx"),其中的xxx表示
 *      在eureka中注册的服务名称，此处还需要利用@EnableFeignClients进行处理启动类，此处是让Spring支持Feign，包扫描会扫描
 *      注解@FeignClient的接口，生成该接口的代理对象，并且利用注解上的服务类名称进行抽取对应的eureka上的注册服务列表
 *      然后我们可以直接使用该接口对象注入到我们需要的层中进行调用服务，此时我们可以发觉，简单明了，轻松愉快。
 */

@EnableFeignClients//此处是利用feign进行支持Spring扫描注解@FeignClient，执行生成接口的代理对象，进行支持从服务中心抽取服务列表
@EnableDiscoveryClient//标记为eureka客户端
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.course")//扫描实体类
@ComponentScan("com.xuecheng.api")//扫描接口
@ComponentScan("com.xuecheng.framework.exception")//扫描通用工程目录
@ComponentScan("com.xuecheng.manage_course")//扫描当前工程下的
public class SpringCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCourseApplication.class,args);

    }
}
