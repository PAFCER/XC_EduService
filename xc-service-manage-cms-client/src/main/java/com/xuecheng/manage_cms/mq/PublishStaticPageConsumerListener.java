package com.xuecheng.manage_cms.mq;

import com.alibaba.fastjson.JSON;
import com.mongodb.gridfs.GridFSDBFile;
import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

/**
 * Created by hotwater on 2018/6/30.
 */
@Component
public class PublishStaticPageConsumerListener {
    //日志处理
    private  final Logger  logger  = LoggerFactory.getLogger(CmsPage.class);

    @Value("${xuecheng.mq.queue}")
    private  String CMS_Queue_Portal;

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    /**
     大致流程是：服务器发送队列消息，采用路由模式，
     需要队列绑定路由
     *
     */

    @RabbitListener(queues = {
            "${xuecheng.mq.queue}"
    })
    public  void  consumerListener(String msg, Message message, Channel channel){

        //1.获取数据进行转换为Map
        String pageId = null;
        try {
            Map map = JSON.parseObject(msg, Map.class);
            pageId = map.get("pageId").toString();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("转换消息队列数据异常，{}",e.getMessage());
        }
        CmsPage cmsPage = null;
        String htmlFileId = null;

        try {
            //2.根据pageId进行查询cmspage
            cmsPage = cmsPageRepository.findOne(pageId);
            //3.拿到文件id
            htmlFileId = cmsPage.getHtmlFileId();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("根据pageId查询mongodb数据异常，{}",e.getMessage());
        }
        InputStream inputStream = null;

        try {
            //4.获取文件对象
            GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
            //5.拿到文件流
            inputStream = gridFSDBFile.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("根据htmlFileId查询GridFS数据异常，{}",e.getMessage());
        }

        ///6.根据上述的cmsPage对象进行组装物理路径---即静态化页面存储路径

        //原来轮播图的测试路径，现在已经修改规则
//        String filePath = cmsPage.getPageWebPath().concat(cmsPage.getPagePhysicalPath()).concat(cmsPage.getPageName());
        String filePath = cmsPage.getPagePhysicalPath().concat(cmsPage.getPageName());

        //7.根据上述的路径进行文件的处理
        FileOutputStream  outputStream  =null;
        try {
            outputStream  = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("根据filePath不存在异常，{}",e.getMessage());
        }
        //8.进行文件的复制
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("根据进行静态化发布数据的时候出现了异常，{}",e.getMessage());
        }
    }



}
