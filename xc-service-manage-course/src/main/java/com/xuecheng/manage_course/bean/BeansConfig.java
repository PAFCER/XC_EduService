package com.xuecheng.manage_course.bean;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hotwater on 2018/7/5.
 */
@Component//组件
public class BeansConfig {

    @Bean//okhttp的连接restTemplate
    @LoadBalanced//ribbon的负载均衡
    public RestTemplate  restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
