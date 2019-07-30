package com.xuecheng.search.test;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
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
public class InitDataForDev {


    @Autowired
    private RestHighLevelClient  restHighLevelClient;


    /**
     * 完成创建索引库和字段映射，为辅助后续学习的前提
     */
    @Test
    public  void  test(){
        //1.创建索引库操作
        CreateIndexRequest  createIndexRequest = new CreateIndexRequest("xc_course");
        //2.创建初始化索引库的信息即设置配置信息
        Settings.Builder builder = Settings.builder();
        builder.put("number_of_shards",1).
                put("number_of_replicas",0);
        createIndexRequest.settings(builder);
        //3.指定映射字段   类型     元素据，数据格式
        createIndexRequest.mapping("doc","{\n" +
                "\t\"properties\": {\n" +
                "\t\t\"description\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\"search_analyzer\": \"ik_smart\"\n" +
                "\t\t},\n" +
                "\t\t\"name\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\"search_analyzer\": \"ik_smart\"\n" +
                "\t\t},\n" +
                "\t\t\"pic\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"index\": false\n" +
                "\t\t},\n" +
                "\t\t\"price\": {\n" +
                "\t\t\t\"type\": \"float\"\n" +
                "\t\t},\n" +
                "\t\t\"studymodel\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"timestamp\": {\n" +
                "\t\t\t\"type\": \"date\",\n" +
                "\t\t\t\"format\": \"yyyy‐MM‐dd HH:mm:ss||yyyy‐MM‐dd||epoch_millis\"\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}", XContentType.JSON);

        try {
            CreateIndexResponse createIndexResponse =
                    restHighLevelClient.indices().create(createIndexRequest);
            boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
            System.err.println("shardsAcknowledged:"+shardsAcknowledged);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Test//准备数据
    public  void  addDoc(){

        IndexRequest  indexRequest  = new IndexRequest("xc_course","doc","1");
        Map<String,Object> source= new HashMap<>();
        source.put("","");
        indexRequest.source(source);




    }






}
