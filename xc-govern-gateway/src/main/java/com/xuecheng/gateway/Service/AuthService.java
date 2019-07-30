package com.xuecheng.gateway.Service;

import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/17.
 */
@Service
public class AuthService {

    /**
     * 校验规则：
     *本小节实现网关连接Redis校验令牌：
     1、从cookie查询用户身份令牌是否存在，不存在则拒绝访问
     2、从http header查询jwt令牌是否存在，不存在则拒绝访问
     3、从Redis查询user_token令牌是否过期，过期则拒绝访问
     */

    /**
     * 此处进行网关的登陆校验，首先需要明确的是：
       1.token
       2.jwt
        上述两者其中之一不存在就拒绝访问
        如果
     *  通过这里的进行校验，第一个就是通过cookie获取access-token
     *  查询redis获取token，存在之后进行
     */
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 校验cookie传递的数据
     * @param request
     * @return
     */
    public boolean checkCookieTokenRelactiveRedis(HttpServletRequest request) {

          String cookieToken=  getCookie(request,"uid");
          if(StringUtils.isEmpty(cookieToken)){
              return false;
          }
        //拿到cookie之后进行查询redis
        boolean  flag=checkTokenOfRedis(cookieToken);
        if(flag){//如果是false则直接返回false
           return  true;
        }
        return false;
    }

    /**
     * 获取cookie
     * @param request
     * @param uid
     * @return
     */
    private String getCookie(HttpServletRequest request,String uid) {
        Map<String, String> stringStringMap = CookieUtil.readCookie(request, uid);
        if(stringStringMap!=null){
            return   stringStringMap.get("uid");
        }
        return null;
    }

    /**
    检查redis中书否存在对应的令牌
     */
    private boolean checkTokenOfRedis(String cookieToken) {
        String key="user_token:"+cookieToken;
        String jwt = stringRedisTemplate.boundValueOps(key).get();
        Long expire = stringRedisTemplate.getExpire(key);
        if(jwt==null||expire<=0){
            return false;
        }
        return true;
    }

    /**
     *
     * 获取header中的authorizen
     * @param request
     * @return‘
     * Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1MzE5NDM1MzMsImp0aSI6IjYwZmYxYTg1LWI4MmQtNGJlMi05YThmLTc3YmM5NTIyMmJhYyIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.TC8-RVY-eJzVWRx-liqW6v7BHuPdUSDH6lAErw4pZl-kiqzdgF4KOu6ku8XKjZA8oeVgG-WUZZosI8mYR0VxNwEtNfZtSqVeB8a1hRxEljDTprHuzOB5KQY0U_uSkccq2c9hYE_E08ak_JanZVqofBK-DntzAKRZX3qzMVa_sGmZgxkuSX8OW9nG7Bx9Q0YPsQFNN4eQ4U50D_vxKQbu-2pPa_V44uFsUPhfl8N1r9k-jeDlRieYQfNAJe3WXgG0P6iAwKLVchT45dSBmLMlYOSvRtgrhigNa3b9dKI8qcmWQRNGIn0TxTBjc-TShlgVjdNXuRx5LumqHG42I08U8w
     */
    public boolean checkJWT(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if(StringUtils.isEmpty(authorization)||!authorization.startsWith("Bearer")){
        return false;
        }

        return true;


    }
}
