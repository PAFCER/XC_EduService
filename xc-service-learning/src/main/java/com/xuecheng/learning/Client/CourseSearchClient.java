package com.xuecheng.learning.Client;

import com.xuecheng.api.search.CourseSearchControllerAPI;
import com.xuecheng.framework.client.XcServiceList;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by hotwater on 2018/7/16.
 */
@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface CourseSearchClient extends CourseSearchControllerAPI {
}