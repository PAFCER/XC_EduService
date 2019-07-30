package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hotwater on 2018/7/13.
 */
public interface TeachPlanMediaRepository extends JpaRepository<TeachplanMedia,String> {

    //根据指定的课程id进行查询媒资信息---为同步发布课程信息时候进行保存媒资信息到对应的pub表
    List<TeachplanMedia> findByCourseId(String courseId);


}
