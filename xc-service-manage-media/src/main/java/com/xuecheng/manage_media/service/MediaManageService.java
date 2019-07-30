package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/13.
 */
@Service
public class MediaManageService {

    @Autowired
    MediaFileRepository  mediaFileRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //定义视频处理的队列
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    public String QUEUE_MEDIA_VIDEO;
    //定义视频处理路由
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String ROUTING_MEDIA_VIDEO;

    /**
     * 查询媒体资源列表--兼容分页模糊查询
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    public QueryResponseResult findAll(Integer page, Integer size, QueryMediaFileRequest queryMediaFileRequest) {

        if(page<=0){
            page=1;
        }
        page=page-1;
        if(size<=0){
            size=12;
        }
        MediaFile  mediaFile= new MediaFile();

        if(queryMediaFileRequest==null){
            queryMediaFileRequest= new QueryMediaFileRequest();
        }
        //原文件名称
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        //资源处理状态
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //设置标记
        if(StringUtils.isNotEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        //设置匹配条件
        //标签模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<MediaFile> fileExample = Example.of(mediaFile, exampleMatcher);

        Pageable pageable =new PageRequest(page,size);
        Page<MediaFile> results = mediaFileRepository.findAll(fileExample, pageable);
        //查询总记录数
        long total= results.getTotalElements();
        //查询数据列表
        List<MediaFile> fileList = results.getContent();
        QueryResult  queryResult = new QueryResult();
        queryResult.setList(fileList);
        queryResult.setTotal(total);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    /**
     *发送消息队列
     * @param id
     * @return
     */
    public ResponseResult manualOperation(String id){
        //准备数据
        if(StringUtils.isEmpty(id)){
            return new ResponseResult(CommonCode.FAIL);
        }
        MediaFile mediaFile = mediaFileRepository.findOne(id);
        if(mediaFile==null){
            return new ResponseResult(CommonCode.FAIL);
        }
        try {
            Map<String,String> map=new HashMap<>();
            map.put("mediaId",id);
            String msg = JSON.toJSONString(map);
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.Exchange_MEDIA_PROCESSOR,ROUTING_MEDIA_VIDEO,msg);
            return new ResponseResult(CommonCode.SUCCESS);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }



}
