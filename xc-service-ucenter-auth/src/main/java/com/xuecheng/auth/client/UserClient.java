package com.xuecheng.auth.client;

import com.xuecheng.api.ucenter.UcenterControllerAPI;
import com.xuecheng.framework.client.XcServiceList;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * Created by hotwater on 2018/7/17.
 */
@FeignClient(value = XcServiceList.XC_SERVICE_UCENTER)
public interface UserClient extends UcenterControllerAPI {
}
