package com.xuecheng.framework.exception;

/**
 * Created by hotwater on 2018/6/28.
 * 此处为了进行方便抛出异常，定义一个工具类方便进行异常抛出
 * 只能抛出运行时异常
 */
public class ExceptionCastUtils {

    //工具类抛出异常
    public  static void   throwException(RuntimeException  e){
            throw  e;
    }

}
