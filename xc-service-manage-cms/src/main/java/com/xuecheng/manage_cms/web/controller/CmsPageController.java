package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerAPI;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPublishResult;
import com.xuecheng.framework.domain.cms.response.GenerateHtmlResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.CmsManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/6/23.
 */
@RestController
public class CmsPageController  implements CmsPageControllerAPI {

    @Autowired
    private CmsManageService  cmsManageService;

    /**
     http://localhost:31001/cms/page/list/0/10?siteId=5a754adf6abb500ad05688d9&pageId=5a751fab6abb5044e0d19ea1
     发送如上述的请求的确实现了对于数据的封装，也就是说颠覆了我们的传统思维，此处进行数据的get传递的时候，实现了对于数据的对于对象的封装。
     切记切记
     */

    @Override
      public QueryResponseResult findAll(@PathVariable("page") Integer  page, @PathVariable("size") Integer size, QueryPageRequest queryPageRequest) {

        return cmsManageService.findAll(page, size, queryPageRequest);
    }

    @Override
    public CmsPageResult findById(@PathVariable("id") String id) {
        return cmsManageService.findById(id);
    }

    @Override
    public ResponseResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        return cmsManageService.update(id,cmsPage);
    }



    @Override
    public CmsPageResult add(@RequestBody  CmsPage cmsPage) {
        return cmsManageService.add(cmsPage);
    }

    @Override
    public ResponseResult delete(@PathVariable("id") String id) {
        System.out.println("id："+id);
        return cmsManageService.deleteById(id);
    }

    @Override
    public GenerateHtmlResult generateHTML(@PathVariable("id") String id) {


        return cmsManageService.generateHTML(id);
    }

    @Override
    public GenerateHtmlResult loadHTML(@PathVariable("id") String id) {

        return cmsManageService.loadHTML(id);
    }

    @Override
    public QueryResponseResult loadSiteList() {
        return cmsManageService.loadSiteList();
    }

    @Override
    public QueryResponseResult loadTemplateList() {
        return cmsManageService.loadTemplateList();
    }

    /**
     * 发布静态页面
     * @param id
     * @return
     */
    @Override
    public ResponseResult publishPage(@PathVariable("id") String id) {
        return cmsManageService.publishPage(id);
    }

    /**
     * 添加页面----远程调用
     *
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return cmsManageService.save(cmsPage);
    }

    /**
     * CMS系统提供的一键发布的接口
     *
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPublishResult OneKeyQuickPublish(@RequestBody CmsPage cmsPage) {
        /**
         * 此处主要思路：
         */

        return cmsManageService.OneKeyQuickPublish(cmsPage);
    }
}
