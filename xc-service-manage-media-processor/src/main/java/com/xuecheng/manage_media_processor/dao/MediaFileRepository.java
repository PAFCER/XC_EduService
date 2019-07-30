package com.xuecheng.manage_media_processor.dao;

import com.xuecheng.framework.domain.media.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by hotwater on 2018/7/12.
 */
public interface MediaFileRepository extends MongoRepository<MediaFile,String > {
}
