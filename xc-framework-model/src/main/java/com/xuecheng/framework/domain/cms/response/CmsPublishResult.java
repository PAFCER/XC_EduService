package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by hotwater on 2018/7/7.
 *     用于页面发布统一的返回消息的数据模型
 */
@Data
@ToString
@NoArgsConstructor
public class CmsPublishResult extends ResponseResult {
    private  String pageUrl;
    public   CmsPublishResult (ResultCode  resultCode,String pageUrl){
        super(resultCode);
        this.pageUrl=pageUrl;
    }
}
