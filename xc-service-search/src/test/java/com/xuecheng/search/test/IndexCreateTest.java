package com.xuecheng.search.test;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by hotwater on 2018/7/8.
 * 进行利用Elastic Search进行测试
 *
 * 说明此处的测试会利用postMan和JavaAPI操作进行对比进行，因此会比较繁琐，但是很有必要。
 * 1.当前文档主要涉及对于索引库的操作，增删改查
 * 2.参照postMan操作指令和JavaAPI操作进行对比
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class IndexCreateTest {

        //注入Elastic Search操作对象
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 测试索引库的创建：操作的方法
     * 1.postman:
     *              http://localhost:9200/xc_test_postman
     *              参数：{
                         "settings":{
                         "index":{
                         "number_of_shards":1,
                         "number_of_replicas":0
                         }
                         }
                         }
     */
    @Test//测试创建索引库
    public  void  IndexCreateTest(){
        //1.创建创建索引库对象并指定索引库名称：xc_test_Java
        CreateIndexRequest  createIndexRequest  =new CreateIndexRequest("xc_test_java");//此处的名字必须小写，不能大写
        //2.创建Setting.Builder对象
        Settings.Builder builder = Settings.builder();
        //3.初始化参数信息--分片--副本等等
        builder.put("number_of_shards", 1);//分片
        builder.put("number_of_replicas",0);//副本
        //4.将配置信息置于创建索引库请求对象中
        createIndexRequest.settings(builder);
        //5.利用操作ES的操作对象进行执行
        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest);
            boolean acknowledged = createIndexResponse.isShardsAcknowledged();
            System.err.println("acknowledged："+acknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *测试对于索引库创建映射----其实本质还是进行创建索引库时候指定对应的映射，
     *              此处也证明了不可修改的特性
     *  1.postman:
     *              http://localhost:9200/xc_test_postman/doc/_mapping
     *    参数：
     *                                 { "properties": {
                                             "description": {
                                             "type": "text",
                                             "analyzer": "ik_max_word",
                                             "search_analyzer": "ik_smart"
                                             },
                                             "name": {
                                             "type": "text",
                                             "analyzer": "ik_max_word",
                                             "search_analyzer": "ik_smart"
                                             },
                                             "price": {
                                             "type": "float"
                                             },
                                             "studymodel": {
                                             "type": "keyword"
                                             }
                                             }
                                             }
     */
    @Test//测试创建映射
    public  void  MappingCreateTest(){
        //1.创建创建索引库对象并指定索引库名称：xc_test_Java,此处进行创建的时候注意是如果已经创建过xc_test_Java，指定映射会报错
        //因此此处修改为xc_test_java_mapping与上述的测试的xc_test_Java无关
        CreateIndexRequest  createIndexRequest  =new CreateIndexRequest("xc_test_java_mapping");//此处的名字必须小写，不能大写
        //2.创建Setting.Builder对象
        Settings.Builder builder = Settings.builder();
        //3.初始化参数信息--分片--副本等等
        builder.put("number_of_shards", 1);//分片
        builder.put("number_of_replicas",0);//副本
        //4.将配置信息置于创建索引库请求对象中
        createIndexRequest.settings(builder);

        //4.1进行创建索引库的映射字段
        //注意此处是直接利用createIndexRequest对象进行创建映射，另外需要注意的是：此处的三个参数：
        //第一个参数是指定类型（Type就是要被废除的那个Type）
        //第二个参数是映射的JSON数据字段（目前是字符串）
        //第三个参数是指定传递的数据第第二个参数的数据类型是JSON格式。
        createIndexRequest.mapping("doc","               { \"properties\": {\n" +
                "                    \"description\": {\n" +
                "                        \"type\": \"text\",\n" +
                "                        \"analyzer\": \"ik_max_word\",\n" +
                "                        \"search_analyzer\": \"ik_smart\"\n" +
                "                    },\n" +
                "                    \"name\": {\n" +
                "                        \"type\": \"text\",\n" +
                "                        \"analyzer\": \"ik_max_word\",\n" +
                "                        \"search_analyzer\": \"ik_smart\"\n" +
                "                    },\n" +
                "                    \"price\": {\n" +
                "                        \"type\": \"float\"\n" +
                "                    },\n" +
                "                    \"studymodel\": {\n" +
                "                        \"type\": \"keyword\"\n" +
                "                    }\n" +
                "                }\n" +
                "                }", XContentType.JSON);
        //5.利用操作ES的操作对象进行执行
        try {
            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest);
            boolean acknowledged = createIndexResponse.isShardsAcknowledged();
            System.err.println("acknowledged："+acknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  /**
     *测试对于索引库删除索引库
     */
    @Test//测试删除
    public  void  DeleteIndexTest(){
        //1.创建删除索引库请求对象并指定索引库名称
        DeleteIndexRequest  deleteIndexRequest  =new DeleteIndexRequest("xc_test_java_mapping");//此处的名字必须小写，不能大写
        //2.利用操作ES的操作对象进行执行
        try {
            DeleteIndexResponse deleteIndexResponse = restHighLevelClient.indices().delete(deleteIndexRequest);
            boolean acknowledged = deleteIndexResponse.isAcknowledged();
            System.err.println("acknowledged："+acknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //测试完毕关于索引库的操作，下面进行关于索引库中文档的操作将会在下一个测试类中进行完成。




}
