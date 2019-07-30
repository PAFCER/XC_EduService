package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by hotwater on 2018/6/30.
 * 课程接口
 */
@Mapper
public interface CourseMapper {

    /**
     * 进行查询所有的课程基本信息
     * @return
     */
    public List<CourseBase> findCourseList();

    /**
     * 进行查询所有的课程基本信息----追加条件查询
     * @return
     */
    public Page<CourseInfo> findCourseListByCondition(CourseListRequest courseListRequest);


}
