package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.CmsSysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hotwater on 2018/7/2.
 */
@Service
public class CmsSysDictionaryService {
    @Autowired
    private CmsSysDictionaryRepository cmsSysDicthinaryRepository;
    public SysDictionary  findBydType(String dType){
      return   cmsSysDicthinaryRepository.findBydType(dType);
    }
}
