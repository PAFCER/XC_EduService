package com.xuecheng.manage_course.test;

import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by hotwater on 2018/6/30.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
//此处遗忘了此处的实体扫描，注意实体扫描的差异性---知其然知其所以然的重要性
@EntityScan(basePackages = "com.xuecheng.framework.domain")
@ComponentScan(basePackages = "com.xuecheng.manage_course")
//@MapperScan("com.xuecheng.manage_course")
public class SpringRibbonJunitTest {

    @Autowired
    RestTemplate  restTemplate;

    /**
     * 测试Robbin的负载均衡
     */

    @Test
    public void  testRibbonLoadBalance(){
        final String pre="xc-service-manage-cms";
        final String  URL = "http://"+pre+"/cms/page/findById/5b34e7286962435b907626f4";
        for (int x=0;x<10;x++) {
            ResponseEntity<CmsPageResult> forEntity =
                    restTemplate.getForEntity(URL, CmsPageResult.class);
            CmsPageResult body = forEntity.getBody();
            System.err.println("Ribbon查询处理的数据："+body);
        }


    }




}
