package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by hotwater on 2018/6/28.
 * 异常控制器-----进行异常的统一管理捕获处理
 */
@ControllerAdvice
public class ExceptionController {

    //提前准备好不可预知的异常集合
  private     static ImmutableMap< Class<? extends  Throwable>,ResultCode>Exceptions;
    //处理自定义异常
    private  static ImmutableMap.Builder<Class<? extends  Throwable>,ResultCode>builder=ImmutableMap.builder();

    static {
        builder.put(org.springframework.core.convert.ConversionException.class, CommonCode.IllegalArgument_BindArgs);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody//此处的注解如果丢失就会出现不执行捕获的行为，切记切记
    public ResponseResult  handlerCustomerException(CustomException  e){
//        e.printStackTrace();
        return  new ResponseResult(e.getResultCode());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public   ResponseResult  handlerException(Exception  e){

//        e.printStackTrace();
        //1.首先验证是否存在该异常集合对象
        if(Exceptions==null){
            Exceptions=builder.build();
        }
        //2.根据捕获的异常的权限定类名进行进行查询集合
            ResultCode resultCode = Exceptions.get(e.getClass());
        //3.如果查询出来对应的则直接抛出
        if(resultCode!=null){
            return new ResponseResult(resultCode);
        }
        //4.如果不存在则直接抛出指定的系统忙
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

}
