package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.CourseControllerAPI;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/6/30.
 */
@RestController
public class CourseController implements CourseControllerAPI{
    @Autowired
    private CourseService  courseService;

    @Override

    public QueryResponseResult findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size, CourseListRequest courseListRequest) {
        QueryResponseResult  queryResponseResult=courseService.findAll(page,size,courseListRequest);
        return queryResponseResult;
    }

    @Override
    public AddCourseResult addCourse(@RequestBody  CourseBase courseBase) {
        return courseService.addCourse(courseBase);
    }

    /**
     * 根据课程id查询课程计划列表
     * @param courseid
     * @return
     */
    @Override
    public TeachplanNode findAll(@PathVariable("courseid") String courseid) {
        return courseService.findAll(courseid);
    }


    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @Override
    public ResponseResult addTeachplan(@RequestBody  Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }


    /**
     * 根据课程id查询课程信息
     * @param courseid
     * @return
     */
    @Override
    public CourseBase findById(@PathVariable("courseid") String courseid) {

        return courseService.findById(courseid);
    }


    /**
     *根据课程id查询和课程信息进行修改课程信息
     * @param courseid
     * @param courseBase
     * @return
     */
    @Override
    public ResponseResult updateCourseBase(@PathVariable("courseid") String courseid, @RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(courseid,courseBase);
    }


    /**
     * 依据课程id查询课程营销信息
     *
     * @param courseid
     * @return
     */
    @Override
    public CourseMarket findMarketInfo(@PathVariable("courseid") String courseid) {
        return courseService.findMarketInfo(courseid);
    }

    /**
     * 修改课程营销信息---与上述的修改课程基本信息相同
     *
     * @param courseid
     * @param courseMarket
     * @return
     */
    @Override
    public ResponseResult updateMarketInfo(@PathVariable("courseid") String courseid,@RequestBody CourseMarket courseMarket) {
        return courseService.updateCourseMarketInfo(courseid,courseMarket);
    }


    /**
     * 根据课程id查询课程的图片
     *
     * @param courseid
     * @return
     */
    @Override
    public CoursePic findCoursePicList(@PathVariable("courseid") String courseid) {

        return courseService.findCoursePicList(courseid);
    }

    /**
     * 添加课程图片
     *
     * @param courseid
     * @param pic
     * @return
     */
    @Override
    public ResponseResult addCoursePic(@PathVariable("courseid") String courseid, @PathVariable("pic") String pic) {

        return courseService.addCoursePic(courseid,pic);
    }

    /**
     * 根据课程id删除课程图片
     *
     * @param courseid
     * @return
     */
    @Override
    public ResponseResult deleteCoursePic(@PathVariable("courseid") String courseid) {

        return courseService.deleteCoursePic(courseid);
    }


    /**
     * 依据课程id查询课程视图即全部相关的课程信息
     *
     * @param courseId
     * @return
     */
    @Override
    public CourseView courseView(@PathVariable("courseId") String courseId) {
        return courseService.courseView(courseId);
    }

    /**
     * 前端页面调用，此处生成静态页面的访问路径给予用户
     *
     * @param courseId
     * @return
     */
    @Override
    public CoursePublicResult LoadPreViewURL(@PathVariable("courseId") String courseId) {
        return courseService.LoadPreViewURL(courseId);
    }

    /**
     * 前端页面调用，利用Reign调用CMS一键发布接口，CMS系统实现页面的静态化，发布（发送MQ），
     * 及其组装发布的服务器的资源访问路径
     * 返回当前页面进行访问。
     *
     * @param courseId
     * @return
     */
    @Override
    public CoursePublicResult loadAccessUrlAfterPublish(@PathVariable("courseId") String courseId) {
        return courseService.loadAccessUrlAfterPublish(courseId);
    }


    @Override
    public ResponseResult buildRelativeBetweenTeachPlanAndMedia(@RequestBody TeachplanMedia teachplanMedia) {

        return courseService.saveMedia(teachplanMedia);
    }


}
