package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hotwater on 2018/7/4.
 */
public interface CoursePicRepository extends JpaRepository<CoursePic,String>{

    /**
     * 根据课程id删除课程图片信息
     * @param courseid
     * @return
     */
    long deleteByCourseid(String courseid);


}
