package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hotwater on 2018/7/13.
 *
 * 此处主要是操作媒资信息的发布表---待索引表
 */
public interface TeachPlanMediaPubRepository extends JpaRepository<TeachplanMediaPub,String> {


    //根据指定的课程id进行删除课程媒资信息---为新插入做准备前的必做工作

    long  deleteByCourseId(String courseId);


}
