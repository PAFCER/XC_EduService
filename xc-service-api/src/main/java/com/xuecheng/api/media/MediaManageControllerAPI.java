package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hotwater on 2018/7/12.
 *
 * 统一的媒体处理接口-----上传文件接口
 */
@Api("媒资管理接口，提供媒体资源的增删改查等等接口规范")
@RestController
public interface MediaManageControllerAPI {

    //统一接口前缀
    final  String  MediaPre="/media";

    @ApiOperation("进行查询媒体资源列表")
    @GetMapping(MediaPre+"/list/{page}/{size}")
    public QueryResponseResult findAll(
                @PathVariable("page") Integer page,
                @PathVariable("size")Integer size,
                QueryMediaFileRequest queryMediaFileRequest);

    @ApiOperation("进行媒体资源的再次发送处理，发送消息队列")
    @GetMapping(MediaPre+"/manualOperation/{id}")
    public ResponseResult  manualOperation(@PathVariable("id")String id);
}
