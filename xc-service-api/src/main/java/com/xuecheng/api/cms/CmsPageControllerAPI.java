package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPublishResult;
import com.xuecheng.framework.domain.cms.response.GenerateHtmlResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hotwater on 2018/6/23.
 * 接口
 */
@Api(value = "CMS管理接口",description = "cms接口提供增删改查的方法")
public interface CmsPageControllerAPI {

    //前缀
    final  String  CMS_PRE="/cms/page";
    /**
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    @ApiOperation(value = "cms分页查询数据列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",required = true,dataType="Integer",value = "页码，起始索引0",paramType ="path" ),
            @ApiImplicitParam(name = "size",required = true,dataType="Integer",value = "页面大小值",paramType ="path" )
        }
    )
    @GetMapping(CMS_PRE+"/list/{page}/{size}")
   public  QueryResponseResult  findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size, QueryPageRequest  queryPageRequest);

    /**
     * 追加代码实现对于目前的页面的增删改查
     * 16点41分2018年6月26日
     */
    /**
     * 添加代码实现根据页面id进行查询页面信息
     * @param id
     * @return
     */
    @ApiOperation(value="CMS根据页面id查询页面信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name="id",required = true,dataType = "Integer",value = "查询页面的条件id",paramType = "path")
            }
    )
    @GetMapping(CMS_PRE+"/findById/{id}")
    public   CmsPageResult  findById(@PathVariable("id") String  id);

    /**
     * 关于实现对于页面数据对象的更新
     * @param id
     * @param cmsPage
     * @return
     */
    @ApiOperation(value = "CMS根据页面id和商品的信息进行更新页面信息")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "id",required = true,value = "进行更新前查询的id",dataType = "Integer",paramType = "path"),
                    @ApiImplicitParam(name="cmsPage",required = true,value = "页面进行数据更新的实体数据封装对象",dataTypeClass = CmsPage.class,paramType = "path")
            }
    )
    @PutMapping(CMS_PRE+"/edit/{id}")
    public  ResponseResult  edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage);

    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    @PostMapping(CMS_PRE+"/add")
    @ApiOperation("添加页面")
    public CmsPageResult add(@RequestBody CmsPage cmsPage);

    //查询关于站点的列表
    @ApiOperation(value = "查询站点数据下拉")
    @GetMapping(CMS_PRE+"/loadSiteList")
    public  QueryResponseResult  loadSiteList();

    //查询关于模板的列表
    @ApiOperation(value = "查询模板数据下拉")
    @GetMapping(CMS_PRE+"/loadTemplateList")
    public  QueryResponseResult  loadTemplateList();

    @ApiOperation(value = "cms根据id删除对应的页面信息")
    @GetMapping(CMS_PRE+"/deleteById/{id}")
    public  ResponseResult  delete(@PathVariable("id")  String id);


    @ApiOperation("CMS提供接口，根据pageId查询预备静态化页面")
    @GetMapping(CMS_PRE+"/gerenateHTML/{id}")
    public GenerateHtmlResult generateHTML(@PathVariable("id") String id);

  @ApiOperation("CMS提供接口，VUE初始化时根据pageId查询静态化页面，此时可有可无")
    @GetMapping(CMS_PRE+"/loadHTML/{id}")
    public GenerateHtmlResult loadHTML(@PathVariable("id") String id);

     @ApiOperation("CMS提供接口，发布静态化页面到服务器")
    @GetMapping(CMS_PRE+"/publishPage/{id}")
    public ResponseResult publishPage(@PathVariable("id") String id);


    /**预览功能模板涉及到的方法：
     * 添加页面----远程调用
     * @param cmsPage
     * @return
     */
    @PostMapping(CMS_PRE+"/save")
    @ApiOperation("添加页面,此处的添加页面是用于被其他服务系统如课程管理系统远程调用使用")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);

    /**
     * CMS系统提供的一键发布的接口
     * @param cmsPage
     * @return
     */
    @ApiOperation("CMS系统提供的通用的，课程一键发布的接口")
    @PostMapping(CMS_PRE+"/OneKeyQuickPublish")
    public CmsPublishResult OneKeyQuickPublish(@RequestBody CmsPage  cmsPage);


}
