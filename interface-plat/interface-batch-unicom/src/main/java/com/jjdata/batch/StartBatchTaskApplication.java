package com.jjdata.batch;


import com.jjdata.batch.config.PageTagConfig;
import com.jjdata.batch.config.UserInfoConfig;
import com.jjdata.batch.task.BatchDataTask;
import com.jjdata.batch.util.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author shipeien
 * @version 1.0
 * @Title: StartBatchApplication
 * @ProjectName run-batch
 * @Description: TODO
 * @email shipeien@jinjingdata.com
 * @date 2018/12/615:03
 */

public class StartBatchTaskApplication {

    protected static Log logger = LogFactory.getLog(StartBatchTaskApplication.class);
    public static void main(String[] args) {
        try {

            ConfigUtils.loadClasspathConfig("get-tag.properties");
            //初始化标签信息
            PageTagConfig.initTagInfo();
            //初始化用户信息
            UserInfoConfig.initUserInfo();

            new BatchDataTask().run();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 为保证服务一直开着，利用输入流的阻塞来模拟
        synchronized (StartBatchTaskApplication.class) {
            while (true) {
                try {
                    StartBatchTaskApplication.class.wait();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
