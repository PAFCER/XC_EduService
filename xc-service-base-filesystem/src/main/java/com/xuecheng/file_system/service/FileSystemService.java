package com.xuecheng.file_system.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.file_system.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/4.
 */
@Service
@Transactional
public class FileSystemService {

    private  final  Logger  logger = LoggerFactory.getLogger(FileSystemService.class);
//    xuecheng:
//    fastdfs:
//    connect_timeout_in_seconds: 5
//    network_timeout_in_seconds: 30
//    charset: UTF-8
//    tracker_servers: 192.168.101.65:22122
//

    /**
     * 进行加载数据---连接fastdfs文件系统
     */
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private  String   connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private  String   network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private  String  charset;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private  String  tracker_servers;


    private  void  initClientGlobalConfig(){
        try {
        //解析上述的配置
        String[] split = tracker_servers.split(",");
        InetSocketAddress [] inetSocketAddresses= new InetSocketAddress[split.length];
        int x=0;
        for (String server:split) {
            String[] server_url_port = server.split(":");
            String url=server_url_port[0];
            Integer port=Integer.parseInt(server_url_port[1]);
            inetSocketAddresses[x++]=new InetSocketAddress(url,port);
        }
            ClientGlobal.setG_charset(charset);//编码
            ClientGlobal.setG_connect_timeout(Integer.parseInt(connect_timeout_in_seconds));//连接超时
            ClientGlobal.setG_network_timeout(Integer.parseInt(network_timeout_in_seconds));//网络超时
            ClientGlobal.initByTrackers(inetSocketAddresses);//初始化
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件系统配置文件初始化失败，{}"+e.getMessage());
        } catch (MyException e) {
            e.printStackTrace();
            logger.error("文件系统配置文件初始化失败，{}"+e.getMessage());
        }
    }
    @Autowired
    private FileSystemRepository  fileSystemRepository;

    /**
     * 此处的上传文件需要两步走：
     * 1.首先上传我们的文件到文件系统fastDFS中，
     * 2.另外将返回的数据信息id存储到我们的mongodb数据库中，此处的1和2两部可以在一个上传即一起实现即可
     * 3.我们将上述上传后的mongodb数据库返回的信息返回页面，然后前端页面依据成功情况将执行将我们的信息存储在
     *      MySQL数据库中添加信息，存储文件与数据库表的对照关系
     * mongodb数据库中
      * @param file
     * @param businesskey
     * @param filetag
     * @param metadata
     * @return
     */
    public UploadFileResult uploadFile(MultipartFile file, String businesskey, String filetag, String metadata) {
        //1.初始化调用服务的配置工具
        this.initClientGlobalConfig();
        if(file==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("上传文件为空：");
        }
        //2.上传文件---单独抽取一个方法
      String fileId=  this.realUpload(file);
        if(StringUtils.isEmpty(fileId)){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.uploadFile_ERROR));
        }
        //3.进行处理mongdb数据存储
        FileSystem fileSystem = this.dealMongDB(fileId, file, businesskey, filetag, metadata);

        if(fileSystem==null){
            return   new UploadFileResult(CommonCode.FAIL,null);
        }
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    /**
     * 存储文件信息到mongoDB数据库
     * @param fileId
     * @param file
     * @param businesskey
     * @param filetag
     * @param metadata
     */
    private FileSystem dealMongDB(String fileId, MultipartFile file, String businesskey, String filetag, String metadata) {

        if(file==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("上传文件为空：");
        }
        if(fileId==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
            logger.error("存储后的文件id为空：");
        }
        //进行fileSyetem数据组装完成对于数据的存储
        //获取文件名称
        String originalFilename = file.getOriginalFilename();
        if(StringUtils.isNotEmpty(originalFilename)&&originalFilename.lastIndexOf("/")!=-1){
            //此处兼容了浏览器的问题，带windows盘符路径
            originalFilename=originalFilename.substring(originalFilename.lastIndexOf("/"+1));
        }
        //获取文件类型
        String fileType = file.getContentType();
        FileSystem fileSystem=new FileSystem();
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileId(fileId);
        fileSystem.setFileName(originalFilename);
        fileSystem.setFileSize(file.getSize());
        fileSystem.setFiletag(filetag);
        fileSystem.setFilePath(fileId);//此处是否是一样的？？？
        fileSystem.setFileType(fileType);
        //转换map
        try {
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCastUtils.throwException(new CustomException(CommonCode.ParseJSONMAP_ERROR));
            logger.error("转换数据异常：{}",e.getMessage());
        }
        //
        FileSystem save = fileSystemRepository.save(fileSystem);

        if(save==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.SaveFileSystemToMongoDB_ERROR));
            logger.error("将上传文件信息存储到MongoDB数据库失败");
        }
        return save;
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    private String realUpload(MultipartFile file) {
        try {
            //1.创建客户端trackerClient
            TrackerClient  trackerClient  = new TrackerClient();
            //2.创建trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //3.创建存储服务端
            StorageServer  storageServer  =trackerClient.getStoreStorage(trackerServer);
            //4.创建存储客户端
            StorageClient1  storageClient1=new StorageClient1(trackerServer,storageServer);
            //5.执行上传
            //byte[] file_buff, String file_ext_name, NameValuePair[] meta_list
            //5.1获取文件字节数组
            byte[] bytes = file.getBytes();
            //5.2获取文件名称----此处并没有使用
            String originalFilename = file.getOriginalFilename();
            if(StringUtils.isNotEmpty(originalFilename)&&originalFilename.lastIndexOf("/")!=-1){
              //此处兼容了浏览器的问题，带windows盘符路径
                originalFilename=originalFilename.substring(originalFilename.lastIndexOf("/"+1));
            }
            //5.3获取文件扩展名
            String ext_name = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileId = storageClient1.upload_file1(bytes, ext_name, null);

        return fileId;
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionCastUtils.throwException(new CustomException(CommonCode.uploadFile_ERROR));
            logger.error("上传文件失败，{}",e.getMessage());
        }catch (MyException e1){
            e1.printStackTrace();
            ExceptionCastUtils.throwException(new CustomException(CommonCode.uploadFile_ERROR));
            logger.error("上传文件失败，{}",e1.getMessage());

        }
        return null;
    }


}
