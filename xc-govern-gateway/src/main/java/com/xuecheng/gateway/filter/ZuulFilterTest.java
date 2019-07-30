package com.xuecheng.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hotwater on 2018/7/18.
 * 测试成功
 */
//@Component
public class ZuulFilterTest  extends ZuulFilter{
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    @Override
    public Object run() {
    //此处的方法是进行设定是否放行的关键
        RequestContext  requestContext  =RequestContext.getCurrentContext();
        HttpServletRequest request =
                requestContext.getRequest();
        HttpServletResponse response =
                requestContext.getResponse();
        //如果是空则不予以放行
        if(StringUtils.isEmpty(request.getHeader("Authorization"))){
            //如果为空则校验为false，不予以放行
            requestContext.setSendZuulResponse(false);
            //设置状态码为200
            requestContext.setResponseStatusCode(200);
            //创建响应码数据对象
            ResponseResult  responseResult= new ResponseResult(AuthCode.AUTH_AUTHORIZATION_ERROR);
            //转换为JSON数据
            String jsonString = JSON.toJSONString(responseResult);
            //设置响应类型
            response.setContentType("application/json;charset=utf-8");
            //设置响应体
            requestContext.setResponseBody(jsonString);
            return null;
        }


        return null;
    }
}
