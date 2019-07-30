package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by hotwater on 2018/6/30.
 * 查询课程计划列表的dao
 */
@Mapper
public interface TeachplanMapper {

    /**
     * 查询课程计划列表
     * @param courseid
     * @return
     */
    TeachplanNode teachplanList(String courseid);

}
