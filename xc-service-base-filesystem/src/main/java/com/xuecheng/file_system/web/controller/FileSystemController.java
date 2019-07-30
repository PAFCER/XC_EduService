package com.xuecheng.file_system.web.controller;

import com.xuecheng.api.filesystem.FileSystemControllerAPI;
import com.xuecheng.file_system.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by hotwater on 2018/7/4.
 */
@RestController
public class FileSystemController implements FileSystemControllerAPI{

    @Autowired
    private FileSystemService  fileSystemService;

    /**上传文件到fastdfs服务器
     * @param file        上传文件
     * @param businesskey 业务key
     * @param filetag     业务标签
     * @param metadata    文件元信息
     * @return
     */
    @Override

    public UploadFileResult uploadFile(@RequestParam(value = "file",required = true) MultipartFile  file ,
                                       @RequestParam(value = "businesskey",required = false) String  businesskey,
                                       @RequestParam(value = "filetag",required = false) String filetag,
                                       @RequestParam(value = "metadata",required = false) String metadata) {


        return fileSystemService.uploadFile(file,businesskey,filetag,metadata);
    }
}
