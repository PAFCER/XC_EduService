package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.SysDictionaryControllerAPI;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.CmsSysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/2.
 */
@RestController
public class CmsSysDictionaryController implements SysDictionaryControllerAPI{

    @Autowired
    private CmsSysDictionaryService cmsSysDicthinaryService;
    @Override
    public SysDictionary getSysDicthinaryByDType(@PathVariable("dType") String dType) {

        return cmsSysDicthinaryService.findBydType(dType);
    }
}
