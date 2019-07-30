package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/7/2.
 */
public interface CmsSysDictionaryRepository extends MongoRepository<SysDictionary,String>{

    /**
     * 根据字典类型查询字典对象
     * @param dType
     * @return
     */
    SysDictionary  findBydType(String  dType);
}
