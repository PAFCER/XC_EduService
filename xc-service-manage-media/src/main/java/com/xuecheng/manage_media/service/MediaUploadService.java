package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.exception.CustomException;
import com.xuecheng.framework.exception.ExceptionCastUtils;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * Created by hotwater on 2018/7/12.
 */
@Service
public class MediaUploadService {
    //上传文件的根路径
    @Value("${xc-service-manage-media.upload-location}")
    private  String uploadLocation;
    //mongodb数据库操作对象
    @Autowired
    private MediaFileRepository  mediaFileRepository;

    //注入发送消息队列的消息的对象
    @Autowired
    private RabbitTemplate  rabbitTemplate;

    //定义视频处理的队列
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    public String QUEUE_MEDIA_VIDEO;
    //定义视频处理路由
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String ROUTING_MEDIA_VIDEO;

    /**
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
//        1.上传前的注册：检测是否已经存在该文件（比对数据库及其服务器磁盘），此处的文件md5是数据库的记录id
//                如果已经存在该文件则告知文件已经存在,此处利用返回对象参数success进行判定成功与否
//        检测存储目录是否存在，不存在则创建
        //1.查询数据库是否存在记录
        MediaFile mediaFile = mediaFileRepository.findOne(fileMd5);
        //2.查询服务器是否存在文件
        //获取文件存储目录：根路径/md5首字符/md5第二个字符/md5/
//        String fileFolder=getStoreFileFolder(uploadLocation,fileMd5);
        String fileAbsolutePath=getFileAbsolutePath(uploadLocation,fileMd5,fileExt);
        File checkfile= new File(fileAbsolutePath);
        if(checkfile.exists()&&mediaFile!=null){//如果存在则返回
            return new ResponseResult(CommonCode.FAIL);//文件存在返回false，也就是校验结果是存在文件
        }
        //不存在则进行下一步校验
        //是否存在目录
        String fileFolder = getStoreFileFolder(uploadLocation, fileMd5);
        File file = new File(fileFolder);
        if(!file.exists()){
            file.mkdirs();//创建目录
        }
        //返回告知不存在可以上传
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取文件的绝对路径
     * @param uploadLocation
     * @param fileMd5
     * @param fileExt
     * @return
     */
    private String getFileAbsolutePath(String uploadLocation, String fileMd5, String fileExt) {
        if(StringUtils.isNotEmpty(uploadLocation)&&StringUtils.isNotEmpty(fileMd5)){
            return new StringBuffer ().append(uploadLocation).append(fileMd5.substring(0,1)).append("/").append(fileMd5.substring(1,2)).append("/").append(fileMd5).append("/").append(fileMd5+"."+fileExt).toString();
        }
        //抛出异常
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        return null;
    }

    /**
     * 根据根路径及其文件的md5获取文件存储目录
     * @param uploadLocation
     * @param fileMd5
     * @return
     */
    private String getStoreFileFolder(String uploadLocation, String fileMd5) {

        if(StringUtils.isNotEmpty(uploadLocation)&&StringUtils.isNotEmpty(fileMd5)){
            return new StringBuffer ().append(uploadLocation).append(fileMd5.substring(0,1)).append("/").append(fileMd5.substring(1,2)).append("/").append(fileMd5).append("/").toString();
        }
        //抛出异常
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        return null;
    }

    public CheckChunkResult checnchunk(String fileMd5, Long chunk, Long chunkSize) {
        //获取存储块文件目录
        String chunkFileAbsolutePath = getChunkFileAbsolutePath(uploadLocation, fileMd5, chunk);
        File file = new File(chunkFileAbsolutePath);
        if(file.exists()){//存在该文件
            if(file.length()==chunkSize){//存在的话判定是否大小一致
                return  new CheckChunkResult(CommonCode.SUCCESS,true);
            }
            //大小不一致则进行不完整块的删除以避免后续的影响
            file.delete();
        }
        //不存在该文件则进行校验目录是否存在，不存在则进行创建目录
        String chunkFileFolder = getChunkFileFolder(uploadLocation, fileMd5);
        file = new File(chunkFileFolder);
        if(!file.exists()){
            boolean mkdirs = file.mkdirs();//创建目录
            if(!mkdirs){
                ExceptionCastUtils.throwException(new CustomException(CommonCode.createChunkFolderError));
            }

        }
        //进行返回页面展示告知块文件不存在
        return new CheckChunkResult(CommonCode.SUCCESS,false);
    }

