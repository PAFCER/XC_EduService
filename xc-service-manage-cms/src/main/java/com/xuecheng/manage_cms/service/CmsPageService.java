package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsManageRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

/**
 * Created by hotwater on 2018/6/23.
 */
@Service
public class CmsPageService {

    @Autowired
    private CmsManageRepository cmsManageRepository;

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


}
