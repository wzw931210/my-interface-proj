package com.jjdata.batch.config;

import com.jjdata.batch.util.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shipeien
 * @version 1.0
 * @Title: TaskConfig
 * @ProjectName run-batch
 * @Description: ftp配置参数
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1115:15
 */
public class FtpConfig {

    //每天几点后开始执行
    public static int BEGIN_RUN_TIME = ConfigUtils.getInt("begin-run-time");
    protected static Log logger = LogFactory.getLog(TaskConfig.class);
    //本地参数信息
    public static String LOCAL_DATA_BACK = ConfigUtils.getString("local_data_back");


    //pass 服务器参数信息
    public static String SFTP_PASS_HOST = ConfigUtils.getString("sftp_pass_host");
    public static int SFTP_PASS_PORT = ConfigUtils.getInt("sftp_pass_port");
    public static String SFTP_PASS_USERNAME = ConfigUtils.getString("sftp_pass_username");
    public static String SFTP_PASS_PASSWORD = ConfigUtils.getString("sftp_pass_password");

    public static String SFTP_DATA_DOWN_PATH = ConfigUtils.getString("sftp_data_down_path");


}
