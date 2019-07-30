package com.xuecheng.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by hotwater on 2018/7/8.
 * 利用配置文件进行初始化Elastic Search提供的Java操作ES的对象
 *
 */
@Configuration//标记为配置类
public class ElasticSearchConfig {

    //加载配置文件中的配置信息
    @Value("${xuecheng.elasticsearch.hostlist}")
    private  String hostlist;
    //创建restHightClient对象注入到spring容器中
    @Bean
    public RestHighLevelClient  restHighLevelClient(){
        //获取配置的ES服务器列表字符串
        String[] hostlist_str = hostlist.split(",");
        //依据配置文件中配置的节点数量进行初始化主机地址数量数组
        HttpHost [] httpHosts =new HttpHost[hostlist_str.length];
        int   index=0;
        for (String host_port:hostlist_str) {
            String[] one = host_port.split(":");
            String host=one[0];//主机地址
            Integer port=Integer.parseInt(one[1]);//端口号
            httpHosts[index++]=new HttpHost(host,port);
        }
        //组装完需要的初始化参数即进行传递参数进行构造创建restHightLevelClient对象置入spring容器中。
        return  new RestHighLevelClient(RestClient.builder(httpHosts));
    }


}
