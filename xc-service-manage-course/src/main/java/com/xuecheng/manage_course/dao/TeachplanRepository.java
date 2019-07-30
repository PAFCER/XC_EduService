package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by hotwater on 2018/6/30.
 */
public interface TeachplanRepository extends JpaRepository<Teachplan,String>{



    //自定义方法根据courseid和parentid进行查询对应的课程计划节点
    List<Teachplan> findByCourseidAndParentid(String courseid,String parentid);


}
