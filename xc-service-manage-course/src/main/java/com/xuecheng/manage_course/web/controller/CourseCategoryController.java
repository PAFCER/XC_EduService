package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.CourseCategoryControllerAPI;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/1.
 */
@RestController
public class CourseCategoryController implements CourseCategoryControllerAPI{

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Override
    public CategoryNode categoryList() {

        return courseCategoryService.categoryList();
    }
}