    /**
     * 根据根路径及其块目录获取快文件存储目录
     * @param uploadLocation
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolder(String uploadLocation, String fileMd5) {
        if(StringUtils.isNotEmpty(uploadLocation)&&StringUtils.isNotEmpty(fileMd5)){
            return new StringBuffer ().append(uploadLocation).append(fileMd5.substring(0,1)).append("/").append(fileMd5.substring(1,2)).append("/").append(fileMd5).append("/").append("chunks").append("/").toString();
        }
        //抛出异常
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        return null;
    }

    /**
     *
     * 根据根路径及其块目录及其文件的md5换算出快文件存储绝对路径
     * @param uploadLocation
     * @param fileMd5
     * @param chunk
     * @return
     */
    private String getChunkFileAbsolutePath(String uploadLocation, String fileMd5,Long chunk) {
        if(StringUtils.isNotEmpty(uploadLocation)&&StringUtils.isNotEmpty(fileMd5)){
            return new StringBuffer ().append(uploadLocation).append(fileMd5.substring(0,1)).append("/").append(fileMd5.substring(1,2)).append("/").append(fileMd5).append("/").append("chunks").append("/").append(chunk).toString();
        }
        //抛出异常
        ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        return null;
    }

    /**
     * 复制块文件
     * @param file
     * @param fileMd5
     * @param chunk
     * @return
     */
    public ResponseResult uploadchunk(MultipartFile file, String fileMd5, Long chunk) {

        //上传文件----空参校验
        if(file==null){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.IllegalArgument_Ref_Null));
        }
        //进行块文件的生成保存
        InputStream  inputStream =null;
        FileOutputStream fileOutputStream=null;
        try {
            //获取文件输入流
            inputStream = file.getInputStream();
            //获取快文件的绝对路径
            String chunkFileAbsolutePath = getChunkFileAbsolutePath(uploadLocation, fileMd5, chunk);
            //进行创建块文件---此处不创建也是可以的，因为output stream会创建
            File chunkFile = new File(chunkFileAbsolutePath);
            if(!chunkFile.exists()){
                boolean newFile = chunkFile.createNewFile();//此处觉得没有必要写==
            }
            fileOutputStream = new FileOutputStream(chunkFile);
            //开始进行流数据的复制
            IOUtils.copy(inputStream, fileOutputStream,1024);//字节

        } catch (IOException e) {
            e.printStackTrace();

            return new ResponseResult(CommonCode.FAIL);//复制块文件失败

        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 合并块文件列表
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
            //根据根路径及其文件md5值获取块文件的列表
        List<File> fileList = getchunksList(uploadLocation, fileMd5);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    int compare = Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
                    if(compare>0){
                        return 1;
                    }
                    return -1;
                }
            });
            //获取目录是否存在
        String fileFolder = getStoreFileFolder(uploadLocation, fileMd5);
        if(!new File(fileFolder).exists()){
            boolean mkdirs = new File(fileFolder).mkdirs();
            if(!mkdirs){
                //创建文件失败
                ExceptionCastUtils.throwException(new CustomException(CommonCode.createMergeFolderError));
            }
        }
        //获取存储的合并的文件绝对路径
        String fileAbsolutePath = getFileAbsolutePath(uploadLocation, fileMd5, fileExt);
        File file = new File(fileAbsolutePath);
        //若合并后的文件之前居然存在，则先进行删除即可
        if(file.exists()){
            file.deleteOnExit();//删除文件
        }
       //进行创建合并文件
        if(!file.exists()){
            try {
                boolean newFile = file.createNewFile();
                if(!newFile){
                    ExceptionCastUtils.throwException(new CustomException(CommonCode.createMergeFileError));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //排序之后进行开始利用数据流整合合并
        //创建输入流
        RandomAccessFile  writer=null;
        //读取块文件的流
        RandomAccessFile  read=null;
        try {
            //写的流进行绑定文件
            writer= new RandomAccessFile(file,"rw");
            for (int x=0;x<fileList.size();x++){
            read= new RandomAccessFile(fileList.get(x),"r");
            //缓冲数组
            byte[]bytes=new byte[1024];
            //标记位
            int length=-1;
            while((length=read.read(bytes))!=-1){
                writer.write(bytes,0,length);//
            }
            //一个循环之后说明一个块文件读取完毕进行释放资源，如此可以将流中数据刷到文件中去
            if(read!=null){
                read.close();
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常就进行返回
            return new ResponseResult(CommonCode.generateMergeFileError);

        }finally {
          if(writer!=null){
              try {
                  writer.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }
        //加载合并文件为流
        FileInputStream fileInputStream =null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //进行校验文件的md5进行校验
        try {
//            页面传递的MD5:43c54a407a4982aa4c34f56841ab3d5a
//            合并的文件的MD5:7a2f9332df4c8a04e27b8dc7a6b16ffa
            System.err.println("页面传递的MD5:"+fileMd5);
            String md5=DigestUtils.md5Hex(fileInputStream);
            System.err.println("合并的文件的MD5:"+md5);
            String digestAsHex = org.springframework.util.DigestUtils.md5DigestAsHex(fileInputStream);
            System.err.println("Spring提供的MD5算法的实现："+digestAsHex);
            if(!fileMd5.equalsIgnoreCase(md5)){
                //如果文件MD5校验失败则进行删除分块数据
                //删除文件块
                boolean deleteFlag=deleteChunksFile(uploadLocation,fileMd5);
                if(!deleteFlag){
                    ExceptionCastUtils.throwException(new CustomException(CommonCode.deleteChunksFileAndFolder_ERROR));
                }
                ExceptionCastUtils.throwException(new CustomException(CommonCode.UploadFileMD5CheckFail_ERROR));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果成功了就进行保存上传文件信息到数据库
        MediaFile mediaFile=createMediaFileObj( fileMd5,  fileName,  fileSize,  mimetype,  fileExt);
        MediaFile save = mediaFileRepository.save(mediaFile);
        if(save==null){
            return new ResponseResult(CommonCode.saveUploadFileInfoToMediaFileTableError);
        }
        //删除文件块
        boolean deleteFlag=deleteChunksFile(uploadLocation,fileMd5);
        if(!deleteFlag){
            ExceptionCastUtils.throwException(new CustomException(CommonCode.deleteChunksFileAndFolder_ERROR));
        }
        //发送MQ
        //准备数据
        try {
            Map<String,String>map=new HashMap<>();
            map.put("mediaId",save.getFileId());
            String msg = JSON.toJSONString(map);
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.Exchange_MEDIA_PROCESSOR,ROUTING_MEDIA_VIDEO,msg);
            return new ResponseResult(CommonCode.SUCCESS);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    /**
     * 删除chunk文件及其chunks文件夹
     * @param uploadLocation
     * @param fileMd5
     * @return
     */
    private boolean deleteChunksFile(String uploadLocation, String fileMd5) {
        try {
            String chunkFileFolder = getChunkFileFolder(uploadLocation, fileMd5);
            if(StringUtils.isEmpty(chunkFileFolder)){//如果是空的话则进行
                return false;
            }
            //如果不存在的话则进行抛出异常
            File folder = new File(chunkFileFolder);
            if(!folder.exists()){
                ExceptionCastUtils.throwException(new CustomException(CommonCode.deleteChunksFolderNotFound_ERROR));
            }
            //目录存在并且
            if(folder.isDirectory()&&folder.exists()){
                File[] files = folder.listFiles();
                for (File file:files) {
                    if(file.isFile()&&file.exists()){
                        file.delete();
                    }
                }
                //删除文件夹
                folder.delete();
            }
            //返回为true表示删除成功，chunks文件及其chunk目录
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * 依据现有的信息进行创建组装一个文件进行保存
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    private MediaFile createMediaFileObj(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //创建一个母体
        MediaFile  mediaFile = new MediaFile();
        //进行组装数据
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //获取文件的存储相对路径目录
        String relativeFilePath=getfileRelativePath(fileMd5);
        mediaFile.setFilePath(relativeFilePath);
        mediaFile.setFileSize(fileSize);
        mediaFile.setFileStatus("301002");
        mediaFile.setFileType(fileExt);
        mediaFile.setMimeType(mimetype);
        mediaFile.setUploadTime(new Date());
        //文件的绝对路径
//        mediaFile.setFileUrl();
//        mediaFile.setMediaFileProcess_m3u8();
        return  mediaFile;

    }

    /**
     * 过去文件的相对目录
     * @param fileMd5
     * @return
     */
    private String getfileRelativePath(String fileMd5) {
        return new StringBuffer().append("/").append(fileMd5.substring(0,1)).append("/").append(fileMd5.substring(1,2)).append("/").append(fileMd5).append("/").toString();
    }

    /**
     * 获取文件列表----将数组转换为集合列表
     * @param uploadLocation
     * @param fileMd5
     * @return
     */
    private List<File> getchunksList(String uploadLocation, String fileMd5) {
        //调用私有方法进行获取块文件存储的目录
        String chunkFileFolder = getChunkFileFolder(uploadLocation, fileMd5);
        if(StringUtils.isEmpty(chunkFileFolder)){//如果目录为空则进行返回null
            return null;
        }
        //进行获取文件列表
        File[]fileList=null;
        List<File> files =null;
        try {
             fileList= new File(chunkFileFolder).listFiles();
             files = Arrays.asList(fileList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
       return files;
    }
}
