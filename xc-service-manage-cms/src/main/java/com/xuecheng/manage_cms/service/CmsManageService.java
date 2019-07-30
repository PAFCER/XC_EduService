package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPublishResult;
import com.xuecheng.framework.domain.cms.response.GenerateHtmlResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.CMSRabbitConfig;
import com.xuecheng.manage_cms.dao.CmsManageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hotwater on 2018/6/23.
 */
@Service
public class CmsManageService {

    @Autowired
    private CmsManageRepository cmsManageRepository;

    @Autowired
    private CmsTemplateRepository  cmsTemplateRepository;
    //注入一个restTemplate
    @Autowired
    private RestTemplate  restTemplate;

    @Autowired
    private CmsSiteRepository  cmsSiteRepository;

    //访问对应的gridFS存储系统
    @Autowired
    private GridFsTemplate  gridFsTemplate;



    /**
     * 模糊分页查询
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    public QueryResponseResult findAll(int page, int size, QueryPageRequest  queryPageRequest){

        if(queryPageRequest==null){
            queryPageRequest=new QueryPageRequest();
        }

        /**
         * 在此处组装数数据，处理业务逻辑
         * 此处的模糊查询什么都没有做，等到后续需要
         */
        if(page<=0){
            page=1;
        }
        page=page-1;//由于前端页面的特性需要进行页面处理，页面上的起始页面是1，后台的页面起始是0，因此需要特殊处理
        ExampleMatcher  exampleMatcher = ExampleMatcher.matching();
        CmsPage  cmsPage  = new CmsPage();
         //cmsPage.setSiteId("5ad99fb768db523ef42cd02d");
       //cmsPage.setPageAliase("分类");
       //进行条件判定
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
           cmsPage.setPageAliase(queryPageRequest.getPageAliase());
       }
       if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
       }
        exampleMatcher=exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<CmsPage> example= Example.of(cmsPage,exampleMatcher);
        Pageable  pageable  = new PageRequest(page,size);
        Page<CmsPage> all = cmsManageRepository.findAll(example, pageable);
        // List<CmsPage>all=cmsManageRepository.findAll();
      //  long total=all.getTotalElements();
       // List<CmsPage> list= all.getContent();
        QueryResult queryResult=new QueryResult<CmsPage>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    /**
     * 添加cmspage页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage  cmsPage){
        //添加之前需要进行先校验是否存在
        //校验是否空引用
        if(cmsPage==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }

        CmsPage cmsPage1=  cmsManageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath());
        if(cmsPage1==null){//不存在
            cmsPage.setPageId(null);//设置为null防止脏数据
         CmsPage save =cmsManageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS,save);
        }else{
            return new CmsPageResult(CommonCode.Page_Exist,null);
        }
    }

    /**
     * 根据id查询页面数据--
     * @param id
     * @return
     */
    public  CmsPageResult  findById(String id){
        if(StringUtils.isEmpty(id)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CmsPage one = cmsManageRepository.findOne(id);
        if(one==null){
            return  new CmsPageResult(CommonCode.FAIL,null);
        }else{
            return  new CmsPageResult(CommonCode.SUCCESS,one);
        }
    }

    /**
     * 更新页面信息，首先需要明确一点，更新前需要根据id进行查询出数据
     * @param id
     * @param cmsPage
     * @return
     */
    public  CmsPageResult  update(String id,CmsPage  cmsPage){
        //id值为空
        if(StringUtils.isEmpty(id)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CmsPage one = cmsManageRepository.findOne(id);

        //异常处理
        if(one==null){
            //异常处理
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_NoExist));
        }

        if(cmsPage==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }


        //根据id查询出来对象，再根据cmsPage进行更新
        //更新模板id
        one.setTemplateId(cmsPage.getTemplateId());
        //更新站点id
        one.setSiteId(cmsPage.getSiteId());
        //更新页面别名
        one.setPageAliase(cmsPage.getPageAliase());
        //更新数据来源url请求路径
        one.setDataUrl(cmsPage.getDataUrl());
        //更新页面的名称
        one.setPageName(cmsPage.getPageName());
        //更新页面的访问路径
        one.setPageWebPath(cmsPage.getPageWebPath());
        //更新页面的物理路径
        one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
        //执行更新完成之后返回更新后的对象
        CmsPage save = cmsManageRepository.save(one);
        if(save!=null){
            //更新成功则返回正常处理结果
            return  new CmsPageResult(CommonCode.SUCCESS,save);
        }
        //更新失败则返回异常
        return  new CmsPageResult(CommonCode.FAIL,null);
    }


    public ResponseResult  deleteById(String  id){
        if(StringUtils.isEmpty(id)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CmsPage one = cmsManageRepository.findOne(id);
        if(one!=null){
            cmsManageRepository.delete(one);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return  new ResponseResult(CommonCode.FAIL);

    }

    /**
     *      此处页面静态化的逻辑是：
     *                  首先根据页面id即pageId进行查询出来页面的数据
     *                      其中包含站点id即该页面对应的网站的id，模板id，数据来源地址等等信息
     *                      实现页面静态化需要几个步骤：静态化页面=数据+模板
     *                      第一步我们需要根据dataUrl进行远程拉取数据模型
     *                      第二步我们需要通过模板id拉去mongodb数据库的模板数据
     *                      第三步是我们需要通过将上述的数据模型和静态化模板利用静态化技术free marker进行静态化
     *                      第四步是我们需要通过将静态化的结果反馈给前端页面即可。
     *               实现对于页面静态化的梳理，有利于理解程序，理解系统代码实现。
     *               ===============================================================================
     *               此处由于目前需要实现对于页面的课程管理的预览功能和发布功能的实现，教学系统上面我们可以实现对于页面的预览功能，
     *               预览功能：教学系统（前端）--发送预览请求传递课程id-->课程管理系统（后台）--根据课程id准备数据组装一个cmsPage页面对象,请求CMS系统-->CMS系统（后台）
     *                          ---进行页面生成，生成cmsPage--->返回一个生成的页面id进行返回给调用者（课程管理系统）------课程管理系统生成预览页面URL反馈给前端教学系统
     *                          ----前端教学系统利用该
     *                      既然预览首先需要进行数据的cmsPage的生成， 生成一个cmsPage就需要数据填充，
     *
     *                            1. 一个课程的数据涉及到几大块，课程计划，课程基本信息，课程营销等数据的组装，
     *                              将数据组装成为一个cmsPage进行远程调用保存到cms系统的接口实现对于cmsPage的保存，
     *                              另外我们保存之后，需要利用静态化技术将数据模型和页面模板进行整理，实现对于静态化的操作
     *                              然后将静态化页面进行存储到预览的服务器目录下，最终返回一个预览URL进行，然后我们进行展示即可
     *
     * @param id 即pageId
     * @return
     */
    public GenerateHtmlResult generateHTML(String id) {
        //此处根据页面id查询出来对应的数据
        if(StringUtils.isEmpty(id)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CmsPage  one=cmsManageRepository.findOne(id);
        if(one==null){
            return null;//如果不存在就返回null
        }
     String  templateIntoString=this.getTemplateString(one,id);

    //生成静态文件之后将页面输出保存到GridFS中
        GridFSFile file =null;
      try {
           file = gridFsTemplate.store(IOUtils.toInputStream(templateIntoString, "utf-8"), "轮播图静态化页面");
      }catch(Exception e){
          ExceptionCastUtils.throwException(new CustomException(CommonCode.StoreNewTemplate_ERROR));
      }
        Object fileId = file.getId();//拿到文件id
        //将文件id保存到cms_page中
        one.setHtmlFileId(fileId+"");
        //更新cmsPage表的HTMLFileId数据
        CmsPage save = cmsManageRepository.save(one);
        if(save==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.updateCMSPAGE_HTMLFILEID_ERROR));
        }
        //返回页面信息
        return new GenerateHtmlResult(CommonCode.SUCCESS,templateIntoString);
    }

    /**
     * 根据页面id进行抽取数据模型和模板类型进行组装静态化数据----
     * @param id
     * @return   静态化后的流数据
     */
    private String getTemplateString(CmsPage  one,String id) {

        String dataUrl=  one.getDataUrl();

        //拿到dataUrl进行远程调用----此处是利用OK HTTP实现远程调用
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        //轮播图测试用
        //Map<String,Object>banner=new HashMap<>();
        //banner.put("banner",body);
        //测试预览页面测试用----此处注意细则：到底是哪一个Map（数据模型）和模板流（）放入执行静态化函数的参数。
        Map<String,Object>map=new HashMap<>();
        map.put("model",body);
        //得到模板id，首先查询模板表，查询处理啊其中的模板对应的文件id，然后查询
        //对应的GridFS进行查询对应的模板数据
        String templateId=one.getTemplateId();
        CmsTemplate cmsTemplate =
                cmsTemplateRepository.findOne(templateId);
        String templateFileId = cmsTemplate.getTemplateFileId();
        //根据文件id查询GridFS得到模板
        GridFSDBFile fsdbFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        //得到对应的流对象
        InputStream inputStream = fsdbFile.getInputStream();
        //此处流对象就是模板，另外上述的数据已经住呢比完毕，下面就是准备进行静态化
        String content =null;
        try {
            content = IOUtils.toString(inputStream, "utf-8");
        }catch (Exception  e){

            ExceptionCastUtils.throwException(new CustomException(CommonCode.TemplateContent_null));

        }
        //进行页面初始化
        Configuration configuration = new Configuration(Configuration.getVersion());
        StringTemplateLoader  loader =new StringTemplateLoader();
        loader.putTemplate("bannerTemplate",content);

        configuration.setTemplateLoader(loader);
        String templateIntoString =null;
        try {
            Template bannerTemplate = configuration.getTemplate("bannerTemplate", "utf-8");
            templateIntoString = FreeMarkerTemplateUtils.processTemplateIntoString(bannerTemplate, map);
        }catch (Exception  e){
            e.printStackTrace();
            ExceptionCastUtils.throwException(new CustomException(CommonCode.GenerateHTML_Error));
        }

        return  templateIntoString;
    }

    /**
     * VUE初始化时候加载静态页面资源---不保证有
     * @param id
     * @return
     */
    public  GenerateHtmlResult  loadHTML(String id ){

        if(StringUtils.isEmpty(id)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CmsPage one = cmsManageRepository.findOne(id);
        if(one==null){
            return  new GenerateHtmlResult(CommonCode.FAIL,null);
        }
        //获取文件id
        String htmlFileId = one.getHtmlFileId();
        //不为空才能查询
        String html=null;
        if(StringUtils.isNotEmpty(htmlFileId)){

            GridFSDBFile htmlFileId1 = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(htmlFileId)));
            InputStream inputStream = htmlFileId1.getInputStream();
            try {
                html= IOUtils.toString(inputStream, "utf-8");
                //成功返回
                return   new GenerateHtmlResult(CommonCode.SUCCESS,html);

            }catch (Exception e){
                ExceptionCastUtils.throwException(new CustomException(CommonCode.loadHTML_ERROR));
            }
        }
        return  new GenerateHtmlResult(CommonCode.FAIL,null);

    }

    /**
     * 加载全部的站点数据
     * @return
     */
        public  QueryResponseResult  loadSiteList(){

            try {
                List<CmsSite> list = cmsSiteRepository.findAll();
                QueryResult<CmsSite> queryResult = new QueryResult<>();
                queryResult.setList(list);
                queryResult.setTotal(list.size());
                return  new QueryResponseResult(CommonCode.SUCCESS,queryResult);
            }catch (Exception e){
                ExceptionCastUtils.throwException(new CustomException(CommonCode.loadSiteList_ERROR));
            }
            //此处即使没有数据也要进行创建一个对象进行返回，因为房子出现空指针异常
            return  new QueryResponseResult(CommonCode.FAIL,new QueryResult());
        }

    /**
     * 加载所有的模板列表
     * @return
     */
    public QueryResponseResult loadTemplateList() {
        try {
            List<CmsTemplate> list = cmsTemplateRepository.findAll();
            QueryResult<CmsTemplate> queryResult = new QueryResult<>();
            queryResult.setList(list);
            queryResult.setTotal(list.size());
            return  new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        }catch (Exception e){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.loadTemplateList_ERROR));
        }
        //此处即使没有数据也要进行创建一个对象进行返回，因为房子出现空指针异常
        return  new QueryResponseResult(CommonCode.FAIL,new QueryResult());


    }

    @Autowired
    private RabbitTemplate  rabbitTemplate;
    private final Logger  logger= LoggerFactory.getLogger(CmsPage.class);
    /**
     * 发布静态页面到服务器
     * @param id
     * @return
     */
    public ResponseResult publishPage(String id) {

        try {

            //1.首先执行页面静态化之后
            try {
                this.generateHTML(id);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("将页面发布服务器时候的静态化操作，{}",e.getMessage());
            }

            //2.进行查询
            CmsPage cmsPage = cmsManageRepository.findOne(id);
            //3.获取站点id即routingKey
            String routingKey = cmsPage.getSiteId();

            //4.准备数据
            String message = null;
            try {
                Map<String,String>map= new HashMap<>();
                map.put("pageId",cmsPage.getPageId());
                message = JSON.toJSONString(map);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("组装消息队列数据转json出现异常,{}",e.getMessage());
            }
            // 5.发送消息队列
            rabbitTemplate.convertAndSend(CMSRabbitConfig.ExChange_Routing_Portal,routingKey,message);
            return  new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return  new ResponseResult(CommonCode.FAIL);
        }

    }

    /**
     *
     * 远程调用-----保存页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage  cmsPage){
        //添加之前需要进行先校验是否存在
        //校验是否空引用
        if(cmsPage==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        //如果已经存在则进行修改即可，否则进行新保存
        CmsPage cmsPage1=  cmsManageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),cmsPage.getSiteId(),cmsPage.getPageWebPath());
        if(cmsPage1==null){//不存在
            //本地调用添加即可
         return this.add(cmsPage);
        }else{
            //本地调用更新即可
            return   this.update(cmsPage1.getPageId(),cmsPage);
        }
    }


    /**
    * 此处追加代码是实现对于页面预览返回生成的静态文件流对象的方法
     *与上述本服务层的函数有类似的函数但是主要是此处不保存页面数据库
     */
     public  String   getHTMLAfterStatic(String id){
         //此处根据页面id查询出来对应的数据
         if(StringUtils.isEmpty(id)){
             ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
         }
         CmsPage  one=cmsManageRepository.findOne(id);
         if(one==null){
             return null;//如果不存在就返回null
         }
         String templateString = this.getTemplateString(one, id);
       return  templateString;
     }

    /**
     * 页面一键发布的业务层
     * @param cmsPage
     * @return
     */
    public CmsPublishResult OneKeyQuickPublish(CmsPage cmsPage) {
        /**
         * 此处主要业务逻辑是：
         *      1.完成对于页面的保存操作（新增或者更新）
         *      2.根据上述的页面id进行抽取远程数据（利用OKHTTP），拉取MongoDB模板，组装静态化的两大组件即数据模型和模板
         *         完成静态化操作，及其对于静态化后的页面的保存操作（mongoDB），随机发送MQ消息队列告知消费端发起消费执行页面发布
         *         此处的消费端一般在当前请求一键发布功能的服务器系统的调用
         *      3.上述操作都完成之后就开始组装访问地址
         *      4.将上述组装的访问地址返回页面即可实现。
         *      5.整体来讲业务不复杂但是接口调用需要清楚明白，捋顺思路是最关键的任务，完成后20K不是梦想。剑指
         */
        //0.基本校验
        if(cmsPage==null){
//            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            return  new CmsPublishResult(CommonCode.FAIL,null);
        }
        //1.进行页面保存
        CmsPageResult save = this.save(cmsPage);
        //如果校验失败则返回
        if(!(save!=null&&save.isSuccess())){
            return  new CmsPublishResult(CommonCode.FAIL,null);
        }
        //校验成功,获取最新的cmsPage
        CmsPage cmsPage1 = save.getCmsPage();
        if(cmsPage1==null){
        //  ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            return  new CmsPublishResult(CommonCode.FAIL,null);
        }
        //2.执行静态化+发布服务器，此处我们需要明确，我们本层的方法中发布服务器publishPage已经兼容了静态化和发布服务器两个操作
        ResponseResult responseResult = this.publishPage(cmsPage1.getPageId());
        if(!(responseResult!=null&&responseResult.isSuccess())){
         //   ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            return  new CmsPublishResult(CommonCode.FAIL,null);
        }
        //3.进行组装数据返回访问的详情页面
        //访问页面如何组装呢？涉及到：www.xuecheng.com/course/detail/xxxxx.html
        //域名地址[来自于cmsSite]+（页面访问路径+页面名称）[来自于cmsPage]
        String siteId = cmsPage1.getSiteId();
        CmsSite cmsSite = cmsSiteRepository.findOne(siteId);
        if(cmsSite==null){
         //   ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            return  new CmsPublishResult(CommonCode.FAIL,null);
        }
        //端口等等不再配置，默认80
        String pageUrl=cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+cmsPage1.getPageWebPath()+cmsPage1.getPageName();

        //发布页面后进行保存媒资信息到数据库中进行保存





        return  new CmsPublishResult(CommonCode.SUCCESS,pageUrl);
    }




}
