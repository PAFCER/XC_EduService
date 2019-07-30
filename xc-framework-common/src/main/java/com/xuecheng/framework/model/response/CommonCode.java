package com.xuecheng.framework.model.response;

import lombok.ToString;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 18:33.
 * @Modified By:
 */

@ToString
public enum CommonCode implements ResultCode{

    SUCCESS(true,10000,"操作成功！"),
    FAIL(false,11111,"操作失败！"),
    UNAUTHENTICATED(false,10001,"此操作需要登陆系统！"),
    UNAUTHORISE(false,10002,"权限不足，无权操作！"),
    IllegalArgument_BindArgs(false,10003,"参数绑定异常！"),
    IllegalArgument_NoExist(false,10004,"数据库没有对应记录异常！"),
    IllegalArgument_Ref_Null(false,10006,"参数中引用对象为null，异常！"),
    GenerateHTML_Error(false,10007,"生成静态页面，异常！"),
    TemplateContent_null(false,10008,"模板内容为空，异常！"),
    StoreNewTemplate_ERROR(false,10009,"存储静态化后的页面失败，异常！"),
    SaveNewTeachPlan_ERROR(false,10014,"保存新的课程计划失败，异常！"),
    updateCMSPAGE_HTMLFILEID_ERROR(false,10010,"静态化后更新页面静态文件id信息失败，异常！"),
    loadHTML_ERROR(false,10011,"VUE初始化加载静态HTML失败，异常！"),
    loadSiteList_ERROR(false,10012,"加载站点列表失败，异常！"),
    loadTemplateList_ERROR(false,10013,"加载模板列表失败，异常！"),
    createChunkFolderError(false,10111,"上传文件之，创建存储的块目录失败，异常！"),
    createMergeFolderError(false,10112,"上传文件之，创建存储的合并文件目录失败，异常！"),
    createMergeFileError(false,10113,"上传文件之，创建存储的合并文件失败，异常！"),
    generateMergeFileError(false,10114,"上传文件之，将块文件中流信息合并到合并文件中失败，异常！"),
    saveUploadFileInfoToMediaFileTableError(false,10115,"上传文件之，合并文件之后进行文件的信息保存到数据库中失败，异常！"),
    deleteChunksFolderNotFound_ERROR(false,10116,"上传文件之，保存数据库信息之后进行碎片的删除时候目录找不到，异常！"),
    UploadFileMD5CheckFail_ERROR(false,10117,"上传文件之，校验文件MD5失败，异常！"),
    deleteChunksFileAndFolder_ERROR(false,10118,"上传文件之，删除chunks文件和chunks目录失败，异常！"),
    uploadFile_ERROR(false,10044,"上传文件失败，异常！"),
    ParseJSONMAP_ERROR(false,10045,"转换前端的metadata为Map，异常！"),
    SaveFileSystemToMongoDB_ERROR(false,10046,"将上传文件信息存储到mongodb数据库失败，异常！"),
    loadObjectIsNUll(false,10047,"根据课程id查询数据库没有找到对应的数据库信息，异常！"),
    saveMedia_isNotLeafNode(false,10048,"请选择叶子结点进行关联视频！"),

    Page_Exist(false,10005,"页面已经存在异常！"),
    SERVER_ERROR(false,99999,"抱歉，系统繁忙，请稍后重试！");
//    private static ImmutableMap<Integer, CommonCode> codes ;
    //操作是否成功
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private CommonCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }
    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }


}
