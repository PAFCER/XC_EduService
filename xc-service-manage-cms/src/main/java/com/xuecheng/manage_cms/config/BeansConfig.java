package com.xuecheng.manage_cms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hotwater on 2018/6/28.
 */
@Component
public class BeansConfig {

    //注入一个Bean对象RestTemplate
    @Bean
    public RestTemplate  restTemplate(){

        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }


}
