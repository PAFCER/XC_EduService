package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by hotwater on 2018/7/1.
 */
public interface CourseCategoryControllerAPI {

    static  final String Course_PRE="/category/page";

    @ApiOperation("添加课程时候需要选择课程分类，此处是三层分类，因此需要构建前端要求的树形结构")
    @GetMapping(Course_PRE+"/categoryList")
    public CategoryNode categoryList();

}
