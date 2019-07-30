package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by hotwater on 2018/6/23.
 * 接口
 */
@Api(value = "Course管理接口",description = "Course接口提供课程的增删改查的方法")
public interface CourseControllerAPI {

    //前缀
    final  String  Course_PRE="/course/page";
    /**
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    @ApiOperation("根据page，size分页查询,根据条件对象courseListRequest进行兼容条件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",required = true,dataType="Integer",value = "页码，起始索引0",paramType ="path" ),
            @ApiImplicitParam(name = "size",required = true,dataType="Integer",value = "页面大小值",paramType ="path" )
        }
    )
    @GetMapping(Course_PRE+"/list/{page}/{size}")
   public  QueryResponseResult  findAll(@PathVariable("page") Integer page, @PathVariable("size") Integer size, CourseListRequest courseListRequest);

    //添加商品的方法
    @ApiOperation("添加课程")
    @PostMapping(Course_PRE+"/add")
    public AddCourseResult addCourse(@RequestBody CourseBase  courseBase);
    /**
     查询课程计划
//     http://localhost:31002/course/page/teachplan/list
     */
    @ApiOperation("根据课程id查询课程计划列表用于前端ElementUI树形展示")
    @GetMapping(Course_PRE+"/teachplan/list/{courseid}")
    public TeachplanNode  findAll(@PathVariable("courseid")String courseid);

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    @ApiOperation("在指定的课程id情况下进行添加课程计划")
    @PostMapping(Course_PRE+"/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan  teachplan);

    @ApiOperation("根据课程id查询课程信息")
    @GetMapping(Course_PRE+"/find/{courseid}")
    public  CourseBase  findById(@PathVariable("courseid") String courseid);

    @ApiOperation("修改课程基本信息，涉及两个参数，第一个是课程id，第二个是修改后的课程信息承载体")
    @PostMapping(Course_PRE+"/update/{courseid}")
    public  ResponseResult updateCourseBase(@PathVariable("courseid") String courseid,@RequestBody CourseBase courseBase);


    /**
     * 依据课程id查询课程营销信息
     * @param courseid
     * @return
     */
    @ApiOperation("根据课程id查询营销信息，此处主键与课程基本信息表主键一致")
    @GetMapping(Course_PRE+"/market/findById/{courseid}")
    public CourseMarket findMarketInfo(@PathVariable("courseid")String courseid);

    /**
     * 修改课程营销信息---与上述的修改课程基本信息相同
     * @param courseid
     * @param courseMarket
     * @return
     */
    @ApiOperation("修改课程营销信息，涉及两个参数，第一个是课程id，第二个是修改后的课程营销信息承载体")
    @PostMapping(Course_PRE+"/market/update/{courseid}")
    public  ResponseResult updateMarketInfo(@PathVariable String courseid,@RequestBody CourseMarket courseMarket);


    /**
     * 添加课程图片
     * @param courseid
     * @param pic
     * @return
     */
    @ApiOperation("添加课程图片")
    @GetMapping(Course_PRE+"/coursePic/add/{courseid}/{pic}")
    public  ResponseResult  addCoursePic(@PathVariable("courseid")String courseid,@PathVariable("pic")String pic);


    /**
     * 根据课程id查询课程的图片
     * @param courseid
     * @return
     */
    @ApiOperation("根据课程id查询课程的图片")
    @GetMapping(Course_PRE+"/findCoursePic/{courseid}")
    public CoursePic  findCoursePicList(@PathVariable("courseid") String courseid);


    /**
     * 根据课程id删除课程图片
     * @param courseid
     * @return
     */
    @ApiOperation("根据课程id删除课程图片")
    @GetMapping(Course_PRE+"/coursePic/delete/{courseid}")
    public   ResponseResult  deleteCoursePic(@PathVariable("courseid")String courseid);


 /**
  * 依据课程id查询课程视图即全部相关的课程信息
  * @param courseId
  * @return
  */
 @ApiOperation("根据课程id生成全部课程相关的信息即课程视图查询，一般用于生成静态页面的数据模型载体")
    @GetMapping(Course_PRE+"/courseview/{courseId}")
    public CourseView  courseView(@PathVariable("courseId") String courseId);


    /**
     * 前端页面调用，此处生成静态页面的访问路径给予用户
     * @param courseId
     * @return
     */
 @ApiOperation("暴露前端页面访问预览接口，返回用于预览的静态页面的url地址")
 @GetMapping(Course_PRE+"/loadPreViewUrl/{courseId}")
 public CoursePublicResult  LoadPreViewURL(@PathVariable("courseId") String courseId);

    /**
     * 前端页面调用，利用Reign调用CMS一键发布接口，CMS系统实现页面的静态化，发布（发送MQ），
     * 及其组装发布的服务器的资源访问路径
     * 返回当前页面进行访问。
     * @param courseId
     * @return
     */
 @ApiOperation("暴露前端页面访问发布借楼，实则返回发布后的页面归属的服务器的资源访问路径")
 @GetMapping(Course_PRE+"/loadAccessUrlAfterPublish/{courseId}")
 public  CoursePublicResult  loadAccessUrlAfterPublish(@PathVariable("courseId")String courseId);


 @ApiOperation("构建课程计划和视频之间的关系，保存信息到TeachplanMedia表中")
 @PostMapping(Course_PRE+"/saveMedia")
 public  ResponseResult  buildRelativeBetweenTeachPlanAndMedia(@RequestBody TeachplanMedia teachplanMedia);




}
