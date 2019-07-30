package com.xuecheng.file_system.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/7/4.
 */
public interface FileSystemRepository  extends MongoRepository<FileSystem,String>{
}
