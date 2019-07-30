package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by hotwater on 2018/7/15.
 */
@Api(value = "用户认证",description = "用户认证接口")
public interface AuthControllerAPI {
    final String API_PRE = "";
    @PostMapping(API_PRE+"/userlogin")//也可以是login
    @ApiOperation("登录")
    public LoginResult login(@RequestBody LoginRequest loginRequest);
    @PostMapping(API_PRE+"/userlogout")
    @ApiOperation("退出")
    public ResponseResult logout();

    @GetMapping(API_PRE+"/userjwt")
    @ApiOperation("查询userjwt令牌")
    public JwtResult userjwt();
}