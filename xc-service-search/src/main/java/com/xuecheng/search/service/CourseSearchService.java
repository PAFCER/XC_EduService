package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hotwater on 2018/7/10.
 */
@Service
public class CourseSearchService {
    //索引库名称_xc_course
    @Value("${xuecheng.elasticsearch.course.index}")
    private String index_name;
    //索引库名称_xc_course_media
    @Value("${xuecheng.elasticsearch.media.index}")
    private String index_name_media;
    //索引库类别
    @Value("${xuecheng.elasticsearch.course.type}")
    private  String type;
    //搜索的结果需要包含的字段
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_fields;
    //检索字段列表
    @Value("${xuecheng.elasticsearch.course.search_field}")
    private  String search_field;

    //记录日志对象
    private  final Logger  logger = LoggerFactory.getLogger(CourseSearchService.class);


    @Autowired
    RestHighLevelClient  restHighLevelClient;

    public QueryResponseResult search(Integer page, Integer size, CourseSearchParam courseSearchParam) {
        //1.处理参数page/size
        if(page<=0){
            page=1;
        }
        page=page-1;
        int offset=page*size;
        if(size<=0){
            size=12;
        }
        //2.处理参数courseSearchParam---为空则初始化一个参数,防止后续代码空指针异常
        if(courseSearchParam==null){
            courseSearchParam=new CourseSearchParam();
        }
        //3.开始构建查询的体系
        //3.1 首先解决关键字查询，我们肯定是要明确关键字查询会涉及到分词并需要高亮显示，
        //3.2 另外我们需要明确需要涉及到查询三个字段，name description teachplan三个
        //3.3 我们需要再明确一个就是构建我们的搜索对象
        //此处涉及到一个绑定索引库的操作，此处写死不好，我们利用配置文件进行注入
        SearchRequest  searchRequest=new SearchRequest(index_name);
        //指定索引库
        searchRequest.indices(index_name);
        //指定文档类型
        searchRequest.types(type);
        //3.4创建搜索资源构建者---用于配置当前搜索对象的一些配置比如筛选字段资源等等
        SearchSourceBuilder  searchSourceBuilder = new SearchSourceBuilder();
        //3.5利用上述的构建者进行一系列的配置
        //分页查询配置
        searchSourceBuilder.from(offset);
        searchSourceBuilder.size(size);

        //处理高亮字段
        HighlightBuilder highlightBuilder= new HighlightBuilder();
        highlightBuilder.preTags("<font  class= 'selfClass'>");
        highlightBuilder.postTags("</font>");
        //高亮需要指定字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //
        //筛选字段资源,此处同样选择配置文件注入的形式，
        //将注入的数据进行转换
        String[] includes = new String []{};
        //如果配置了就采用配置的字段
        if(StringUtils.isNotEmpty(source_fields)){
            //利用此处进行分割数据
            includes = source_fields.split(",");
        }
        searchSourceBuilder.fetchSource(includes,new String[]{});

        //3.6指定查询
        //3.6.1首先解决关键字
        //3.6.2处理检索字段列表
        //3.6 下面进行指定布尔查询  //错误代码要警惕哟哟哟
//        BoolQueryBuilder  boolQueryBuilder =new BoolQueryBuilder();//错误代码要警惕哟哟哟
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String[]searchFields=new String[]{};
        if(StringUtils.isNotEmpty(search_field)){
            searchFields=search_field.split(",");
        }
        MultiMatchQueryBuilder multiMatchQueryBuilder =null;
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
        multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), searchFields);
        //提升name权重
            multiMatchQueryBuilder.field("name",10);
            //设置匹配度
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //3.6.1将多字段查询绑定到布尔查询
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //3.6.2过滤字段---其实就是精准匹配的过滤器实现
        //涉及到几个过滤：一级分类，二级分类，课程等级等等
        //一级分类
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
//            TermQueryBuilder termQuery = QueryBuilders.termQuery("mt", courseSearchParam.getMt());
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        //二级分类
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
//            TermQueryBuilder termQuery = QueryBuilders.termQuery("st", courseSearchParam.getSt());
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        //课程等级
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
//            TermQueryBuilder termQuery = QueryBuilders.termQuery("grade", courseSearchParam.getGrade());
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }
        //6.4处理完毕上述指定操作进行绑定
        //6.4.1绑定布尔查询到对应的搜索资源构建者上
        searchSourceBuilder.query(boolQueryBuilder);
        //6.4.2绑定搜索资源构建者到搜索对象上
        searchRequest.source(searchSourceBuilder);
        //进行查询
        SearchResponse searchResponse =null;
        //定义一个对象进行承载
        //进行存储数据列表的数据集合
        List<CoursePub> list=new ArrayList<>();
        //承载返回数据的对象---list和total两个属性
        QueryResult<CoursePub>queryResult= new QueryResult<>();
        try {
            searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            //获取匹配的数据记录数
            long totalHits = searchHits.getTotalHits();
            //将数据注入到返回对象属性中
            queryResult.setTotal(totalHits);
            //获取匹配的数据集合
            SearchHit[] hits = searchHits.getHits();
            //下面进行遍历然后将数据注入封装到数据对象中即可
            for (SearchHit hit:hits) {
                //获取高亮部分字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                //构建一个map用于承接高亮部分数据处理
                Map<String,String> realHighlight=new HashMap<>();
                //处理高亮部分
                for (String key : highlightFields.keySet()) {
                    HighlightField highlightField = highlightFields.get(key);
                    Text[] fragments = highlightField.getFragments();
                    //sb用于接收处理高亮字段
                    StringBuffer sb = new StringBuffer();
                    for (Text text:fragments) {
                        sb.append(text.string());
                    }
                    //承接转换完毕的map数据
                    realHighlight.put(key,sb.toString());
                }
                //获取正常的字段
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //对于上述的高亮部分和普通部分，我们都是利用对象进行保存每一个map中的数据，也就是说此处的
                //一个hit就代表一个对象，我们此处利用反射机制可以进行数据的封装注入，对于高亮部分的处理
                // 我们可以利用覆盖注入将高亮部分的数据进行替换传统的字段value值，即当他们的key相同就让他进行高亮替换传统即可
                for (String key:sourceAsMap.keySet()) {
                    //高亮部分包含该键并且所对应的值不为空--进行替换
                    if(realHighlight.containsKey(key)&&StringUtils.isNotEmpty(realHighlight.get(key))){
                        //替换为高亮部分数据
                        sourceAsMap.put(key,realHighlight.get(key));
                    }
                }
                //每一次hit构建一个coursePub，然后进行反射注入数据后进行添加到list中
                CoursePub  coursePub= new CoursePub();
                //此处注意前后需要注意了，关于beanutils具有几个版本，我们要使用的是apache的
                // 两个参数第一个是目标对象，第二个是提供数据的源对象

                //todo
                try {
                    BeanUtils.copyProperties(coursePub,sourceAsMap);
                    //处理完一个进行注入到list集合中一个
                    list.add(coursePub);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
            //将数据列表数据注入到返回对象中
            queryResult.setList(list);

            //构建返回对象return
            return  new QueryResponseResult(CommonCode.SUCCESS,queryResult);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("搜索课程信息的时候出现异常，{}",e.getMessage());
            return  new QueryResponseResult(CommonCode.FAIL,new QueryResult());
        }
    }

    /**
     * 依据课程id进行查询ES课程信息---实际又有的信息就是教学计划
     * @param courseId
     * @return
     */
    public Map<String,CoursePub> findCoursePubById(String courseId) {
        //构建搜索对象
        SearchRequest  searchRequest = new SearchRequest();
        //指定查询的索引库名称
        searchRequest.indices(index_name);
        //指定文档类型
        searchRequest.types(type);
        //构建查询资源构建者
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //进行精准匹配查询利用课程id
        searchSourceBuilder.query(QueryBuilders.termsQuery("id", courseId));
        //将查询资源构建者与其查询请求进行绑定
        searchRequest.source(searchSourceBuilder);
        //进行查询
        //构建一个map进行承载解析后的数据
        Map<String,CoursePub> coursePubMap=new HashMap<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] hits1 = hits.getHits();
            //利用查询出来的数据进行分析数据
            for (SearchHit hit:hits1) {
                CoursePub coursePub= new CoursePub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //利用反射小工具进行数据转换注入
                //此处涉及到String转换Date，进行单独处理即可
                ConvertUtils.register(new Converter() {

                    @Override
                    public Object convert(Class type, Object value) {
                        //将数据串进行转换
                        String substring = value.toString().replaceAll("T", " ").substring(0,value.toString().lastIndexOf("."));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            return simpleDateFormat.parse(substring);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }
                }, Date.class);
                BeanUtils.populate(coursePub,sourceAsMap);
                 //将数据注入到map集合中
                 coursePubMap.put(coursePub.getId(),coursePub);
                }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    }
        return coursePubMap;
    }

    /**
     * 查询媒资信息
     * @param teachplanIds
     * @return
     */
    public List<TeachplanMediaPub> getmedia(String[] teachplanIds) {

        //构建查询对象
        SearchRequest searchRequest = new SearchRequest();
        //指定索引库
        searchRequest.indices(index_name_media);
        //指定文档类型
        searchRequest.types(type);
        //构建查询条件
        SearchSourceBuilder  searchSourceBuilder= new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));

        //将查询对象与资源构建者进行绑定
        searchRequest.source(searchSourceBuilder);

        //进行查询
        SearchResponse searchResponse =null;
        //定义一个数据集合进行承载媒资信息
        List<TeachplanMediaPub>teachplanMediaPubList= new ArrayList<>();
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest);
            SearchHits hits = search.getHits();
            SearchHit[] hits1 = hits.getHits();
            //利用查询出来的数据进行分析数据------由于此处的字段Pojo和ES中的数据映射不匹配，因此不能够与原先的一样使用BeanUtils进行反射注入
            for (SearchHit hit:hits1) {
                TeachplanMediaPub  teachplanMediaPub  = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                teachplanMediaPub.setTeachplanId((String )sourceAsMap.get( "teachplan_id"));//  "402885816347f814016348d68bad0000"
               teachplanMediaPub.setMediaId((String) sourceAsMap.get("media_id"));// -> "595f5d59f5a1f3f9db78a96a973d6dea"
                teachplanMediaPub.setCourseId((String) sourceAsMap.get("courseid"));// -> "4028e581617f945f01617f9dabc40000"
                teachplanMediaPub.setMediaUrl((String)sourceAsMap.get("media_url"));// -> "/5/9/595f5d59f5a1f3f9db78a96a973d6dea/hls\595f5d59f5a1f3f9db78a96a973d6dea.m3u8"
                teachplanMediaPub.setMediaFileOriginalName((String) sourceAsMap.get("media_fileoriginalname"));// -> "luence.avi"
                //此处利用反射注入是行不通的因为数据字段属性名称不一致，
                //将数据保存到集合中
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        }  catch (Exception e) {
            e.printStackTrace();
            ExceptionCastUtils.throwException(new CustomException(CourseCode.SEARCHES_TEACHPLANMEDIAPLAN_ERROR));
        }

        return teachplanMediaPubList;

    }
}
