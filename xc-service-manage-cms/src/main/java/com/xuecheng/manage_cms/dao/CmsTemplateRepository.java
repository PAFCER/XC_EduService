package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/6/23.
 * CmsTemplate
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {

}
