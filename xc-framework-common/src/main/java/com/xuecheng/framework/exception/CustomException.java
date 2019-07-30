package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * Created by hotwater on 2018/6/28.
 * 自定义异常
 * 关于异常处理的思路：
 * 我们利用Spring的增强控制器进行统一捕获异常
 *          首先我们需要明确一点：
 *              异常处理的分类：
 *                  1.自定义异常，可以预知的异常
 *                  2.不可预知的异常
 *                  这类异常我们通常是利用Google的异常处理集合InmulTableMap
 *                  进行处理异常
 *                  3.我们需要定义一个异常捕获类，利用注解进行配置处理，捕获异常之后进行抛出即可
 *                  4.进行统一管理有利于维护
 */
public class CustomException  extends RuntimeException{

    private ResultCode  resultCode;

    //利用构造函数迫使必须传递该参数
    public  CustomException(ResultCode  resultCode){
        this.resultCode=resultCode;
    }
    //提供获取该异常响应码的方法
    public  ResultCode  getResultCode(){
        return resultCode;
    }

}
