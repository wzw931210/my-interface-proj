package com.jjdata.batch.jthread;

import cn.jinjing.plat.util.StatusCode;
import com.alibaba.fastjson.JSONObject;
import com.jjdata.batch.config.PageTagConfig;
import com.jjdata.batch.config.TaskConfig;
import com.jjdata.batch.model.ReturnVal;
import com.jjdata.batch.model.TagTypeModel;
import com.jjdata.batch.service.TagRequestService;
import com.jjdata.batch.util.FileUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

/**
 * @author shipeien
 * @version 1.0
 * @Title: FindTagThread
 * @ProjectName run-batch
 * @Description: 调用标签获取请求结果线程类
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1713:50
 */
public class FindTagThread extends Thread {
    private String mdn;
    private String srcMdn;
    private String month;
    private String saveFilePathName;
    private String usercode;
    private String teleCom;
    private String dataType;

    public static int mdnCount = 1;


    public FindTagThread(String mdn, String srcMdn, String month, String saveFilePathName, String usercode, String teleCom, String dataType) {
        this.mdn = mdn;
        this.srcMdn = srcMdn;
        this.month = month;
        this.saveFilePathName = saveFilePathName;
        this.usercode = usercode;
        this.teleCom = teleCom;
        this.dataType = dataType;
    }

    public static synchronized void addRemoveMdnCount(int num){
        mdnCount=mdnCount+num;
    }

    protected static Log logger = LogFactory.getLog(FindTagThread.class);
    @Override
    public void run(){
        try {
            if(!TaskConfig.isRun){
                throw new Exception(TaskConfig.isRun_message);
            }
            String line_reg="";
            String line_reg_pub="";
            String line_reg_lab="";
            line_reg_pub=mdn+TaskConfig.Separator+srcMdn;
            String upperDataType=dataType.toUpperCase();
            logger.info("文件调用接口类型...."+upperDataType);
            List<TagTypeModel> tagTypeModelList = PageTagConfig.TAG_TYPE_MODEL_MAP.get(upperDataType);
            String isFlag="0";

            //开始获取标签结果
            for(TagTypeModel tagTypeModel:tagTypeModelList){
                logger.info(Thread.currentThread().getName()+">>>>>>>>>>>>开始调接口！");
                if(null!=tagTypeModel.getTagName()&&!"".equals(tagTypeModel.getTagName())){
                    ReturnVal returnVal= findTagHttp(tagTypeModel.getTagName(),usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,tagTypeModel.getTagChName(),1);
                    if(returnVal.isReflag()){
                        isFlag="1";
                    }
                    line_reg_lab=line_reg_lab+TaskConfig.Separator+returnVal.getReg();
                }
                Thread.sleep(500);
            }
            //组装结果
            line_reg=line_reg_pub+TaskConfig.Separator+isFlag+line_reg_lab;
            logger.info(line_reg);

            //根据保存类型写入文件
            String file_name="";
            if("1".equals(TaskConfig.save_file_type)){
                file_name=TaskConfig.save_data_path+saveFilePathName+srcMdn.substring(0,4);
            }else if("2".equals(TaskConfig.save_file_type)){
                file_name=TaskConfig.save_data_path+saveFilePathName;
            }else{
                file_name=TaskConfig.save_data_path+saveFilePathName;
            }

            FileUtils.writeFile(line_reg, file_name);


            //--计费--
            if("1".equals(isFlag)&&TaskConfig.price_check){
                TaskConfig.reducePublicPrice();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            FindTagThread.addRemoveMdnCount(-1);
        }
    }
    public ReturnVal findTagHttp(String label, String userName, String teleCom, String encrypt, String mdn, String month, String labelChName, int excNum) {
        ReturnVal returnVal=new ReturnVal();
        returnVal.setReflag(false);
//        String reg="";
        HttpClient httpClient = new HttpClient();
        HttpMethod method = null;
        try {

            String url = TaskConfig.HTTP_REQUESTURL + label;
            PostMethod postMethod = new PostMethod(url);
            //设置Post参数
            postMethod.addParameter("userCode", userName);
            postMethod.addParameter("accessKey", TaskConfig.userKeys.get(userName));
            postMethod.addParameter("month", month);
            postMethod.addParameter("mdn", mdn);
            postMethod.addParameter("teleCom", teleCom);
            postMethod.addParameter("encrypt", encrypt);

            postMethod.setRequestHeader("Connection", "close");
            // 设置编码格式
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            httpClient.executeMethod(postMethod);
            BufferedReader reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), "UTF-8"));

