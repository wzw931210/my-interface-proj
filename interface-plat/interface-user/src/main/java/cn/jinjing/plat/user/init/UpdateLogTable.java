package cn.jinjing.plat.user.init;

import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.HbaseUtil;
import cn.jinjing.plat.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Date;

public class UpdateLogTable extends Thread{

    private static Log log = LogFactory.getLog(UpdateLogTable.class);
    private static String DX_LOG_TABLE = ConfigUtil.getProperties("interface_dx_log_table");
    private static String INTERFACE_LOG_TABLE = ConfigUtil.getProperties("interface_log_table");
    @Override
    public void run(){
        String month = StringUtil.getStrFromDate(new Date(), "yyyyMM");
        String nextMonth = StringUtil.getPreMonth();

        String tableName1 = INTERFACE_LOG_TABLE+"_"+month;
        String nextTableName1 = INTERFACE_LOG_TABLE + "_" + nextMonth;//用户调用接口日志表

        String tableName2 = DX_LOG_TABLE+"_"+month;
        String nextTableName2 = DX_LOG_TABLE + "_" + nextMonth;//调用数据源接口日志表
    try {
        HbaseUtil.createTable(tableName1, "info");
        HbaseUtil.createTable(nextTableName1, "info");
        HbaseUtil.createTable(tableName2, "info");
        HbaseUtil.createTable(nextTableName2, "info");
        log.info(">>>>>>>>>>成功创建" + nextMonth +"和" +month+ "月份的用户调用接口日志表和调用数据源接口日志表");
    }catch (Exception e){
        log.info(">>>>>>>>>>创建HBase日志表失败！");
    }
    }
}
