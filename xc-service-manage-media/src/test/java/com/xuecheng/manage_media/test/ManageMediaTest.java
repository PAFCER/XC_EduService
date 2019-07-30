package com.xuecheng.manage_media.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hotwater on 2018/7/11.
 *
 *      此处的代码不是很熟悉，而且出现了一些问题，因此需要后续加强练习一次
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ManageMediaTest {

    //此处为测试的文件
    private  final String testFile="E:\\hotwater\\StudySuccessOnline_Home\\dealVideo\\test.mp4";
    //此处为chunks目录
    private  final String chunksPath="E:\\hotwater\\StudySuccessOnline_Home\\dealVideo\\chunks\\";
    //此处为合并后文件
    private  final String mergeFile="E:\\hotwater\\StudySuccessOnline_Home\\dealVideo\\mergeFile.mp4";

    /**
     * 测试拆分文件为小块
     */
    @Test
    public  void  testSpilt(){

        //1.定义一个文件块的大小
        long chunkSize=1*1024*1024;//1MB
        //2.加载文件
        File  file = new File(testFile);
        if(!file.exists()){
            System.err.println("测试文件不存在");
        }
        //3.依据文件大小进行换算拆分的chunks数目---此处转化切记不可使用Long.parseLong()，因为涉及到转换后的精度仍然具有小数点，此处利用强转让其丢失进度即可
        long chunksNum =(long)(Math.ceil((file.length()*1.0/chunkSize)));
        //保证不丢失数据
        if(chunksNum<=0){
            chunksNum=1;
        }
        //4.利用文件进行大小数据进行遍历文件进行拆分
        RandomAccessFile  randomAccessFile_read=null;
        try {
//            RandomAccessFile  randomAccessFile_read=null;
            //读取文件
            randomAccessFile_read= new RandomAccessFile(file,"r");
            for (int x=0;x<chunksNum;x++) {
                //每循环一次就是一个chunks文件，利用文件名称拼接的方式进行处理
                File chunkfile = new File(chunksPath + x);
                //定义一个输出流
    //            OutputStream  outputStream=null;
                RandomAccessFile randomAccessFile_write = null;
                //定义一个缓冲数据加快复制文件速度
                byte[] bytes = new byte[1024];//1KB
                int length = -1;
                //将输出流架到chunks文件上
                randomAccessFile_write = new RandomAccessFile(chunkfile, "rw");
                //读取数据
                while ((length = randomAccessFile_read.read(bytes)) != -1) {
                    //写入数据,读取缓冲数组中的数据
                    randomAccessFile_write.write(bytes, 0, length);
                    //检测输出的chunks文件大小，达到要求就进行终止此次循环
                    if (chunkfile.length() >= chunkSize) {
                        break;
                    }
                }
                //此处的写操作的流需要提前关闭，因为每一个都是新的对象
                randomAccessFile_write.close();//释放对象资源

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                randomAccessFile_read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * 测试合并文件
     */
    @Test
    public  void  testMergeFile(){

        //1.加载到chunks文件目录，遍历文件将其中的文件进行以流的形式进行合并
        File file = new File(chunksPath);
        if(!file.exists()){
            System.err.println("chunks文件目录不存在");
        }
        //2.拿到chunks文件列表
        File[] files = file.listFiles();
        //3.利用集合工具类进行遍历
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                    if(
                        Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                        return 1;
                       }
                       else {
                            return -1;
                    }
            }
        });
        //4.排序后的文件列表就在集合中，然后进行遍历将文件进行合并
        //定义一个文件流进行导向文件
        RandomAccessFile  randomAccessFile_read = null;
        RandomAccessFile  randomAccessFile_write  = null;
        try {
//            if(!new File(mergeFile).exists()){
//                boolean newFile = new File(mergeFile).createNewFile();
//                if(!newFile){
//                    return ;
//                }
//            }

            randomAccessFile_write  = new RandomAccessFile(new File(mergeFile),"rw");
            for(File chunk :fileList){
    //            InputStream  inputStream =null;
                    //遍历每一个文件，将每一个文件加载到内存中进行利用random access file进行书写到mergefile
    //                  inputStream = new FileInputStream(chunk);
            randomAccessFile_read=new RandomAccessFile(chunk,"r");
                    int length=-1;
                    byte[]bytes=new byte[1024];
                    while((length=randomAccessFile_read.read(bytes))!=-1){
                        //将每一个文件进行写入合并文件中
                        randomAccessFile_write.write(bytes,0,length);
                    }
                    //一次循环后就需要将读的流进行释放
                randomAccessFile_read.close();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                randomAccessFile_write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    }




