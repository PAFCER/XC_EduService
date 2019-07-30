package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.CmsManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * Created by hotwater on 2018/7/6.
 * 此处的控制器专门用于页面预览的功能实现----仅用于远程调用不再对外暴露
 *
 *
 */

@Controller//此处利用流的形式返回不再进行转换JSON，直接利用原生的response回写数据
public class CMSPreviewController extends BaseController {

    @Autowired
    private CmsManageService cmsManageService;

    @GetMapping("/cms/preview/{pageId}")
    public  void  pagePreView(@PathVariable("pageId") String pageId){

        //此处debugger验证出此处具有预览图片的请求路径，需要予以处理否则服务器会报错。20180707
        String requestURI = request.getRequestURI();
        System.err.println("requestURI:"+requestURI);
        StringBuffer requestURL = request.getRequestURL();
        System.err.println(requestURL);

        String htmlAfterStatic = cmsManageService.getHTMLAfterStatic(pageId);

        response.setHeader("Content-type","text/html;charset=utf-8");
        //处理服务器报错异常。处理办法是如果有预览请求是null则直接置为null串返回即可
        if(htmlAfterStatic==null){
            htmlAfterStatic=new String();
        }

        ServletOutputStream outputStream =null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(htmlAfterStatic.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
