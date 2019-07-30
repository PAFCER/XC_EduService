package com.xuecheng.auth.web.controller;

import com.xuecheng.api.auth.AuthControllerAPI;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.ext.UserTokenStore;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import com.xuecheng.framework.web.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by hotwater on 2018/7/15.
 */
@RestController
public class AuthController extends BaseController implements AuthControllerAPI {
    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;
    @Autowired
    AuthService authService;


    @Override
    public LoginResult login(LoginRequest loginRequest) {
        //首先进行校验请求的参数的合法性
        //用户名校验
        if(loginRequest==null|| StringUtils.isEmpty(loginRequest.getUsername())){
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_USERNAME_NONE));
        }
        //密码校验
        if( StringUtils.isEmpty(loginRequest.getPassword())){
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_PASSWORD_NONE));
        }
        //客户端id和客户端密码为配置文件注入

         AuthToken  authToken=authService.login(loginRequest.getUsername(),loginRequest.getPassword(),clientId,clientSecret);
        if(authToken==null){
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_ApplyToken_ERROR));
        }
         this.saveCookie(authToken.getAccess_token());
         return  new LoginResult(CommonCode.SUCCESS,authToken.getAccess_token());
    }

    /**
     * 保存cookie
     * @param access_token
     */
    private void saveCookie(String access_token) {
            //此处的httpOnly是属于如果设置为true那么前端JS就访问不到
        CookieUtil.addCookie(response,cookieDomain,"/","uid",access_token,cookieMaxAge,false);

    }

    /**
     * 退出登录需要做的几件事：
     *  1.删除服务端redis中的缓存数据
     *  2.删除客户端cookie
     *  3.删除客户端sessionStorage数据
     * @return
     */
    @Override
    public ResponseResult logout() {

        String uid = this.getToken("uid");
        //删除redis用户信息
        authService.deleteTokenOfRedis(uid);
         //删除客户端cookie信息
        this.clearCookie("uid");
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 删除客户端cookie
     * @param uid
     */
    private void clearCookie(String uid) {
        //思路：首先获取cookie进行设置生命周期为0
        Map<String, String> stringStringMap = CookieUtil.readCookie(request, uid);
        String cookievalue = stringStringMap.get(uid);
        CookieUtil.addCookie(response,cookieDomain,"/","uid",cookievalue,0,false);
    }

    /**
     * 依据cookie中的token进行查询redis进行获取令牌jwt
     * @return
     */
    @Override
    public JwtResult userjwt() {

        String token=this.getToken("uid");
        UserTokenStore userTokenStore = authService.getJWT(token);
        if(userTokenStore==null){
                return new JwtResult(CommonCode.FAIL,null);
        }
        return  new JwtResult(CommonCode.SUCCESS,userTokenStore.getJwt_token());
    }

    /**
     * 根据cookie工具类进行获取我们的token
     * @return
     */
    private String getToken(String uid) {
        Map<String, String> stringMap = CookieUtil.readCookie(request, uid);
        return  stringMap.get(uid);
    }
}