package com.xuecheng.test.fastdfs;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hotwater on 2018/7/4.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    @Test
    public  void  testUpload(){
        try {
            //1.通过加载配置信息进行初始化组件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("g_network_timeout:"+ClientGlobal.g_network_timeout+"");
            //2.创建trackerClient客户端---配置信息已经由上述第一步进行加载
            TrackerClient  trackerClient=new TrackerClient();

            //3.利用trackerClient进行获取trackerSerer
            TrackerServer trackerServer = trackerClient.getConnection();
            if(trackerServer==null){
                System.err.println("trackerServer:为null");
            }
            //4.利用trackerServer进行
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //5.创建存储客户端----需要传递两个参数trackerServer和storageServer
            //构造函数参数：TrackerServer trackerServer, StorageServer storageServer
            StorageClient1 storageClient1=new StorageClient1(trackerServer,storageServer);
//          String group_name, byte[] file_buff, String file_ext_name, NameValuePair[] meta_list
            //指定文件
            FileInputStream fileInputStream = new FileInputStream(new File("E:/upload/gupao.jpg"));
            byte[] bytes = IOUtils.toByteArray(fileInputStream);
            String fileId = storageClient1.upload_file1(bytes, "jpg", null);
            System.err.println("获取上传后的文件id："+fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void  testDelete(){


        try {
            ClientGlobal.initByProperties("config/fastdfs-client.properties");

            //1.创建trackerClient
            TrackerClient trackerClient=new TrackerClient();
            //2.利用trackerClient获取trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            if(trackerServer==null){
                System.err.println("trackerServer:为空");
                return ;
            }
            //3.利用trackerClient和trackerServer创建StorageServer
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //4.创建storageClient1-----需要指定参数
            StorageClient1  storageClient1=new StorageClient1(trackerServer,storeStorage);
            //5.利用上述存储客户端进行操作
//            NameValuePair[] metadata1 = storageClient1.get_metadata1("group1/M00/00/01/wKhlQVs9EHeAXB7xAAG2T-YMRus086.jpg");
            byte[] bytes = storageClient1.download_file1("group1/M00/00/01/wKhlQVs9EHeAXB7xAAG2T-YMRus086.jpg");
            IOUtils.write(bytes,new FileOutputStream("E:/upload/downFile.jpg"));
            System.err.println("下载文件完毕：");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }



}
