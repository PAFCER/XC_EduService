package com.xuecheng.manage_media_processor.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_processor.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hotwater on 2018/7/13.
 * 此处为消费者：处理视频转码存储的操作
 * avi转mp4
 * mp4转u3m8
 * 并将处理后的文件信息进行更新到mediaFile数据库表中，
 * 此处主要处理对应的抽取根据消费端传递过来的数据库表中的文件id
 * 然后查询数据库，解析内部的路径进行转码，
 * 转码分为两部分：
 *      第一步是将avi转换成mp4，第二步是将mp4转换为u3m8格式ts文件，
 *      首先需要判定数据库信息表中的数据文件格式是否正常，如果是avi进行第一步编码转换为mp4（h264编码）
 *      转换的时候会涉及到一系列的路径，比如文件存储的根路径，此处的根路径我们是需要在配置文件中进行配置的
 *      然后通过注解注入进来的，然后根据根路径及其我们的文件信息表的数据进行拼接路径，加载文件进行转换，
 *      然后我们的工具类涉及到几个参数比如ffmpeg路径，avi文件路径，生成后的文件名称，及其生成后的文件目录等等
 *      我们都利用现有的数据进行处理拼接，最后实现对于数据格式转换并实时更新数据库表
 *      进行完成第一步的转换H264之后，进行依据上述的数据知识进行转换m3u8格式，并适时的进行更新数据库，
 *      此处有一个疑问就是什么时候我们进行第二部转换，在案例中为社么遇到不是avi就不转换，直接返回，如果是mp4那么就转换为
 *      m3u8不就可以了，为什么要返回呢？不理解，此处的逻辑可能是错误的，因此对于此处的文件我们如何处理呢，我们暂时给予
 *      按照正常流程给予处理，但是略过转换mp4格式的过程，注意实时更新数据库信息，此时需要注意的，下面进行代码实现。
 */
@Component
public class MediaVideoConsumer {
    private Logger logger= LoggerFactory.getLogger(MediaVideoConsumer.class);

    @Autowired
    MediaFileRepository  mediaFileRepository;
    //上传路径的注入
    @Value("${xc-service-manage-media.upload-location}")
    private String  uploadPath;
    //ffmpeg指令绝对路径
    @Value("${xc-service-manage-media.ffmpeg_path}")
    private String ffmpeg_path;
//配置containerFactory="customContainerFactory"解决指定的线程并发数量
    @RabbitListener(queues={"${xc-service-manage-media.mq.queue-media-video-processor}"},containerFactory="customContainerFactory")
    private void  transferVideo(String  message){
            //1.消息为空返回
            if(message==null){
                logger.error("消息队列出现空数据，{}",message);
               return ;
            }
            //2.消息不为空
            Map map = JSON.parseObject(message, Map.class);
            String mediaId =(String ) map.get("mediaId");//转换为String类型的
            MediaFile mediaFile = mediaFileRepository.findOne(mediaId);
            if(mediaFile==null){
                logger.error("消息队列出现数据库不匹配记录，mediaId:{}",mediaId);
                return;//查不到则直接返回即可
            }
            //media file不为空
            //进行转换前的准备工作
            //校验avi格式
            String fileType = mediaFile.getFileType();
            //avi格式的校验
            if(StringUtils.isEmpty(fileType)||!fileType.equalsIgnoreCase("avi")){
                //返回之前进行更新数据库
                //处理状态
                mediaFile.setProcessStatus("303004");
                mediaFileRepository.save(mediaFile);
                logger.error("检测到文件无需进行转换，非avi格式，mediaId:{}",mediaId);
                return ;
            }
            //开始处理前进行更新数据库信息状态
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
            logger.info("检测到文件开始进行文件转换，mediaId:{}",mediaId);
            //通过检验需要进行转换，avi---->mp4
          boolean flag_1 = transferAVIToMP4(mediaFile);
            if(!flag_1){
                mediaFile.setProcessStatus("303003");//转换失败
                mediaFileRepository.save(mediaFile);
                logger.error("检测到文件转换AVI转换MP4失败，mediaId:{}",mediaId);
                return ;
            }

           //如果成功转换为MP4 进行开始转换 为m3u8
            Map<String, Object> mp4ToM3U8 = transferMP4ToM3U8(mediaFile);
            //返回为空或者状态为false
            if(mp4ToM3U8==null||!(Boolean) mp4ToM3U8.get("flag")){
                mediaFile.setProcessStatus("303003");//转换失败
                mediaFileRepository.save(mediaFile);
                logger.error("检测到文件转换MP4转换M3U8失败，mediaId:{}",mediaId);
                return ;
            }
            //转换成功之后进行处理数据库信息
            mediaFile.setProcessStatus("303002");
            String fileUrl=mediaFile.getFilePath()+"hls"+"/"+mediaFile.getFileId()+".m3u8";
            mediaFile.setFileUrl(fileUrl);
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            List<String> ts_list = (List<String>) mp4ToM3U8.get("ts_list");
            mediaFileProcess_m3u8.setTslist(ts_list);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            //到了此处算是全部完成，视频转换器工具类有bug
        }

