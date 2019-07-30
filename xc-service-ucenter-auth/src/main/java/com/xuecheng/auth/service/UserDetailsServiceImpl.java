package com.xuecheng.auth.service;

import com.xuecheng.auth.client.UserClient;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger LOGGER= LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    /**
     * 注入服务客户端，调用服务端接口
     */
    @Autowired
    UserClient  userClient;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        /**
         * 此处的业务逻辑需要改变位从数据库查询得到，其中
         */
        XcUserExt xcUserExt = userClient.getUserext(username);
        //根据用户名查询不到---直接返回null
        if(xcUserExt==null){
        return  null;
        }
        //获取密码
        String password  =xcUserExt.getPassword();
        //权限标识串
        String user_permission_string  = "";
        UserJwt userDetails = new UserJwt(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
       /* UserDetails userDetails = new org.springframework.security.core.userdetails.User(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(""));*/
//                AuthorityUtils.createAuthorityList("course_get_baseinfo","course_get_list"));
//        填充数据到userDetail中
        try {
            BeanUtils.copyProperties(userDetails,xcUserExt);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("进行数据copy发生失败，{}",e.getMessage());
        }
        return userDetails;
    }
}
