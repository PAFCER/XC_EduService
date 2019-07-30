package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/7/4.
 */
public interface FileSystemRespository  extends MongoRepository<FileSystem,String>{
}
