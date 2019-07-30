package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/6/23.
 */
public interface CmsManageRepository extends MongoRepository<CmsPage,String> {
    /**
     * 根据三要素确定是否已经存在该页面记录
     * @param pageName
     * @param siteId
     * @param pageWebPath
     * @return
     */
    CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String
            pageWebPath);
}
