package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by hotwater on 2018/7/12.
 *
 * 统一的媒体处理接口-----上传文件接口
 */
@Api("媒资管理接口，提供媒体资源的上传接口规范")
@RestController
public interface MediaUploadControllerAPI {

    /**
     1.对于实现上传接口的实现我们前端依赖于WebUploader，而webUploader实现了对于文件上传的API
        很好的实现，其中涉及到一个断点续传，另外断点续传涉及到
        几个钩子函数：
     参数：
     fileMd5:this.fileMd5,
     fileName: file.name,
     fileSize:file.size,
     mimetype:file.type,
     fileExt:file.ext
     1.上传前的注册：检测是否已经存在该文件（比对数据库及其服务器磁盘），此处的文件md5是数据库的记录id
                  如果已经存在该文件则告知文件已经存在,此处利用返回对象参数success进行判定成功与否
                  检测存储目录是否存在，不存在则创建
     参数：
     // 文件唯一表示
     fileMd5:this.fileMd5,
     // 当前分块下标
     chunk:block.chunk,
     // 当前分块大小
     chunkSize:block.end-block.start
     2.上传分块前的检测分块是否存在
                    检测分块是否存在，存在则直接返回true，此处还可以检测存在的分块数据的与前端传递的参数的对比是否丢失数据
                    检测存储分块目录是否存在
                3.上传分块，将分块数据存储到指定的存储分块的目录下
     参数：
     fileMd5:this.fileMd5,
     fileName: file.name,
     fileSize:file.size,
     mimetype:file.type,
     fileExt:file.ext
                4.进行合并分块，分块数据全部上传完毕就进行分块数据的合并，合并完毕之后进行数据MD5 的校验
                    合成之前最好检测是否已经存在该文件，有则进行删除
                    校验成功则进行进行保存数据库，将上传后的文件信息存储到数据库，
                    然后进行分块数据进行删除
     注册：前端出啊等你


     */
    //统一接口前缀
    final  String  MediaPre="/media/upload";

    /**
     * 上传文件第一个方法注册即检测文件
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    @ApiOperation("上传文件前的检测")
    @PostMapping(MediaPre+"/register")
    public ResponseResult  register(
                                @RequestParam("fileMd5")String fileMd5,//文件唯一标识
                                @RequestParam("fileName")String fileName,//文件名称
                                @RequestParam("fileSize")Long fileSize,//文件大小
                                @RequestParam("mimetype")String mimetype,//文件类型
                                @RequestParam("fileExt")String fileExt//文件扩展名
                                    );

    /**
     * 上传分块前的检测分块信息是否存在
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    @ApiOperation("上传分块前的检测分块文件的信息，支持断点续传的核心")
    @PostMapping(MediaPre+"/checkchunk")
    public CheckChunkResult  checkchunk(
                                @RequestParam("fileMd5") String fileMd5,//文件唯一标识
                                @RequestParam("chunk") Long chunk,//文件块下标
                                @RequestParam("chunkSize") Long chunkSize//文件块大小
                                    );

    /**
     *上传分块文件
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    @ApiOperation("上传文件分块")
    @PostMapping(MediaPre+"/uploadchunk")
    public  ResponseResult  uploadchunk(
                                    @RequestParam("file")MultipartFile file,
                                    @RequestParam("fileMd5")String fileMd5,
                                    @RequestParam("chunk")Long chunk
                                    );

    /**
     * 进行分块合并
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    @ApiOperation("进行分块合并")
    @PostMapping(MediaPre+"/mergechunks")
    public  ResponseResult  mergechunks(
                                    @RequestParam("fileMd5")String fileMd5,//文件唯一标识
                                    @RequestParam("fileName")String fileName,//文件名称
                                    @RequestParam("fileSize")Long fileSize,//文件大小
                                    @RequestParam("mimetype")String mimetype,//文件类型
                                    @RequestParam("fileExt")String fileExt//文件扩展名
                                    );


}
