package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.util.StringUtil;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPublishResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hotwater on 2018/7/1.
 */
@Service
@Transactional
public class CourseService {
    //注入生成cmsPage页面的数据---此处的数据基本上与本服务器自身相关，比如站点等等
    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    @Autowired
    private  TeachPlanMediaPubRepository  teachPlanMediaPubRepository;

    @Autowired
    TeachPlanMediaRepository teachPlanMediaRepository;
    private  final Logger  logger= LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseMarketRepository  courseMarketRepository;

    @Autowired
    private CourseMapper  courseMapper;

    @Autowired
    private  TeachplanMapper  teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;
    @Autowired
    private  FileSystemRespository  fileSystemRespository;
    //用于Feign调用CMS系统的接口代理对象
    @Autowired
    private CmsPageClient  cmsPageClient;


    @Autowired
    private  CoursePicRepository  coursePicRepository;
    public QueryResponseResult   findAll(Integer page, Integer size, CourseListRequest courseListRequest){
        //拦截器追加分页
        PageHelper.startPage(page,size);
        QueryResult<CourseInfo> infoQueryResult = new QueryResult<>();
        try {
            //1.根据分页进行查询不再需要传递分页参数，只需要传递查询条件即可
            Page<CourseInfo> courseListByCondition = courseMapper.findCourseListByCondition(courseListRequest);
            //2.获取数据集合
            List<CourseInfo> result = courseListByCondition.getResult();
            //3.获取总记录数
            long total = courseListByCondition.getTotal();
            //4.给予赋值
            infoQueryResult.setTotal(total);
            infoQueryResult.setList(result);
            //5.返回结果集
            return new QueryResponseResult(CommonCode.SUCCESS,infoQueryResult);
        } catch (Exception e) {
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,infoQueryResult);
        }
    }

    /**
     * 添加课程
     * @param courseBase
     * @return
     */
    public AddCourseResult  addCourse(CourseBase courseBase){
        if(courseBase==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
//        id           VARCHAR(32)     NO  PRI (NULL)   课程主键
//        NAME         VARCHAR(32)     NO      (NULL)   课程名称
//        users        VARCHAR(500)    YES     (NULL)   适用人群
//        mt           VARCHAR(32)     NO      (NULL)   课程大分类
//        grade        VARCHAR(32)     NO      (NULL)   课程等级
//        studymodel   VARCHAR(32)     NO      (NULL)   学习模式
//        teachmode    VARCHAR(32)     YES     (NULL)   授课模式
//        description  TEXT            YES     (NULL)   课程介绍
//        st           VARCHAR(32)     NO      (NULL)   课程小分类
//        STATUS       VARCHAR(32)     YES     (NULL)   课程状态
//        company_id   VARCHAR(32)     YES     (NULL)   教育机构
//        user_id      VARCHAR(32)     YES     (NULL)   创建用户
        //查看上述的数据库信息表可以发现，目前指定一个课程状态，其他的数据可能需要后续功能完善后再进行追加，比如companyId
        courseBase.setStatus("202001");//未发布，此处可以使用枚举或者常量进行指定
        CourseBase save = courseRepository.save(courseBase);
        if(save!=null){
            //成功的或返回客户id保存到浏览器的VUE环境中进行维护方便后续的几个功能进行操作
            return  new AddCourseResult(CommonCode.SUCCESS,save.getId());
        }else{
            return new AddCourseResult(CommonCode.FAIL,null);
        }
    }

    /**
     * 根据课程id查询课程计划列表
     * @param courseid
     * @return
     */
    public TeachplanNode  findAll(String  courseid){
            //1.参数判定
        if(StringUtil.isEmpty(courseid)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("查询课程计划列表的时候参数courseid参数异常为空");
        }

//        id           VARCHAR(32)   NO  PRI (NULL)
//        pname        VARCHAR(64)   NO      (NULL)
//        parentid     VARCHAR(32)   NO      (NULL)
//        grade        CHAR(1)       NO      (NULL)  层级，分为1、2、3级
//        ptype        CHAR(1)       YES     (NULL)  课程类型:1视频、2文档
//        description  VARCHAR(500)  YES     (NULL)  章节及课程时介绍
//        timelength   DOUBLE(5,2)   YES     (NULL)  时长，单位分钟
//        courseid     VARCHAR(32)   YES     (NULL)  课程id
//        orderby      VARCHAR(32)   YES     (NULL)  排序字段
//        STATUS       CHAR(1)       NO      (NULL)  状态：未发布、已发布
//        trylearn     CHAR(1)       YES     (NULL)  是否试学
        //2.查询---类似于原先的分类查询，典型的分类构建树形结构的数据模型
        TeachplanNode teachplanList = teachplanMapper.teachplanList(courseid);

        if(teachplanList==null){
            //如果为空则创建一个空对象返回，不返回null，容易造成前端空指针异常
            return new TeachplanNode(   );
        }else{
            return teachplanList;
        }
    }

    /**
     * 添加课程计划--其实就是教学计划==
     * @param teachplan
     * @return
     */
    public ResponseResult addTeachplan(Teachplan  teachplan){
        /**
         * 此处的逻辑稍稍复杂，需要进行一定的逻辑思考
         * 首先需要明确一点就是我们需要将利用我们页面传递的数据进行添加课程计划记录
         * 1.涉及到的如果传递过来的分类（上级）parentid为空的话，即选择的是默认的父节点，即为该课程对应的在
         *      课程计划表中的记录的parentid，此处需要我们进行查询，如果依据我们的courseid没有查询到对应的
         *       父id的话，那么需要我们将对应的课程添加到教学计划中，此处指定父节点为0，此处需要的数据需要我们进行查询
         *       课程表进行抽取需要的数据进行查询，然后利用上述的记录节点传递给当前的需要添加的课程计划记录的父节点
         *       ，另外涉及到一个等级层次，此处新追加的课程计划依赖于父节点的层次等级，因此我们需要查询出来父节点的层次登记
         *       然后进行添加一定的逻辑进行换算当前追加记录的课程计划的层次登记
         * 2.除了上述的传递的parentid为null 的情况下，如果页面传递了parentid节点，那么我们直接依据我们的该节点进行追加记录即可
         *      对应的层次等级依然按照如上的逻辑进行处理即可，对此我们需要斟酌
         * 3.对于上述的逻辑我们不再赘述，比较简单，另外我们追加，下面快速进行代码实现。
         */
        //此处本身可以抛出异常，但是由于会触发本地的方法异常，因此如果传递参数为null的话，那么我们直接返回null
        if(teachplan==null){
            return null;
        }
        //参数不为null
        String parentid = teachplan.getParentid();
        //如果父节点为空----进行处理父节点问题
        if(StringUtil.isEmpty(parentid)){
        //那么就是根节点即对应的课程及其parentid可以确定下来及其节点
         parentid= this.getCurrentNodeParentId(teachplan.getCourseid());
        }
        //处理完毕父节点之后，进行换算其他的数据，进行保存课程计划
        //拿到父节点id之后，进行数据的保存

        teachplan.setParentid(parentid);

        //设置未发布--默认
        teachplan.setStatus("0");

        //设置等级----依赖于父节点
        String grade = this.getGradeByParentId(parentid);

        //设置等级
        teachplan.setGrade(grade);
        //进行保存
        Teachplan save = teachplanRepository.save(teachplan);
        if(save==null){
            return new ResponseResult(CommonCode.SaveNewTeachPlan_ERROR);
        }else{
            return new ResponseResult(CommonCode.SUCCESS);
        }
    }

    /**
     * 依据父节点进行查询等级并进行换算子节点等级
     * @param parentid
     */
    private String getGradeByParentId(String parentid) {
        Teachplan one = teachplanRepository.findOne(parentid);
        String grade =one.getGrade();
        if("1".equals(grade)){
            grade="2";
        }else if ("2".equals(grade)){
            grade="3";
        }
        return grade;
    }

    /**
     * 为当前新添加的（节点）记录查询或者获取父节点
     * @param courseid
     * @return
     */
    private String  getCurrentNodeParentId(String courseid) {

        if(StringUtil.isEmpty(courseid)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("为当前新添加的（节点）记录查询或者获取父节点参数courseid异常");
        }
        //首先依据courseid和parentid进行查询节点记录----此处当然是默认的父节点0，因为是一级节点
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if(teachplanList==null||teachplanList.size()==0){
            //此处说明不存在咯，那么进行为当前的课程进行新建一级节点
            Teachplan save = this.CreateTeachplanWithOneLevel(courseid);
            if(save==null){
                ExceptionCastUtils.throwException(new CustomException(CommonCode.SaveNewTeachPlan_ERROR));
                logger.error("进行保存新的课程计划出现异常");
            }
            //新建后直接返回即可
             return  save.getId();
        }else{
            //获取随意一个结果集数据的节点id即为所需的parentid
            return teachplanList.get(0).getId();
        }


    }

    /**
     * 依据课程id进行创建一级节点
     * @param courseid
     * @return  新建的一级节点
     */
    private Teachplan CreateTeachplanWithOneLevel(String courseid) {
        CourseBase one = courseRepository.findOne(courseid);
        if(one==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("依据课程id查询对应的课程时候出现异常，不存在该记录");
        }
        //如果走到这里则为当前课程创建对应的教学计划一级节点-----组装数据
        Teachplan  teachplan  = new Teachplan();
        teachplan.setCourseid(courseid);
        teachplan.setGrade("1");//一级节点
        teachplan.setPname(one.getName());
        teachplan.setStatus("0");//未发布
        teachplan.setDescription(one.getDescription());
        teachplan.setParentid("0");//父节点为顶级节点
        Teachplan save = teachplanRepository.save(teachplan);
        return save;
    }


    /**
     * 根据课程id查询课程信息
     * @param courseid
     * @return
     */
    public   CourseBase  findById(String courseid){
        if(StringUtil.isEmpty(courseid)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CourseBase one = courseRepository.findOne(courseid);
        if(one!=null){
            return  one;
        }else{
            //无对应记录
            return null;
        }
    }

    /**
     * 修改课程基本信息
     * @param courseid
     * @param courseBase
     * @return
     */
    public   ResponseResult  updateCourseBase(String courseid,CourseBase courseBase){

            if(StringUtil.isEmpty(courseid)){
                logger.error("更新课程基本信息异常，courseid参数为null");
                ExceptionCastUtils.throwException(
                        new CustomException(CommonCode.IllegalArgument_Ref_Null)
                );
            }
        if(courseBase==null){
            logger.error("更新课程基本信息异常，coursebase参数为null");
            ExceptionCastUtils.throwException(
                        new CustomException(CommonCode.IllegalArgument_Ref_Null)
                );
            }
            //进行赋值
        //先根据courseid进行查询
        CourseBase one = courseRepository.findOne(courseid);
        if(one==null){
            logger.error("依据传递的参数没有对应的数据库记录，请注意前端传递参数与后端数据库的匹配");
            ExceptionCastUtils.throwException(
                    new CustomException(CommonCode.IllegalArgument_Ref_Null)
            );
        }
        try {
            //如果存在则进行数据的更新
            one.setStatus(courseBase.getStatus());
            one.setCompanyId(courseBase.getCompanyId());
            one.setSt(courseBase.getSt());
            one.setDescription(courseBase.getDescription());
            one.setGrade(courseBase.getGrade());
            one.setMt(courseBase.getMt());
            one.setName(courseBase.getName());
            one.setStudymodel(courseBase.getStudymodel());
            one.setTeachmode(courseBase.getTeachmode());
            one.setUserId(courseBase.getUserId());
            one.setUsers(courseBase.getUsers());
            //此处需要利用one进行更新，因为JPA规范对于bean对象的状态有要求，
            CourseBase save = courseRepository.save(one);
            //执行成功则进行返回成功
            return   new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }


    /**
     * 依据课程id查询课程营销信息
     * @param courseid
     * @return
     */
    public CourseMarket  findMarketInfo(String  courseid){
        if(StringUtil.isEmpty(courseid)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        CourseMarket one = courseMarketRepository.findOne(courseid);
        if(one!=null){
            return  one;
        }else{
            //无对应记录
            return null;
        }
    }

    /**
     * 更新课程营销信息
     * @param courseid
     * @param courseMarket
     * @return
     */
    public ResponseResult  updateCourseMarketInfo(String courseid,CourseMarket courseMarket){

        if(StringUtil.isEmpty(courseid)){
            logger.error("更新课程营销信息异常，courseid参数为null");
            ExceptionCastUtils.throwException(
                    new CustomException(CommonCode.IllegalArgument_Ref_Null)
            );
        }
        if(courseMarket==null){
            logger.error("更新课程营销信息异常，courseMarket参数为null");
            ExceptionCastUtils.throwException(
                    new CustomException(CommonCode.IllegalArgument_Ref_Null)
            );
        }
        //进行赋值
        //先根据courseid进行查询
        CourseMarket one = courseMarketRepository.findOne(courseid);
        if(one==null){
            logger.error("依据传递的参数没有对应的数据库记录，请注意前端传递参数与后端数据库的匹配");
            ExceptionCastUtils.throwException(
                    new CustomException(CommonCode.IllegalArgument_Ref_Null)
            );
        }
        try {
//
//            FIELD       TYPE          COMMENT
//                    ----------  ------------  ---------------------------------
//            id          VARCHAR(32)   课程id
//            charge      VARCHAR(32)   收费规则，对应数据字典
//            valid       VARCHAR(32)   有效性，对应数据字典
//            expires     TIMESTAMP     过期时间
//            qq          VARCHAR(32)   咨询qq
//            price       FLOAT(10,2)   价格
//            price_old   FLOAT(10,2)   原价
//            start_time  DATETIME      课程有效期-开始时间
//            end_time    DATETIME      课程有效期-结束时间
            //如果存在则进行数据的更新
               one.setCharge(courseMarket.getCharge());
               one.setEndTime(courseMarket.getEndTime());
               one.setPrice(courseMarket.getPrice());
               one.setPrice_old(courseMarket.getPrice_old());
               one.setQq(courseMarket.getQq());
               one.setStartTime(courseMarket.getStartTime());
               one.setValid(courseMarket.getValid());
            //此处需要利用one进行更新，因为JPA规范对于bean对象的状态有要求，
            CourseMarket save = courseMarketRepository.save(one);
            //执行成功则进行返回成功
            return   new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }



    }

    /**
     * 查询课程图片
     * @param courseid
     * @return
     */
    public CoursePic findCoursePicList( String courseid){

        if(StringUtils.isEmpty(courseid)){
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        logger.error("查询课程图片的参数课程id参数为空异常");
        }
        CoursePic one = coursePicRepository.findOne(courseid);
       if(one==null){
           one= new CoursePic();
       }
        return  one;

    }

    /**
     * 添加图片
     * @param courseid
     * @param pic
     * @return
     */
    public ResponseResult addCoursePic(String courseid,  String pic) {

        if(StringUtils.isEmpty(courseid)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("添加课程图片的参数课程id参数为空异常");
        }
        CoursePic one = coursePicRepository.findOne(courseid);
        CoursePic  coursePic  = new CoursePic();
        if(one==null){
           coursePic.setCourseid(courseid);
           coursePic.setPic(pic);
           coursePicRepository.save(coursePic);
        }else{
            one.setPic(pic);
            coursePicRepository.save(one);
        }
    return  new ResponseResult(CommonCode.SUCCESS);

    }

    /**
     * 依据课程id删除课程图片
     * @param courseid
     * @return
     */
    public ResponseResult deleteCoursePic(String courseid){

        long rows=coursePicRepository.deleteByCourseid(courseid);

        if(rows>0){
            return new ResponseResult(CommonCode.SUCCESS);
        }else{
            return   new ResponseResult(CommonCode.FAIL);
        }

    }

    /**
     * 依据课程id查询课程视图----为页面静态化提供数据模型支持
     * @param courseId
     * @return
     */
    public CourseView courseView(String courseId) {
        if(StringUtils.isEmpty(courseId)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("查询课程视图，课程id参数为空异常");
        }
        CourseView  courseView= new CourseView();
        CourseBase courseBase = courseRepository.findOne(courseId);
        if(courseBase==null){
            return courseView;
        }
        //1.组装课程基本数据
        courseView.setCourseBase(courseBase);
        //2.组装课程营销数据
        CourseMarket courseMarket = courseMarketRepository.findOne(courseId);
        courseView.setCourseMarket(courseMarket);
        //3.组装课程图片
        CoursePic coursePic = coursePicRepository.findOne(courseId);
        courseView.setCoursePic(coursePic);
        //4.组装课程计划
        TeachplanNode teachplanNode = teachplanMapper.teachplanList(courseId);
        courseView.setTeachplanNode(teachplanNode);
        //5.返回课程视图
        return courseView;
    }

    /**
     * 生成预览的静态页面访问路径返回给前端用户用于预览
     * @param courseId
     * @return
     */
    public CoursePublicResult LoadPreViewURL(String courseId) {
        CmsPage cmsPage = generateCmsPage(courseId);
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if(cmsPageResult!=null&&cmsPageResult.isSuccess()){
            //2.加载yml配置文件进行初始化cmsPage然后调用添加页面的方法实现。
            String  preViewURL=this.previewUrl+cmsPageResult.getCmsPage().getPageId();
                return  new CoursePublicResult(CommonCode.SUCCESS,preViewURL);
        }else{
            return  new CoursePublicResult(CommonCode.FAIL,null);
        }
    }

    /**
     * 抽取公共的组装CmsPage的方法
     * @param courseId
     * @return
     */
    private CmsPage generateCmsPage(String courseId) {
        //1.利用Feign远程调用CMS系统进行生成CMSPage，然后获取返回的生成页面的id，用于后续的预览静态化做准备
        //利用课程id查询数据扩充cmspage数据
        CourseBase courseBase = courseRepository.findOne(courseId);
        if(courseBase==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("加载预览页面URL地址异常，异常");
        }
        CmsPage  cmsPage=new CmsPage();
        cmsPage.setSiteId(this.publish_siteId);
        cmsPage.setDataUrl(this.publish_dataUrlPre+courseId);
//        cmsPage.setHtmlFileId(this.);
        cmsPage.setPageAliase(courseBase.getName());
//        cmsPage.setPageCreateTime(this.p);
//        cmsPage.setPageHtml();
        cmsPage.setPagePhysicalPath(this.publish_page_physicalpath);
//        cmsPage.setPageStatus(this.);
//        cmsPage.setPageTemplate(this.p);
        cmsPage.setPageWebPath(this.publish_page_webpath);
        cmsPage.setTemplateId(this.publish_templateId);
        cmsPage.setPageName(courseId+".html");
        return cmsPage;
    }

    /**
     * 利用Reign调用CMS
     * @param courseId
     * @return
     */
    @Transactional
    public CoursePublicResult loadAccessUrlAfterPublish( String courseId) {
        //1.此处构建cmspage对象
        CmsPage cmsPage = this.generateCmsPage(courseId);
        //2.此处进行一键快速发布
        CmsPublishResult cmsPublishResult = cmsPageClient.OneKeyQuickPublish(cmsPage);
        if(!(cmsPublishResult!=null&&cmsPublishResult.isSuccess())){
            return new CoursePublicResult(CommonCode.FAIL,null);
        }
        //此处成功调用一键发布的时候，需要进行更新courseid对应的课程表
        CourseBase courseBase = courseRepository.findOne(courseId);
        courseBase.setStatus("202002");//更新为发布状态
        courseRepository.save(courseBase);
        String pageUrl = cmsPublishResult.getPageUrl();
        /*
                此处追加进行发布后的处理待索引表
                思路：
                    1.进行组装待索引表对象，预备进行保存数据库
                    2.进行数据的组装涉及到
                   coursePub:
                                courseBase
                                courseMarket
                                teachPlan
                                            private String id;
                                            private String name;
                                            private String users;
                                            private String mt;
                                            private String st;
                                            private String grade;
                                            private String studymodel;
                                            private String teachmode;
                                            private String description;
                                            private String pic;//图片
                                            private Date timestamp;//时间戳
                                            private String charge;
                                            private String valid;
                                            private String qq;
                                            private Float price;
                                            private Float price_old;
                                            private String expires;
                                            private String teachplan;//课程计划
                                            @Column(name="pub_time")
                                            private String pubTime;//课程发布时间

         *
         */


        CoursePub  coursePub=  this.injectCoursePub(courseId);

        //拿到组装后的数据进行保存----------待索引表
       CoursePub coursePub_new=this.saveCoursePub(courseId,coursePub);
        if(coursePub_new==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        //课程发布的时候将课程对应的媒资信息进行同步媒资信息待索引表----进行原则是先删除后同步，CourseMediaPub是ES采集数据的表
        //1.删除pub表中的原本对应的信息
        //2.将media中的信息进行同步到pub中
        this.synchronizedTeachMediaPub(courseId);




        return   new CoursePublicResult(CommonCode.SUCCESS,pageUrl);
    }

    /**
     * 对数据进行同步
     * @param courseId
     */
    private void synchronizedTeachMediaPub(String courseId) {

        //1.首先查出来teachplanMedia中的数据
        List<TeachplanMedia> teachplanMediaList = teachPlanMediaRepository.findByCourseId(courseId);
        if(teachplanMediaList==null||teachplanMediaList.size()<=0){
            ExceptionCastUtils.throwException(new CustomException(CourseCode.COURSE_PUBLISH_SYSNCHRONIZEDTEACHPLANMEDIA_ERROR));
        }
        //2.如果存在信息记录则进行先删除后执行
        long rows = teachPlanMediaPubRepository.deleteByCourseId(courseId);
        //2.1将步骤1中查询的数据进行转换为pub集合

        List<TeachplanMediaPub>teachplanMediaPubList= new ArrayList<>();
        try {
            for (TeachplanMedia  media:teachplanMediaList) {
                //每来一个就进行一次转换
                TeachplanMediaPub  teachplanMediaPub = new TeachplanMediaPub();
                //执行数据的拷贝复制
                BeanUtils.copyProperties(teachplanMediaPub,media);
                //追加一个属性值的注入
                teachplanMediaPub.setTimestamp(new Date());
                //将数据保存到之前声明的集合中
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("课程发布之疼同步媒资信息资源的时候，进行数据转换的时候出现异常，{}",e.getMessage());
        }
        //2.2进行数据更新到teachplanMediaPub中
        List<TeachplanMediaPub> save = teachPlanMediaPubRepository.save(teachplanMediaPubList);
        if(save==null||save.size()<=0){
            ExceptionCastUtils.throwException(new CustomException(CourseCode.COURSE_PUBLISH_SYSNCHRONIZEDTEACHPLANMEDIA_ERROR));
        }

    }

    @Autowired
    CoursePubRepository  coursePubRepository;
    /**
     * 真正执行保存的操作-----此处注意coursepub的id是课程id
     * @param  courseId coursePub
     * @return
     */
    private CoursePub saveCoursePub(String courseId,CoursePub coursePub) {

        if(StringUtils.isEmpty(courseId)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("创建待索引表时候courseid参数为空，异常");
        }
        if(coursePub==null){
            logger.error("创建待索引表时候coursePub参数为空，异常");
            return null;
        }
        //如果没有id，则说明一个问题就是新增
        CoursePub one = coursePubRepository.findOne(courseId);
        if(one==null){//如果查询不出则标明是新建

            coursePub.setId(courseId);
            return coursePubRepository.save(coursePub);

        }else{//如果存在则进行更新
            //1.赋值数据到查询出来的对象中
            try {
                BeanUtils.copyProperties(one,coursePub);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //2.将课程id进行赋值，预防丢失数据
            one.setId(courseId);
            return coursePubRepository.save(one);
        }


    }


    /**
     * 发布服务器之后进行保存或者更新对应的待索引数据库表coursepub
     *              此处是进行组装数据
     * @param courseId
     * @return
     */
    private CoursePub injectCoursePub(String courseId) {
        /**
         * 此处的待索引表涉及到三张表的数据：
         *              courseBase
         *              courseMarket
         *              teachplan
         */
        CoursePub  coursePub=new CoursePub();
        //1.查询courseBase---将course字段组装到coursepub上面
        CourseBase courseBase = courseRepository.findOne(courseId);
        if(courseBase!=null){
            try {
                BeanUtils.copyProperties(coursePub,courseBase);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //2.查询courseMarket----将course字段组装到coursepub上面
        CourseMarket courseMarket = courseMarketRepository.findOne(courseId);
        if(courseMarket!=null){
            try {
                BeanUtils.copyProperties(coursePub,courseMarket);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        //3.查询teach plan----将course字段组装到coursepub上面
        try {
            TeachplanNode teachplanNode = teachplanMapper.teachplanList(courseId);
            //将其转换为json串存储在coursepub中
            String jsonString = JSON.toJSONString(teachplanNode);
            coursePub.setTeachplan(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //4.查询图片信息进行
        CoursePic coursePic = coursePicRepository.findOne(courseId);
        if(coursePic!=null){
            try {
                BeanUtils.copyProperties(coursePic,coursePub);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        coursePub.setId(courseId);

        //5.设置课程发布时间和时间戳
        coursePub.setPubTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        coursePub.setTimestamp(new Date());

        return  coursePub;
    }

    /**
     * 保存课程计划与视频对应的关系
     * @param teachplanMedia
     * @return
     */
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia) {

        if(teachplanMedia==null||StringUtils.isEmpty(teachplanMedia.getTeachplanId())){
            //参数异常
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        String teachplanId = teachplanMedia.getTeachplanId();
        TeachplanMedia one = teachPlanMediaRepository.findOne(teachplanId);
        if(one==null){
           one = new TeachplanMedia();
        }
        //校验叶子结点
        Teachplan teachplan = teachplanRepository.findOne(teachplanId);
        String grade = teachplan.getGrade();
        if(!"3".equalsIgnoreCase(grade)){//判断是不是叶子节点
            return  new ResponseResult(CommonCode.saveMedia_isNotLeafNode);
//            ExceptionCastUtils.throwException(new CustomException(CommonCode.saveMedia_isNotLeafNode));
        }
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        one.setTeachplanId(teachplanMedia.getTeachplanId());
        teachPlanMediaRepository.save(one);







        return new ResponseResult(CommonCode.SUCCESS);
    }
}
