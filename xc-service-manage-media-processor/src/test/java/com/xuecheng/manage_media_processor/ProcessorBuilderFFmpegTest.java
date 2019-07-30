package com.xuecheng.manage_media_processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hotwater on 2018/7/13.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProcessorBuilderFFmpegTest {

    @Test
    public  void  testProcessBuilder(){
        ProcessBuilder  processBuilder = new ProcessBuilder();
        processBuilder.command("ping ", "127.0.0.1");
        processBuilder.redirectErrorStream(true);//将正确和错误的流进行合并
        InputStream inputStream =null;
        InputStreamReader  inputStreamReader  =null;
        try {
            Process start = processBuilder.start();
            inputStream = start.getInputStream();
            //转换为转换字符流
            inputStreamReader  = new InputStreamReader(inputStream,"GBK");
            char[]buffer=new char[1024];
            int length=-1;
            StringBuffer  stringBuffer=new StringBuffer();
            while((length=inputStreamReader.read(buffer))!=-1){
                System.err.print(new java.lang.String(buffer,0,length));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStreamReader!=null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
