package cn.jinjing.plat.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalLabelData extends Thread{

    private static Log log = LogFactory.getLog(LocalLabelData.class);
    //本地号码表
    private static String LOCAL_PHONE_TABLE = ConfigUtil.getProperties("local_phone_table");
    private static int THREADNUM = Integer.parseInt(ConfigUtil.getProperties("save_local_data_threadnum"));
    private static String LOCALDATA_KEY_PREFIX = ConfigUtil.getProperties("redis.localdata.key.prefix");

    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADNUM);

    private String tableName;
    private String mdn;
    private String month;
    private Map<String, String> values;

    public LocalLabelData(String tableName, String mdn, String month, Map<String, String> values){
        this.tableName = tableName;
        this.mdn = mdn;
        this.month = month;
        this.values = values;
    }


    @Override
    public void run() {
        Table tb = null;
        try {
            //save HBase
            String rowKey = mdn + "_" + month;
            TableName tbName = TableName.valueOf(tableName);
            tb = HbaseUtil.getConnection().getTable(tbName);
            Get get;
            Put put = new Put(Bytes.toBytes(rowKey));
            for(Map.Entry<String, String> entry : values.entrySet()){
                get = new Get(Bytes.toBytes(rowKey));
                get.addColumn(Bytes.toBytes("info"), Bytes.toBytes(entry.getKey()));
                if(!tb.exists(get)){
                    put.addColumn(Bytes.toBytes("info"),Bytes.toBytes(entry.getKey()),Bytes.toBytes(entry.getValue()));
                }
            }
            if(put.size() > 0){
                tb.put(put);
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
        } finally {
            if(tb != null){
                try {
                    tb.close();
                } catch (IOException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
            }
        }
    }


    public static String getLocalLabel(String tableName, String mdn, String month, String columnName){
        String value = null;
        String rowKey = mdn + "_" + month;
        Table tb = null;
        try {
            //查询redis
            JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
            String redisKey = LOCALDATA_KEY_PREFIX + ":" + tableName + ":" + rowKey;
            String values = jc.get(redisKey);
            if (!StringUtil.isEmpty(values)){
                JSONObject jSONObject = JSONObject.parseObject(values);
                value = jSONObject.getString(columnName);
            }
            //redis中不存在查询HBase
            if(StringUtil.isEmpty(value)){
                TableName tbName = TableName.valueOf(tableName);
                tb = HbaseUtil.getConnection().getTable(tbName);
                Get get = new Get(Bytes.toBytes(rowKey));
                get.addColumn(Bytes.toBytes("info"), Bytes.toBytes(columnName));
                if(tb.exists(get)){
                    Result result = tb.get(get);
                    value = Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes(columnName)));
                }
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
        } finally {
            if(tb != null){
                try {
                    tb.close();
                } catch (IOException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
            }
        }
        return value;
    }


    public static void putLocalLabel(String tableName, String mdn, String month, String columnName, String value){
        try {
            //save redis
            String rowKey = mdn + "_" + month;
            JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
            String redisKey = LOCALDATA_KEY_PREFIX + ":" + tableName + ":" + rowKey;
            JSONObject jSONObject;
            String jsonValues = jc.get(redisKey);
            if (!StringUtil.isEmpty(jsonValues)){
                jSONObject = JSONObject.parseObject(jsonValues);
                jSONObject.put(columnName, value);
            }else{
                jSONObject = new JSONObject();
                jSONObject.put(columnName, value);
            }
            jc.setex(redisKey, 60, jSONObject.toJSONString());
        } catch (Exception e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        Map<String, String> values = new HashMap<>();
        values.put(columnName, value);
        //save HBase
        executorService.submit(new LocalLabelData(tableName, mdn, month, values));
    }


    public static void putLocalLabels(String tableName, String mdn, String month, Map<String, String> values){
        try {
            //save redis
            String rowKey = mdn + "_" + month;
            JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
            String key = tableName + ":" + rowKey;
            JSONObject jSONObject;
            String jsonValues = jc.get(key);
            if (!StringUtil.isEmpty(jsonValues)){
                jSONObject = JSONObject.parseObject(jsonValues);
                for(Map.Entry<String, String> entry : values.entrySet()){
                    jSONObject.put(entry.getKey(), entry.getValue());
                }
            }else{
                jSONObject = new JSONObject();
                for(Map.Entry<String, String> entry : values.entrySet()){
                    jSONObject.put(entry.getKey(), entry.getValue());
                }
            }
            jc.setex(key, 60, jSONObject.toJSONString());
        } catch (Exception e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        //save HBase
        executorService.submit(new LocalLabelData(tableName, mdn, month, values));
    }

    public static boolean existsMdn(String mdn){
        boolean flag = false;
        if(!StringUtil.isEmpty(LOCAL_PHONE_TABLE)){
            TableName tbName = TableName.valueOf(LOCAL_PHONE_TABLE);
            Table tb = null;
            try {
                tb = HbaseUtil.getConnection().getTable(tbName);
                Get get = new Get(Bytes.toBytes(mdn));
                flag = tb.exists(get);
            }
            catch (Exception e){
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
            finally {
                if(tb != null){
                    try {
                        tb.close();
                    } catch (IOException e) {
                        log.error(ExceptionUtil.printStackTraceToString(e));
                    }
                }
            }
        }
        return flag;
    }

}
