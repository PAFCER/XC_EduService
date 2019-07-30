package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hotwater on 2018/7/17.
 */
public interface XcUserRepository extends JpaRepository<XcUser, String> {
    XcUser findXcUserByUsername(String username);
}
