package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hotwater on 2018/7/15.
 * 本地的redis测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class LocalRedisTest {

    @Autowired
    StringRedisTemplate  stringRedisTemplate;

    @Test
    public  void  insertDataToRedis(){

        String key="Token:12710ab9-63a1-47be-bf07-e295cf648fe2";
        Map<String,String> value_map=new HashMap<>();
        value_map.put("username","tom");
        value_map.put("password","123");
        String value= JSON.toJSONString(value_map);
        stringRedisTemplate.boundValueOps(key).set(value,60,TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.boundValueOps(key).getExpire();
        System.err.println("expire:"+expire);


    }
}
