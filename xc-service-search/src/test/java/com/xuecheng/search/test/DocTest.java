package com.xuecheng.search.test;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/8.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DocTest {
    @Autowired
    RestHighLevelClient  restHighLevelClient;
    //测试添加文档，其实创建文档就是进行数据索引（动词）的时候。

    /**
     *
     *      1.postman :
     *                  http://localhost:9200/xc_test_postman/doc/1
     *         参数：{
                     "name":"spring cloud实战",
                     "description":"本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战SpringBoot 4.注册中心eureka。",
                     "studymodel":"201001",
                     "price":5.6
                     }
     *
     */
    @Test
    public  void testAddDoc(){

        //1.创建创建索引文档的对象indexRequest
        //创建上述对象时候需要指定三个参数：第一个是索引库名称，第二个是类型，第三个是指定id，
        //此处创建具有三个重载的构造函数，可以参考其他的两个构造进行测试
        IndexRequest  indexRequest = new IndexRequest("xc_test_java","doc","1");
        //2.构建数据源---利用MAP
        Map<String,Object> resource=new HashMap<String,Object>();
        resource.put("name","spring cloud实战");
        resource.put("description","本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战SpringBoot 4.注册中心eureka。");
        resource.put("syudymodel","201001");
        resource.put("price",5.6);
        //3.指定数据源
        indexRequest.source(resource);

        //4.进行索引
        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest);
            RestStatus status = indexResponse.status();
            System.err.println("status:"+status);
            DocWriteResponse.Result result = indexResponse.getResult();
            System.err.println("result:"+result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查询文档
    @Test
    public  void getDocTest(){
        GetRequest  getRequest=new GetRequest("xc_test_java","doc","1");
        try {
            GetResponse getResponse = restHighLevelClient.get(getRequest);
            boolean exists = getResponse.isExists();
            System.err.println("exists: "+exists);
            Map<String, Object> objectMap = getResponse.getSource();
            System.err.println("objectMap:"+objectMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新文档
     */
    @Test
    public  void updateDocTest(){
        //1.创建更新请求对象
        UpdateRequest  updateRequest=new UpdateRequest("xc_test_java","doc","1");
        //2.局部更新一个字段
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("price",99.99);
        //3.将更新的数据注入到请求对象中
        updateRequest.doc(hashMap);
        //4.利用客户端进行更新
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
            GetResult getResult = updateResponse.getGetResult();
            System.err.println("getResult:"+getResult);
            RestStatus status = updateResponse.status();
            //5.状态
            System.err.println("status:"+status);//ok
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
  /**
     * 删除文档
     */
    @Test
    public  void deleteDocTest(){
        //1.创建更新请求对象
        DeleteRequest  deleteRequest=new DeleteRequest("xc_test_java","doc","1");

        //4.利用客户端进行更新
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
            DocWriteResponse.Result deleteResponseResult = deleteResponse.getResult();
            System.err.println("deleteResponseResult:"+deleteResponseResult);
            RestStatus status = deleteResponse.status();
            //5.状态
            System.err.println("status:"+status);//ok
        } catch (IOException e) {
            e.printStackTrace();
        }


    }





}
