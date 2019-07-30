package com.xuecheng.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.gateway.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hotwater on 2018/7/17.
 */
@Component
public class LoginFilter  extends ZuulFilter {

    @Autowired
    AuthService  authService;

    @Override
    public String filterType() {
        //四种类型：pre、routing、post、error
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        //利用网费的对象进行获取request对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();

        //下面是进行校验cookie数据的合法性
        boolean Tokenflag = authService.checkCookieTokenRelactiveRedis(request);
        if(!Tokenflag){
            access_refuse(currentContext,response);
        }
        //校验令牌jwt---在header中存在的
        boolean jwtFlag=authService.checkJWT(request);
        if(!jwtFlag){
            access_refuse(currentContext,response);//拒绝访问
        }

        return null;
    }

    /**
     * 设置拒绝访问的
     * @param currentContext
     * @param response
     */
    private void access_refuse(RequestContext currentContext, HttpServletResponse response) {
        //组织响应的数据
        ResponseResult  responseResult= new ResponseResult(AuthCode.AUTH_AUTHORIZATION_ERROR);
        String jsonString = JSON.toJSONString(responseResult);
        //设置响应体
        currentContext.setResponseBody(jsonString);
        //设置响应码
        currentContext.setResponseStatusCode(200);
        //设置编码
        response.setContentType("application/json;charset=UTF-8");
        //阻止访问
        currentContext.setSendZuulResponse(false);
    }
}
