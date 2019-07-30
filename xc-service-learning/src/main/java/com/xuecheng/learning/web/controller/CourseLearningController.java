package com.xuecheng.learning.web.controller;

import com.xuecheng.api.learning.CourseLearningControllerAPI;
import com.xuecheng.framework.domain.learning.GetMediaResult;
import com.xuecheng.learning.service.LearningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/16.
 */
@RestController
public class CourseLearningController implements CourseLearningControllerAPI{
    @Autowired
    LearningService learningService;
    @Override
    public GetMediaResult getmedia(@PathVariable String courseId, @PathVariable String
            teachplanId) {
//获取课程学习地址
        return learningService.getMedia(courseId, teachplanId);
    }
}