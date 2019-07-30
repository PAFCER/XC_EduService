package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/6/30.
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String>{
}
