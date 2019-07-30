package com.xuecheng.ucenter.web.controller;

import com.xuecheng.api.ucenter.UcenterControllerAPI;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/17.
 */
@RestController
public class UcenterController implements UcenterControllerAPI {
    @Autowired
    UserService userService;
    @Override
    public XcUserExt getUserext(@RequestParam("username") String username) {
        XcUserExt xcUser = userService.getUserExt(username);
        return xcUser;
    }
}
