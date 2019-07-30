package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Created by hotwater on 2018/7/10.
 * 课程搜索服务API
 */
@Api(value="课程搜索服务接口",description = "提供课程搜索服务的接口")
public interface  CourseSearchControllerAPI {
    //课程搜索服务前缀
      final String Search_PRE="/search/course";
    @ApiOperation("提供前端课程搜索服务，具有关键字搜索，类别过滤，其他条件过滤等等并且兼容分页及其搜索关键字高亮显示")
    @GetMapping(Search_PRE+"/list/{page}/{size}")
    public QueryResponseResult search(
            @PathVariable("page")Integer page,//分页起始页码
            @PathVariable("size")Integer size,//分页每页数据量
             CourseSearchParam courseSearchParam//分页查询条件
            );

    /**
     * 依据课程id进行查询ES获取课程信息
     * @param courseId
     * @return
     */
    @ApiOperation("依据课程id在ES中查询课程信息")
@GetMapping(Search_PRE+"/findCoursePubById/{courseId}")
public Map<String,CoursePub> findCoursePubById(@PathVariable("courseId")String courseId);

    /**
     * 搜索服务提供媒资信息
     * @param teachplanId
     * @return
     */
    @GetMapping(value="/getmedia/{teachplanId}")
    @ApiOperation("根据课程计划查询媒资信息")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);

}
