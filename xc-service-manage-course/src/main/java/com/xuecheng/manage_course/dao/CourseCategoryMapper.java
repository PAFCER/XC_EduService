package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by hotwater on 2018/6/30.
 * 课程接口
 */
@Mapper
public interface CourseCategoryMapper {

    CategoryNode categoryList();

}
