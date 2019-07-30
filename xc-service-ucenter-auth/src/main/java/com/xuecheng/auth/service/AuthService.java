package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.ext.UserTokenStore;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.exception.ExceptionCastUtils2;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hotwater on 2018/7/15.
 */
@Service
public class AuthService {

    private  static  final Logger LOGGER= LoggerFactory.getLogger(AuthService.class);

    //redis的存活时间
    @Value("${auth.tokenValiditySeconds}")
    private int tokenValiditySeconds;

    //服务调用对象
    @Autowired
    LoadBalancerClient  loadBalancerClient;
    //注入restTemplate，远程访问Http
    @Autowired
    RestTemplate restTemplate;
    //注入redis请求的对象
    @Autowired
    StringRedisTemplate  stringRedisTemplate;
    /**
     * 利用四大元素进行构建验证
     * @param username      用户名
     * @param password      密码
     * @param clientId      客户端id
     * @param clientSecret  客户端密码
     * @return
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //参数处理
        if(StringUtils.isEmpty(username)){
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_USERNAME_NONE));
        }
        if(StringUtils.isEmpty(password)){
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_PASSWORD_NONE));
         }
        /**
         * 思路整理：
         *          首先利用我们构建条件进行请求验证服务器进行获取Token
         *
         */
      AuthToken authToken=  applyToken(username,password,clientId,clientSecret);
      if(authToken==null){
          ExceptionCastUtils2.throwException(  AuthCode.AUTH_ApplyToken_ERROR);
      }
      //进行将数据存储在redis中
        boolean  saveTokenToRedis=this.saveTokenToRedis(authToken,tokenValiditySeconds);
        if(!saveTokenToRedis){
            ExceptionCastUtils2.throwException(AuthCode.AUTH_SAVETOKENTOREDIS_ERROR);
        }
        return  authToken;

    }

    /**
     * 在认证中心服务层进行保存用户信息到redis中
     * @param authToken
     * @param tokenValiditySeconds
     * @return
     */
    private boolean saveTokenToRedis(AuthToken authToken, int tokenValiditySeconds) {

        //数据校验
        if(authToken==null||StringUtils.isEmpty(authToken.getJwt_token())){
            ExceptionCastUtils2.throwException(CommonCode.IllegalArgument_Ref_Null);
        }
        String key="user_token:"+authToken.getAccess_token();//此处注意是jti
        String value = null;
        try {
            value = JSON.toJSONString(authToken);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCastUtils2.throwException(AuthCode.JSON_TOSTRING_ERROR);
        }
        //保存之后进行查询保证存储可靠性
        stringRedisTemplate.boundValueOps(key).set(value,tokenValiditySeconds, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.boundValueOps(key).getExpire();
        return expire>0;
    }

    /**
     *  首先利用我们构建条件进行请求验证服务器进行获取Token
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret) {
        //1.此处利用loadBalancerClient进行实现调用eureka服务实现客户端的负载均衡
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        if(serviceInstance==null){
            ExceptionCastUtils2.throwException(AuthCode.AUTH_LOGIN_SERVER_NOTFOUND);
        }
        //2.此处利用eureka获取请求地址，并进行字符串拼接构建
        String RequestURL  = serviceInstance.getUri().toString() + "/auth/oauth/token";
        //3.构建查询的条件---一个是body中的验证模式，用户名，密码以及header中的authentication的验证
        //通过下面的条件我们知道，httpEntity的构造函数
//        	public HttpEntity(MultiValueMap<String, String> body,MultiValueMap<String, String> headers)
        MultiValueMap<String,String >body= new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);
        MultiValueMap<String,String>header= new LinkedMultiValueMap<>();
        //获取转换后的数据
        String basicValue=this.getAuthentication(clientId,clientSecret);
        header.add("Authorization",basicValue);
        //4.进行发送请求，获取令牌数据
        ResponseEntity<Map> responseEntity =null;

        //在此处追加代码进行处理SpringSecurity的对于400和401抛出异常的问题，设置我们的默认的异常处理机制
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            /**
             * @param response
             */
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //如果返回的状态码不是400或者401的时候才会进行调用父类的spring的异常处理
                if(response.getRawStatusCode()!=400&&response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        try {
        //真正的远程调用实现申请令牌
            responseEntity = restTemplate.exchange(RequestURL, HttpMethod.POST,new HttpEntity<MultiValueMap<String,String >>(body,header), Map.class);
        } catch (RestClientException e) {
//            e.printStackTrace();
            LOGGER.error("调用认证服务器获取令牌失败，{}",e.getMessage());
            //此处老师是将异常信息没有进行捕获而是直接返回null
            ExceptionCastUtils2.throwException(AuthCode.AUTH_ApplyToken_ERROR);
        }
        //5.从认证服务器返回的数据进行抽取数据转换为我们需要的数据
        AuthToken temp=new AuthToken();

        //取出返回对象中的map
        Map map =null;
//        if(map==null){
//            ExceptionCastUtils.throwException(new CustomException(AuthCode.RESPONSEENTITY_NULL));
//        }
        if(responseEntity!=null){
             map = responseEntity.getBody();
        }
        //进行数据转换之前进行数据的校验看看是否具有Error_description
        if(map!=null){
            String error_description =(String) map.get("error_description");
            if(StringUtils.isNotEmpty(error_description)){
            if(error_description.contains("坏的凭证")){
                //证明密码不对---SpringSecurity校验不通过
                ExceptionCastUtils2.throwException(AuthCode.AUTH_CREDENTIAL_ERROR);
            }else //UserDetailsService returned null, which is an interface contract violation
            if(error_description.contains("UserDetailsService returned null")){
                //证明用户名不存在---我们人为返回了null
                ExceptionCastUtils2.throwException(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }else{
                ExceptionCastUtils2.throwException(AuthCode.AUTH_LOGIN_ERROR);
            }
            }
        }



        try {

            //6.进行数据抽取

            //进行转换注入数据
            System.err.println("responseEntity.getBody():"+responseEntity.getBody());
            org.apache.commons.beanutils.BeanUtils.populate(temp,responseEntity.getBody());
            //此处的Jti不是标准的数据，因此需要进行单独处理
            String jwi = (String)responseEntity.getBody().get("jti");
            //交换access-token和jwt的位置----因为我们要保存jwt作为存储校验
            String access_token = temp.getAccess_token();
            String jwt_token = jwi;
//            String exchange=access_token;
            temp.setAccess_token(jwt_token);
            temp.setJwt_token(access_token);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            LOGGER.error("获取令牌请求中对于结果集进行转换数据失败,{}",e.getMessage());
            ExceptionCastUtils.throwException(new CustomException(AuthCode.AUTH_EXCHANGEDATA_ERROR));
        }
 
        return temp;

    }

    /**
     * 利用客户端id和客户端密码进行转换为请求的header数据即请求令牌时候需要的指定简单的HttpBasic验证
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getAuthentication(String clientId, String clientSecret) {
        //进行格式化
        String beforeEncode= String.format("%s:%s",clientId,clientSecret);
        //进行Base64编码
        byte[] encode = Base64.encode(beforeEncode.getBytes());
        return "Basic "+ new String (encode);

    }

    /**
     * 从redis中进行获取令牌
     * @param token
     * @return
     */
    public UserTokenStore getJWT(String token) {
        String key="user_token:"+token;
        String tempString = stringRedisTemplate.boundValueOps(key).get();
        UserTokenStore userTokenStore =null;
        if(tempString!=null) {
            //解析数据到
            userTokenStore = JSON.parseObject(tempString, UserTokenStore.class);
        try {
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("利用token进行抓取usertoken时候转换数据出现异常，{}",e.getMessage());
        }
            return userTokenStore;
        }
        return  null;

    }

    /**
     * 删除redis中的用户信息数据
     * @param uid
     * @return
     */
    public ResponseResult deleteTokenOfRedis(String uid) {
        String key="user_token:"+uid;
        stringRedisTemplate.delete(key);
        return new ResponseResult();
    }
}
