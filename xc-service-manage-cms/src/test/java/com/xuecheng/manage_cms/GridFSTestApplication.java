package com.xuecheng.manage_cms;

import com.mongodb.gridfs.GridFSFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by hotwater on 2018/6/28.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTestApplication {
    @Autowired
    private  GridFsTemplate  gridFsTemplate;
    @Test
    public void  test(){
        System.err.println("是否为空："+gridFsTemplate);
        try {
            FileInputStream fis = new FileInputStream(new File("D:\\Desktop\\index_banner.ftl"));
            System.out.println("是否是空null:"+gridFsTemplate);
            GridFSFile bannerTest2018 = gridFsTemplate.store(fis, "bannerTest2018");
            System.err.println("测试新程序tenplateFileId："+bannerTest2018.getId().toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
