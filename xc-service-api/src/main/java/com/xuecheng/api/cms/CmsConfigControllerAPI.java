package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.response.CmsConfigResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by hotwater on 2018/6/23.
 * 接口
 */
@Api(value = "CMS远程调用接口",description = "cms接口提供轮播图，精品推荐等等资源的增删改查方法")
public interface CmsConfigControllerAPI {

    //前缀
    final  String  CMS_PRE="/cms/config";

    /**
    考虑一下，首先需要暴露的接口具有
            根据dataUrl获取数据模型，此处的url在cms_page中存储的有该路径，并且restful风格的数据进行传递id
            根据文件id获取对应的数据模板
     */

    @ApiOperation("提供暴露的端口供外部调用使用依据dataUrl中的id查询数据模型")
    @GetMapping(CMS_PRE+"/getDataModelByDataUrl/{id}")
    public CmsConfigResult getDataModelByRemoteDataUrl(@PathVariable ("id") String id);


}
