package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsConfigControllerAPI;
import com.xuecheng.framework.domain.cms.response.CmsConfigResult;
import com.xuecheng.manage_cms.service.CmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/6/28.
 * 此处的远程调用接口的实现控制器，本身应该是在另外i一个项目中，但是此时却是在同一个项目中
 * 是不合适的是不合理的，但是只是模拟而已，不必当真。
 */
@RestController
public class CmsConfigController implements CmsConfigControllerAPI {
    @Autowired
    private CmsConfigService  cmsConfigService;

    @Override
    public CmsConfigResult getDataModelByRemoteDataUrl(@PathVariable("id") String id) {
        return cmsConfigService.findById(id);
    }

}
