package com.jjdata.batch.config;

import com.jjdata.batch.util.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shipeien
 * @version 1.0
 * @Title: TaskConfig
 * @ProjectName run-batch
 * @Description: 任务处理配置参数
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1115:15
 */
public class TaskConfig {
    //全局处理状态
    public static boolean RUN_STATUS=true;

    protected static Log logger = LogFactory.getLog(TaskConfig.class);
    public static String Separator="^A";
    //本地参数信息

    public static String save_data_db = ConfigUtils.getString("save_data_db");
    public static String save_data_path = ConfigUtils.getString("save_data_path");
    public static String data_file_name = ConfigUtils.getString("data_file_name");
    public static int num_begin_telTile = ConfigUtils.getInt("num_begin_telTile");
    public static boolean date_check = ConfigUtils.getBoolean("date_check");
    public static boolean price_check = ConfigUtils.getBoolean("price_check");
    public static boolean telephone_number_type = ConfigUtils.getBoolean("telephone_number_type");
    //隔日校验
    public static boolean next_date_check = ConfigUtils.getBoolean("next_date_check");
    public static boolean isRun=true;
    public static int last_date = 0;
    //文件保存方式 1-号段前四位
    //文件保存方式 2-单文件
//    public static String save_file_type = "1";
    public static String save_file_type = ConfigUtils.getString("save_file_type");
    public static String isRun_message="全局开关已经关闭，请检查额度是否超限用完...";

    public static Integer publicPrice=0;
    public static synchronized void reducePublicPrice(){
        TaskConfig.publicPrice--;
        logger.info("剩余点数------------------------------------------>"+publicPrice);
    }
    public static synchronized void isRunFalse(){
        //设置设为false 的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        last_date=Integer.parseInt(format.format(System.currentTimeMillis()));
        isRun=false;
    }


    //http请求参数
    public static String HTTP_LOGINURL = ConfigUtils.getString("loginUrl");
    public static String HTTP_REQUESTURL= ConfigUtils.getString("requestUrl");
    public static int HTTP_READMDNTHREADPOOLSIZE= ConfigUtils.getInt("readMDNThreadPoolSize");
    public static String HTTP_DEFAULT_USERCODE= ConfigUtils.getString("default_usercode");
    public static String HTTP_DEFAULT_PASSWORD= ConfigUtils.getString("default_password");
    public static String HTTP_ENCRYPT= ConfigUtils.getString("encrypt");
    public static int HTTP_SYNC_TIME= ConfigUtils.getInt("sync_time");

    public static String BATCH_TYPE = ConfigUtils.getString("batch_Type");
    public static Long TIME_INFO_MAX = ConfigUtils.getLong("timeInfoMAX");
    public static String TELE_UNICOM = ConfigUtils.getString("teleUnicom");
    public static String RUN_MONTH = ConfigUtils.getString("run_month");
    public static String LABEL_FILE_NAME = ConfigUtils.getString("label_file_name");
    public static int BATCH_GENERATE_TEL_NUM = ConfigUtils.getInt("batch_generate_tel_num");


    public static Map<String, String> userKeys = new Hashtable<>();




}
