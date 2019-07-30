package com.xuecheng.search.web.controller;

import com.xuecheng.api.search.CourseSearchControllerAPI;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/10.
 */
@RestController
public class SearchCourseController  implements CourseSearchControllerAPI {
    @Autowired
    CourseSearchService  courseSearchService;
    @Override
    public QueryResponseResult search(
            @PathVariable("page")Integer page,
            @PathVariable("size") Integer size,
           CourseSearchParam courseSearchParam) {
        return courseSearchService.search(page,size,courseSearchParam);
    }


    /**
     * 依据课程id进行查询ES获取课程信息
     *
     * @param courseId
     * @return
     */
    @Override
    public Map<String, CoursePub> findCoursePubById(@PathVariable("courseId") String courseId) {
        return courseSearchService.findCoursePubById(courseId);
    }

    /**
     * 搜索服务提供媒资信息查询接口
     *
     * @param teachplanId
     * @return
     */
    @Override
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId) {
        //为了后续的扩展，此处service可以进行传入多个参数即查询教学计划的
        String[] teachplanIds = {teachplanId};
        List<TeachplanMediaPub> teachplanMediaPubList = courseSearchService.getmedia(teachplanIds);
        if(teachplanMediaPubList==null||teachplanMediaPubList.size()<=0){
            return new TeachplanMediaPub();
        }

        return  teachplanMediaPubList.get(0);

        //写到这里了，完成了对应的媒资信息的搜索，后续需要的是完成

    }
}
