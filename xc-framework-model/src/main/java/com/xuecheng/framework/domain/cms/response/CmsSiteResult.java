package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

/**
 * 进行返回站点集合的数据
 */
@Data
public class CmsSiteResult extends ResponseResult {
    CmsSite cmsSite;
    public CmsSiteResult(ResultCode resultCode, CmsSite cmsSite) {
        super(resultCode);
        this.cmsSite = cmsSite;
    }
}
