package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/6/23.
 * CmsConfig
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {

}
