package com.xuecheng.manage_media_processor;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;

/**
 * Created by hotwater on 2018/7/13.
 */

public class HSLUtilsTest {


    /**
     * 测试视频转换工具
     * avi转MP4
     */
    @Test
    public void  test(){
//参数
// String ffmpeg_path,
// String video_path,
// String mp4_name,
// String mp4folder_path
        String ffmpeg_path="E:\\hotwater\\StudySuccessOnline_Home\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        String video_path="E:\\hotwater\\StudySuccessOnline_Home\\dealVideo\\mp4\\hc - 因为是女子 中文字幕版.mp4";
        String mp4_name="test.mp4";
        String mp4folder_path="E:\\hotwater\\StudySuccessOnline_Home\\dealVideo\\mp4\\";
        Mp4VideoUtil videoUtil  = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String result = videoUtil.generateMp4();
        System.err.println("result");



    }



}
