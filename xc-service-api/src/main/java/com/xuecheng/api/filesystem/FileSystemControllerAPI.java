package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by hotwater on 2018/7/4.
 */
@Api(value="基础文件管理服务系统接口",description = "提供对于文件上传下载删除查询等功能")
public interface FileSystemControllerAPI {
      final String   FS_PRE="/filesystem";
    /**
     *
     * @param file  上传文件
     * @param businesskey  业务key
     * @param filetag   业务标签
     * @param metadata  文件元信息
     * @return
     */
    @ApiOperation("上传文件功能接口")
    @PostMapping(FS_PRE+"/upload/")
    public UploadFileResult uploadFile(@RequestParam(value = "file",required = true) MultipartFile  file ,
                                       @RequestParam(value = "businesskey",required = false) String  businesskey,
                                       @RequestParam(value = "filetag",required = false) String filetag,
                                       @RequestParam(value = "metadata",required = false) String metadata);


}
