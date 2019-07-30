package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by hotwater on 2018/7/6.
 *   构建预览和发布页面的响应模型
 */
@Data
@ToString
@NoArgsConstructor
public class CoursePublicResult extends ResponseResult{

    private String preURL;
    public   CoursePublicResult(CommonCode commonCode,String preURL){
        super(commonCode);
        this.preURL=preURL;
    }


}
