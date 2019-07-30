package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hotwater on 2018/6/30.
 */
public interface CourseRepository extends JpaRepository<CourseBase,String>{

}
