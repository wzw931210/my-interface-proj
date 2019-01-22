package com.jjdata.batch.task;

import com.jjdata.batch.config.TaskConfig;
import com.jjdata.batch.model.UserInfoModel;
import com.jjdata.batch.service.TagRequestService;
import com.jjdata.batch.service.TaskService;
import com.jjdata.batch.util.FileUtils;
import com.jjdata.batch.util.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: YGBatchDataTask
 * @ProjectName run-batch
 * @Description: 自动生成号码处理没有隔日次数限制
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1111:27
 */
public class BatchDataTask extends Thread {
    protected static Log logger = LogFactory.getLog(BatchDataTask.class);

    @Override
    public void run() {
        TaskService taskService =new TaskService();
        try {
            logger.info("任务处理");
            while (true){
                List<String> mdnList =null;
                //判断时间和额度是否到了
                if (!TaskService.checkRun()){
                    throw new Exception("不可以运行了..");
                }
                //隔日运行校验
                if(!TaskService.nextDayCheck()){
                    continue;
                }
                //segment/mdn/number
                //号段类型
                if("segment".equals(TaskConfig.telephone_number_type)){
                    mdnList = taskService.genMobile(mdnList);
                }
                //MDN类型
                else if("mdn".equals(TaskConfig.telephone_number_type)){
                    mdnList = taskService.selMobileMd5();
                }
                //明文号码类型
                else if("number".equals(TaskConfig.telephone_number_type)){
                    mdnList = taskService.selMobileNoMd5();
                }
                //判断是否需要继续执行
                if(null!=mdnList&&mdnList.size()>0){
                    taskService.excTag(mdnList);
                }else{
                    logger.info("没有号码可以处理了....");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

        }
    }

}
