package com.xuecheng.manage_media.web.controller;

import com.xuecheng.api.media.MediaUploadControllerAPI;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by hotwater on 2018/7/12.
 */
@RestController
public  class MediaUploadController   implements MediaUploadControllerAPI {

    @Autowired
    MediaUploadService  mediaUploadService;
    /**
     * 上传文件第一个方法注册即检测文件
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    @Override
    public ResponseResult register(@RequestParam("fileMd5")String fileMd5,//文件唯一标识
                                   @RequestParam("fileName")String fileName,//文件名称
                                   @RequestParam("fileSize")Long fileSize,//文件大小
                                   @RequestParam("mimetype")String mimetype,//文件类型
                                   @RequestParam("fileExt")String fileExt//文件扩展名
                                    ) {
//        1.上传前的注册：检测是否已经存在该文件（比对数据库及其服务器磁盘），此处的文件md5是数据库的记录id
//                如果已经存在该文件则告知文件已经存在,此处利用返回对象参数success进行判定成功与否
//        检测存储目录是否存在，不存在则创建
        return mediaUploadService.register(fileMd5, fileName, fileSize, mimetype, fileExt);
    }

    /**
     * 上传分块前的检测分块信息是否存在
     *
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    @Override
    public CheckChunkResult checkchunk(
            @RequestParam("fileMd5") String fileMd5,//文件唯一标识
            @RequestParam("chunk") Long chunk,//文件块下标
            @RequestParam("chunkSize") Long chunkSize//文件块大小
                           ) {
        return mediaUploadService.checnchunk(fileMd5,chunk,chunkSize);
    }

    /**
     * 上传分块文件
     *
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    @Override
    public ResponseResult uploadchunk(
            @RequestParam("file")MultipartFile file,
            @RequestParam("fileMd5")String fileMd5,
            @RequestParam("chunk")Long chunk
                                       ) {
        return mediaUploadService.uploadchunk(file,fileMd5,chunk);
    }

    @Override
    public ResponseResult mergechunks(
            @RequestParam("fileMd5")String fileMd5,//文件唯一标识
            @RequestParam("fileName")String fileName,//文件名称
            @RequestParam("fileSize")Long fileSize,//文件大小
            @RequestParam("mimetype")String mimetype,//文件类型
            @RequestParam("fileExt")String fileExt//文件扩展名
    ) {
        return mediaUploadService.mergechunks(fileMd5,fileName,fileSize,mimetype,fileExt);
    }
}
