package com.jjdata.batch.service;

import com.alibaba.fastjson.JSONObject;
import com.jjdata.batch.config.TaskConfig;
import com.jjdata.batch.config.UserInfoConfig;
import com.jjdata.batch.jthread.FindTagThread;
import com.jjdata.batch.model.UserInfoModel;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author shipeien
 * @version 1.0
 * @Title: YGBatchDataTaskServer
 * @ProjectName run-batch
 * @Description: 获取结果
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1119:48
 */
@SuppressWarnings("ALL")
public class TagRequestService {

    protected static Log logger = LogFactory.getLog(TagRequestService.class);
    @SuppressWarnings("AlibabaThreadPoolCreation")
    private static ExecutorService threadPool = Executors.newFixedThreadPool(TaskConfig.HTTP_READMDNTHREADPOOLSIZE);
    public synchronized static String loginSys(String usercode){
        String reg="00";
        try {
            if(!TaskConfig.isRun){
                throw new Exception(TaskConfig.isRun_message);
            }
            UserInfoModel userInfoModel =findRequestUser(usercode);
            logger.info("判断是不是需要同步秘钥....");
            //判断一下最近同步时间是不是3分钟之内，如果是十分钟之内则不再同步
            String timeStr= TaskConfig.userKeys.get(userInfoModel.getUsercode()+"Time");
            boolean isLogin=true;
            //判断是否需要登录
            if(null!=timeStr){
                Long times= Long.parseLong(timeStr);
                Long nowTime= System.currentTimeMillis();
                isLogin=(nowTime-times)/1000/60> TaskConfig.HTTP_SYNC_TIME;
                if(!isLogin){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                    logger.info("已经同步过上次同步时间："+ format.format(times)+"------"+(nowTime-times)/1000/60);
                }
            }else{
                logger.info("当前秘钥..."+ TaskConfig.userKeys.get(userInfoModel.getUsercode()));
            }
            if(isLogin){
                HttpClient httpClient = new HttpClient();
                String url = TaskConfig.HTTP_LOGINURL;
//
                PostMethod postMethod = new PostMethod(url);
                //设置Post参数
                postMethod.addParameter("userCode", userInfoModel.getUsercode());
                postMethod.addParameter("passwd", userInfoModel.getPassword());
                postMethod.addParameter("encrypt", TaskConfig.HTTP_ENCRYPT);
                HttpMethod method = postMethod;


                method.setRequestHeader("Connection", "close");
                httpClient.executeMethod(method);
                // 设置编码格式
                httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8"));

                String line;
                StringBuffer response = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    //换行
                    response.append(System.getProperty("line.separator"));
                }

                String result = response.toString();
                logger.info("客户："+ userInfoModel.getUsercode()+ "登录，返回值为：" +result);
                JSONObject valueMap = JSONObject.parseObject(result);
                JSONObject data = valueMap.getJSONObject("data");
                String flag = valueMap.getString("flag");
                if("SUCCESS".equals(flag)){
                    String accessKey = data.getString("accessKey");
                    System.out.println("同步成功秘钥:"+accessKey);
                    //获得accessKey
                    TaskConfig.userKeys.put(userInfoModel.getUsercode(), accessKey);
                    //获得accessKey
                    TaskConfig.userKeys.put(userInfoModel.getUsercode()+"Time", System.currentTimeMillis()+"");
                }else{
                    logger.info("同步失败...");
                    reg="ERROR";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("登录异常",e);
            reg="ERROR";
        }
        return reg;
    }

    public static boolean findTelTag(List<String> scanListPath, String saveFilePathName, String month, String usercode, String telcom, String dataType){
        boolean exc=true;
        //获取文件行信息
        try {
            UserInfoModel userInfoModel =findRequestUser(usercode);
            for (int i=0;i<scanListPath.size();i++) {
                if(!TaskConfig.isRun){
                    throw new Exception(TaskConfig.isRun_message);
                }
                String line =scanListPath.get(i);
                if(null!=line){
                    String[] lines=line.split(",");
                    //截取文件
                    String mdn = lines[1];
                    String srcMdn = lines[0];

                    System.out.println("《《《《《《《《《《《条数：" + FindTagThread.mdnCount + ":" + mdn);
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    if (!TaskService.checkRun()){
                        throw new Exception("不可以运行了..");
                    }
                    while(FindTagThread.mdnCount>=TaskConfig.HTTP_READMDNTHREADPOOLSIZE){
                        logger.info("并发线程满当前待执行线程满暂停1秒--->"+FindTagThread.mdnCount);
                        Thread.sleep(1000);
                    }
                    System.out.println("《《《《《《《《《《《《《《调接口线程丢进线程池！");
                    FindTagThread.addRemoveMdnCount(1);
                    //在线程池中开启线程任务
                    threadPool.submit(new FindTagThread( mdn, srcMdn,month,saveFilePathName,userInfoModel.getUsercode(),telcom,dataType));


                    //等待结束后，从队列中读数据写入文件
                    logger.info("当前待执行线程数--->"+ FindTagThread.mdnCount);
                }

            }
            while(FindTagThread.mdnCount>1){
                logger.info("《《《《《《《《《《《《《《等待进程调用结束-------------->"+ FindTagThread.mdnCount);
                Thread.sleep(1*1000);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("处理异常",e);
            exc=false;
        }
        return exc;
    }


    public static UserInfoModel findRequestUser(String usercode){
        UserInfoModel userInfoModel=new UserInfoModel();
        try {
            String password= UserInfoConfig.MAP_USER_INFO.get(usercode);
            if(null!=password&&!"".equals(password)){
                userInfoModel.setUsercode(usercode);
                userInfoModel.setPassword(password);
            }else{
                //使用默认用户名密码
                userInfoModel.setUsercode(TaskConfig.HTTP_DEFAULT_USERCODE);
                userInfoModel.setPassword( TaskConfig.HTTP_DEFAULT_PASSWORD);
            }
        }catch (Exception e){
            logger.error("处理异常",e);
        }
        return  userInfoModel;
    }



}
