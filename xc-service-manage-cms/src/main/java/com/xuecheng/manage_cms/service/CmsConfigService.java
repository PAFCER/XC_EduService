package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsConfigResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hotwater on 2018/6/23.
 */
@Service
public class CmsConfigService {

    @Autowired
    private CmsConfigRepository cmsConfigRepository;
    /**
     * 根据id查询数据对象
     * @param id
     * @return
     */
    public CmsConfigResult  findById(String id){
    if(StringUtils.isEmpty(id)){
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
    }
    //根据id查询对应的实体信息
     CmsConfig one= cmsConfigRepository.findOne(id);
    if(one==null){
        return  new CmsConfigResult(CommonCode.FAIL,one);
    }
    return   new CmsConfigResult(CommonCode.SUCCESS,one);
    }
}
