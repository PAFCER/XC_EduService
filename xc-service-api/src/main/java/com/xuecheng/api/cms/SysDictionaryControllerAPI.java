package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by hotwater on 2018/7/2.
 */
@Api("字典表接口，提供对于字典表的查询和管理功能")
public interface SysDictionaryControllerAPI {
    //前缀
     final  String  SYS_PRE="/sys/dictionary";
     @ApiOperation("根据指定的字典数据类型查询系统字典表的列表")
     @GetMapping(SYS_PRE+"/get/{dType}")
    public SysDictionary getSysDicthinaryByDType(@PathVariable("dType") String dType);
}
