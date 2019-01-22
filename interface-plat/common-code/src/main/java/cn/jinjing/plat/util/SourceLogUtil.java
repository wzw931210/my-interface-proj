package cn.jinjing.plat.util;

import cn.jinjing.plat.entity.SourceLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SourceLogUtil extends Thread{

    public static Log log = LogFactory.getLog(SourceLogUtil.class);
    //hbase 电信接口日志表
    private static String DX_LOG_TABLE = ConfigUtil.getProperties("interface_dx_log_table");
    private static int THREADNUM = Integer.parseInt(ConfigUtil.getProperties("save_dx_log_threadnum"));

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADNUM);

    private SourceLog sourceLog;

    public SourceLogUtil(SourceLog sourceLog){
        this.sourceLog = sourceLog;
    }

    @Override
    public void run() {
        String month ;
        if(sourceLog.getStartTime() != null){
            month = StringUtil.getStrFromDate(sourceLog.getStartTime(), "yyyyMM");
        }else{
            month = StringUtil.getStrFromDate(new Date(), "yyyyMM");
        }
        TableName tbName = TableName.valueOf(DX_LOG_TABLE + "_" + month);
        Table tb = null;
        try {
            tb = HbaseUtil.getConnection().getTable(tbName);
            String startTime = StringUtil.getMsecStrFromDate(sourceLog.getStartTime());
//            String rowKey = sourceLog.getDataSource() + "_" + sourceLog.getStartTime() + "_" + CreateKey.createKey(8);
            String rowKey = sourceLog.getDataSource() + "_" + startTime + "_" + CreateKey.generateShortUuid8();
            Put put = new Put(Bytes.toBytes(rowKey));
            if(!StringUtil.isEmpty(sourceLog.getLabel())){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("label"),Bytes.toBytes(sourceLog.getLabel()));
            }
            if(!StringUtil.isEmpty(sourceLog.getParams())){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("params"),Bytes.toBytes(sourceLog.getParams()));
            }
            if(sourceLog.getStartTime() != null){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("startTime"),Bytes.toBytes(StringUtil.getMsecStrFromDate(sourceLog.getStartTime())));
            }
            if(sourceLog.getEndTime() != null){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("endTime"),Bytes.toBytes(StringUtil.getMsecStrFromDate(sourceLog.getEndTime())));
            }
            if(!StringUtil.isEmpty(sourceLog.getStatusCode())){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("statusCode"),Bytes.toBytes(sourceLog.getStatusCode()));
            }
            if(!StringUtil.isEmpty(sourceLog.getMessage())){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("message"),Bytes.toBytes(sourceLog.getMessage()));
            }
            if(!StringUtil.isEmpty(sourceLog.getUserCode())){
                put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("userCode"),Bytes.toBytes(sourceLog.getUserCode()));
            }
            if(put.size() > 0){
                tb.put(put);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
        } finally {
            if(tb != null){
                try {
                    tb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveDxInterfaceLog(SourceLog dxLog){
        executorService.submit(new SourceLogUtil(dxLog));
    }

}
