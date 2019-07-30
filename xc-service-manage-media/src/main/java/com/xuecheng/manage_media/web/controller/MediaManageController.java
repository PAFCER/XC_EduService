package com.xuecheng.manage_media.web.controller;

import com.xuecheng.api.media.MediaManageControllerAPI;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.service.MediaManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/13.
 */

@RestController
public class MediaManageController  implements MediaManageControllerAPI {


    @Autowired
    MediaManageService mediaManageService;
    @Override
    public QueryResponseResult findAll(@PathVariable("page") Integer page,
                                       @PathVariable("size")Integer size,
                                       QueryMediaFileRequest queryMediaFileRequest) {

        return mediaManageService.findAll(page,size,queryMediaFileRequest);
    }

    /**
     *此处根据id发送MQ让其再次执行
     * @param id
     * @return
     */
    @Override
    public ResponseResult manualOperation(@PathVariable("id") String id) {

        return mediaManageService.manualOperation(id);
    }
}
