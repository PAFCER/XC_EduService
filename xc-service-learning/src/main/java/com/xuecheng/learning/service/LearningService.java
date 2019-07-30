package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.learning.GetMediaResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.Client.CourseSearchClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hotwater on 2018/7/16.
 */
@Service
public class LearningService {
    @Autowired
    CourseSearchClient courseSearchClient;
    //获取课程
    public GetMediaResult getMedia(String courseId, String teachplanId) {
//校验学生的学习权限。。是否资费等
//调用搜索服务查询
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
        if(teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())){
            //获取视频播放地址出错
            ExceptionCastUtils.throwException(new CustomException(CourseCode.COURSE_MEDIS_VIDEOURLISNULL));
        }
        return new GetMediaResult(CommonCode.SUCCESS,teachplanMediaPub.getMediaUrl());
    }
}