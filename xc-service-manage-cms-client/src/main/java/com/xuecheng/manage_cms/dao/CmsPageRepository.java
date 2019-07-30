package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/6/30.
 */
public interface CmsPageRepository extends MongoRepository<CmsPage,String>{
}