            String line;
            StringBuffer response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append(System.getProperty("line.separator"));//换行
            }

            String result = response.toString();
            System.out.println("结果：result："+result);
            if("SUCCESS".equals(JSONObject.parseObject(result).getString("flag"))) {
                if(JSONObject.parseObject(result).containsKey("data") && ! "".equals(JSONObject.parseObject(result).getString("data"))){
                    String data = JSONObject.parseObject(result).getString("data");
                    System.out.println("----------------------调用"+label+"-->"+labelChName+"结果："+data);
                    //TODO 写入queue
//                    reg=label+":"+data;
//                    reg=data;
                    returnVal.setReflag(true);
                    returnVal.setReg(data);
                    returnVal.setCode("200");
                }else{
                    String code = JSONObject.parseObject(result).getString("code");
                    returnVal.setCode(code);
                    String message = JSONObject.parseObject(result).getString("message");
                    //如果是超时秘钥发生变化重新登录获取下密钥
                    if("08".equals(code)){
                        //同步秘钥
                        TagRequestService.loginSys(userName);
                        //如果调用了三次还没有结果则返回结果
                        if(excNum>3){
//                            reg=label+":"+code+"-"+message;
//                            reg=code+"-"+message;
                            returnVal.setReg(code+"-"+message);
                        }else{
                            //暂停1秒递归调用
                            Thread.sleep(1000*60);
                            returnVal= findTagHttp(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1);
                        }
                    }else if(StatusCode.SYSERR2_1.getCode().equals(code)){//可能是内部线程超速
                        logger.info("---------------------------------超速关闭全局开关----------------------------------------");
                        //全局开关关掉
                        TaskConfig.isRunFalse();

                        returnVal.setReg(code+"-"+message);
                    }else{
//                        reg=label+":"+code+"-"+message;
//                        reg=code+"-"+message;
                        returnVal.setReg(code+"-"+message);
                    }
                }
            }else {
                //TODO 写入queue
                String code = JSONObject.parseObject(result).getString("code");
                returnVal.setCode(code);
                String message = JSONObject.parseObject(result).getString("message");
                System.out.println("----------------------调用"+label+"-->"+labelChName+"错误！");
                if("08".equals(code)){
                    //同步秘钥
                    TagRequestService.loginSys(userName);
                    //如果调用了三次还没有结果则返回结果
                    if(excNum>3){
//                            reg=label+":"+code+"-"+message;
//                        reg=code+"-"+message;
                        returnVal.setReg(code+"-"+message);
                    }else{
                        //暂停1秒递归调用
                        Thread.sleep(1000*60);
                        returnVal= findTagHttp(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1);
                    }
                }else{
                    //联通错误码判断
                    if("1".equals(code)){
                        if(StatusCode.SYSERR4_3.getCode().equals(code)||StatusCode.SYSERR4_0.equals(code)||StatusCode.SYSERR4.equals(code)){
//                            if(excNum>3){
////                            reg=label+":"+code+"-"+message;
////                            reg=code+"-"+message;
//                                returnVal.setReg(code);
//                            }else{
//                                //暂停1秒递归调用
//                                returnVal= findTagHttp(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1);
//                            }
                            returnVal= findTagHttpNum(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1,code,message);
                        }else{
//                        reg=message;
                            returnVal.setReg(code+"-"+message);
                        }
                    }else{
                        if(StatusCode.SYSERR1.getCode().equals(code)){
                            returnVal= findTagHttpNum(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1,code,message);
                        }
                        //可能是内部线程超速
                        else if(StatusCode.SYSERR2_1.getCode().equals(code)){
                            logger.info("---------------------------------超速关闭全局开关----------------------------------------");
                            //全局开关关掉
                            TaskConfig.isRunFalse();

                            returnVal.setReg(code+"-"+message);
                        }else{
//                        reg=message;
                            returnVal.setReg(code+"-"+message);
                        }
                    }


                }
//                reg="error:"+code+message;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(method!=null){
                method.releaseConnection();
            }
        }
        System.out.println(new Date());

        return returnVal;

    }

    public  ReturnVal findTagHttpNum(String label, String userName, String teleCom, String encrypt, String mdn, String month, String labelChName, int excNum,String code,String message){
        ReturnVal returnVal=new ReturnVal();
        returnVal.setReflag(false);
      try {

          if(excNum>3){
//                            reg=label+":"+code+"-"+message;
//                            reg=code+"-"+message;
              returnVal.setReg(code+"-"+message);
          }else{
              //暂停1秒递归调用
              Thread.sleep(1000);
              returnVal= findTagHttp(label,usercode,teleCom, TaskConfig.HTTP_ENCRYPT,mdn,month,labelChName,excNum+1);
          }
      }catch (Exception e){
          e.printStackTrace();
          logger.error("处理值失败",e);
      }
        return returnVal;

    }

}