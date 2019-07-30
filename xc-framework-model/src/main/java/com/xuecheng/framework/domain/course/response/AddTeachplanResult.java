package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

/**
 * Created by wp on 2018/7/03.
 * 添加课程计划的返回值对象实体
 */
@Data
@ToString
public class AddTeachplanResult extends ResponseResult {
    public AddTeachplanResult(ResultCode resultCode, String courseid) {
        super(resultCode);
        this.courseid = courseid;
    }
    private String courseid;

}