    /**
     * 将mp4文件转换为m3u8
     * @param mediaFile
     * @return
     */
    private Map<String,Object> transferMP4ToM3U8(MediaFile mediaFile) {
        Map<String ,Object>hashmap=new HashMap<>();
        //校验参数
        if(mediaFile==null){
            logger.error("将视频文件MP4转换为M3U8格式失败,媒资文件参数为null，文件：{}",mediaFile);
            hashmap.put("flag",false);
            return hashmap;
        }
        String video_path=uploadPath+mediaFile.getFilePath()+mediaFile.getFileId()+".mp4";
        String m3u8_name=mediaFile.getFileId()+".m3u8";
        String m3u8folder_path=uploadPath+mediaFile.getFilePath()+"hsl"+"//";

        //进行文件的检测
          boolean flag=this.EnsureSourceFileExist(video_path);
        if(!flag){
            logger.error("将视频文件MP4转换为M3U8格式失败,源文件不存在，文件：{}",mediaFile);
            hashmap.put("flag",false);
            return hashmap;
        }

        HlsVideoUtil  hlsVideoUtil = new HlsVideoUtil(ffmpeg_path,video_path,m3u8_name,m3u8folder_path);
        String generateM3u8 = hlsVideoUtil.generateM3u8();
        if(!"success".equalsIgnoreCase(generateM3u8)){
            logger.error("将视频文件MP4转换为M3U8格式失败,异常信息：{}",generateM3u8);
            hashmap.put("flag",false);
            return hashmap;
        }
        //设置状态不要忘记了//
        hashmap.put("flag",true);
        //转换成功之后进行换算抽取数据---获取列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        hashmap.put("ts_list",ts_list);
        return hashmap;

    }

    /***
     * 检测文件是否存在--不存在则不进行转换，否则会空指针异常的
     * @param video_path
     * @return
     */
    private boolean EnsureSourceFileExist(String video_path) {
        return new File(video_path).exists();
    }

    /**
     * 进行AVI转换为MP4的功能实现
     * @param mediaFile
     * @return
     */
    private boolean transferAVIToMP4(MediaFile mediaFile) {
        if(mediaFile==null){
         logger.error("将视频文件avi转换为MP4格式失败,媒资文件参数为null，文件：{}",mediaFile);
            return false;
        }

        //需要四个参数，通过mediaFile进行处理就行
        String video_path=uploadPath+mediaFile.getFilePath()+mediaFile.getFileName();
        String mp4_name=mediaFile.getFileId()+".mp4";
        String mp4Folder_path=uploadPath+mediaFile.getFilePath();
        //进行文件的检测
        boolean flag=this.EnsureSourceFileExist(video_path);
        if(!flag){
            logger.error("将视频文件AVI转换为MP4格式失败,源文件不存在，文件：{}",mediaFile);
            return false;
        }
        Mp4VideoUtil mp4VideoUtil= new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4Folder_path);
        String generateMp4 = mp4VideoUtil.generateMp4();
        if(!"success".equalsIgnoreCase(generateMp4)){
            logger.error("将视频文件avi转换为MP4格式失败，文件名：{}",mediaFile.getFileName());
            return false;
        }
        return true;
    }


}
