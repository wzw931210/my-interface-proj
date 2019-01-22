package com.jjdata.batch.task;

import com.jjdata.batch.config.FtpConfig;
import com.jjdata.batch.enumj.MessageEnum;
import com.jjdata.batch.model.FtpDataModel;
import com.jjdata.batch.model.MessageModel;
import com.jjdata.batch.service.FtpServer;
import com.jjdata.batch.util.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author shipeien
 * @version 1.0
 * @Title: FtpTask
 * @ProjectName interface-plat
 * @Description: 定时下载文件
 * @email shipeien@jinjingdata.com
 * @date 2019/1/1715:27
 */
public class FtpTask  extends Thread {
    protected static Log logger = LogFactory.getLog(FtpTask.class);
    private static  Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    @Override
    public void run() {
        while (true){
            logger.info("任务处理");
            //几点后可以执行
            SimpleDateFormat format = new SimpleDateFormat("HH");
            SimpleDateFormat format_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
            int timeInfo=Integer.parseInt(format.format(System.currentTimeMillis()));
            try {
                //还没有到时间，休息3分钟
                if(timeInfo<FtpConfig.BEGIN_RUN_TIME){
                    logger.info("还没有到时间，休息10分钟....."+FtpConfig.BEGIN_RUN_TIME+"开始");
                    Thread.sleep(1000*60*10);
                    continue;
                }
                //判断有没有数据需要处理
                FtpDataModel ftpDataModelPass=new FtpDataModel(FtpConfig.SFTP_PASS_HOST,FtpConfig.SFTP_PASS_PORT
                        ,FtpConfig.SFTP_PASS_USERNAME,FtpConfig.SFTP_PASS_PASSWORD
                        ,FtpConfig.SFTP_DATA_DOWN_PATH,FtpConfig.LOCAL_DATA_BACK,"","");
                List<String> fileList= FtpServer.findFileList(ftpDataModelPass);
                //判断有没有要处理的文件
                if(null!=fileList&&fileList.size()>0){
                    for(String fileName:fileList){
                        //--查看本地是否有这个文件，如果有则不需要下载
                        File[] localFile=FileUtils.findPathFileNoDir(FtpConfig.LOCAL_DATA_BACK,"");
                        boolean down_is=true;
                        for(File file:localFile){
                            if(fileName.equals(file.getName())){
                                down_is=false;
                                logger.info(fileName+"文件已经存在不需要下载...");
                                break;
                            }
                        }
                        if(!down_is){
                            continue;
                        }
                        //文件不是今天的都下载
                        if(down_rule(fileName)){
                            //重置下载路径
                            ftpDataModelPass.setSftpPath(FtpConfig.SFTP_DATA_DOWN_PATH);
                            logger.info("下载服务器上文件--------------------------"+fileName);
                            //有则下载数据
                            ftpDataModelPass.setFileName(fileName);
                            MessageModel messageModel=FtpServer.downLoadFile(ftpDataModelPass);
                            logger.info("下载结束--------------------------"+fileName);
                            if(MessageEnum.SUCCESS.getMessageCode().equals(messageModel.getMessageCode())){
                                logger.error(messageModel.toString());
                                logger.error("下载pass服务器文件成功..."+fileName+messageModel.toString());
                            }else{
                                logger.error(messageModel.toString());
                                logger.error("下载pass服务器文件失败..."+fileName+messageModel.toString());
                            }
                        }
                    }
                }else{
                    logger.info("还没有要处理的文件...");
                }
                //半小时轮询一次
                Thread.sleep(1000*60);
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


    public boolean down_rule(String fileName){
        boolean down_is=false;
        SimpleDateFormat format_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        if(fileName.length()>8&&isInteger(fileName.substring(0,8))&&Integer.parseInt(fileName.substring(0,8))<Integer.parseInt(format_yyyyMMdd.format(System.currentTimeMillis()))){
            down_is=true;
        }else if(fileName.endsWith(".OK")){
            down_is=true;
        }else{
            logger.info(fileName+"不符合下载规则不能下载...");
        }
        return down_is;
    }

    public static boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

}
