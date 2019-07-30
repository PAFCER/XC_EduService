package com.xuecheng.manage_course.client;

import com.xuecheng.api.cms.CmsPageControllerAPI;
import com.xuecheng.framework.client.XcServiceList;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by hotwater on 2018/7/5.
 */

@FeignClient(value=XcServiceList.XC_SERVICE_MANAGE_CMS)
public interface CmsPageClient extends CmsPageControllerAPI{
}
