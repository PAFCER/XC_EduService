package com.xuecheng.framework.domain.ucenter.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;


/**
 * Created by admin on 2018/3/5.
 */
@ToString
public enum AuthCode implements ResultCode {
    AUTH_USERNAME_NONE(false,23001,"请输入账号！"),
    AUTH_PASSWORD_NONE(false,23002,"请输入密码！"),
    AUTH_VERIFYCODE_NONE(false,23003,"请输入验证码！"),
    AUTH_ACCOUNT_NOTEXISTS(false,23004,"账号不存在！"),
    AUTH_CREDENTIAL_ERROR(false,23005,"账号或密码错误！"),
    AUTH_LOGIN_ERROR(false,23006,"登陆过程出现异常请尝试重新操作！"),
    AUTH_LOGIN_SERVER_NOTFOUND(false,23007,"验证服务器加载失败"),
    AUTH_ApplyToken_ERROR(false,23008,"调用认证服务器获取令牌失败" ),
    RESPONSEENTITY_NULL(false,23009,"请求令牌数据返回结果集为空" ),
    AUTH_EXCHANGEDATA_ERROR(false,23010 ,"获取令牌请求中对于结果集进行转换数据失败" ),
    JSON_TOSTRING_ERROR(false,23011 ,"将数据进行转换从Map到字符串失败" ),
    AUTH_SAVETOKENTOREDIS_ERROR(false,23012 ,"进行存储token到redis失败" ),
    AUTH_AUTHORIZATION_ERROR(false,23013 ,"未匹配上AUTHORIZATION" );


    //操作代码
    @ApiModelProperty(value = "操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "操作代码", example = "22001", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "操作提示", example = "操作过于频繁！", required = true)
    String message;
    private AuthCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, AuthCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, AuthCode> builder = ImmutableMap.builder();
        for (AuthCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
