package com.xuecheng.file_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hotwater on 2018/6/23.
 */
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.filesystem")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.framework.exception"})//扫描异常通用
@ComponentScan(basePackages={"com.xuecheng.file_system"})//扫描本项目下的所有类
public class FileSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileSystemApplication.class,args);
    }
}
