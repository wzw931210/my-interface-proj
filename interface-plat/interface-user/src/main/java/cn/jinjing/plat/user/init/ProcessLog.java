package cn.jinjing.plat.user.init;

import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.user.dao.UserInfoMapper;
import cn.jinjing.plat.user.pojo.UserInfo;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.*;

import java.io.IOException;
import java.util.*;

public class ProcessLog extends Thread{
    public static Log log = LogFactory.getLog(ProcessLog.class);
    private UserInfoMapper userDao = (UserInfoMapper) SpringContextUtil.getBean("userInfoMapper");

    private static final String SERVERS = ConfigUtil.getProperties("kafka.bootstrap.servers");
    private static String TOPIC = ConfigUtil.getProperties("interface_log_topic");
    private static final String GROUP = ConfigUtil.getProperties("log_consumer_group");

    private static String INTERFACE_LOG_TABLE = ConfigUtil.getProperties("interface_log_table");

    private static KafkaConsumer<String, String> consumer;
    private static Properties props = new Properties();

    static {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //latest, earliest
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "524288"); //512KB
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "5000"); //5s
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");//30s
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");//10s
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");//1000条
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    }

    public static void initConsumer(){
        log.info(">>>>>>>>>> Init KafkaConsumer...");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
    }

    /**
     * 从Kafka消费日志信息，将日志记录到HBase，并统计费用信息定时更新到Mysql
     */
    @Override
    public void run() {

        initConsumer();

        Table tb = null;

        //每次处理时间不能超过SESSION_TIMEOUT_MS_CONFIG（30s）
        while (true) {
            try {
                //每次调用poll方法，将返回一个来自已分配的Partition的message集合（可能是空的），同时发送心跳
                //poll()方法的参数控制当Consumer在当前Position等待记录时，它将阻塞的最大时长。当有记录到来时，Consumer将会立即返回。但是，在返回前如果没有任何记录到来，Consumer将等待直到超出指定的等待时长
                ConsumerRecords<String, String> records = consumer.poll(10000);
                if(!records.isEmpty()){
                    long count = 0;
                    double money = 0.0;
                    String month = StringUtil.getStrFromDate(new Date(), "yyyyMM");
                    TableName tbName = TableName.valueOf(INTERFACE_LOG_TABLE+"_"+month);
                    tb = HbaseUtil.getConnection().getTable(tbName);
                    Put put;
                    List<Put> list = new ArrayList<>();
                    Map<String, Double> users = new HashMap<>();
                    for (ConsumerRecord<String, String> record : records)
                    {
                        count ++;
                        String serialNo = record.partition() + "_" + record.offset()%1000000;//hbase Rowkey标识唯一用
                        String value = record.value();
                        InterfaceLog interfaceLog = JSONObject.parseObject(value, InterfaceLog.class);

                        //计算消费
                        if(users.containsKey(interfaceLog.getUserCode())){
                            money = users.get(interfaceLog.getUserCode());
                            if(interfaceLog.getCharge()==null) {
                                log.info(">>>>>>>>>计算消费" + interfaceLog.getUserCode() + "--" + interfaceLog.getCharge());
                                log.info(interfaceLog.getStartTime());
                            }
                            users.put(interfaceLog.getUserCode(), money + (interfaceLog.getCharge()==null ? 0:interfaceLog.getCharge()));
                        }else{
                            users.put(interfaceLog.getUserCode(), interfaceLog.getCharge()) ;
                        }
                        String startTime = StringUtil.getMsecStrFromDate(interfaceLog.getStartTime());
                        String rowKey = interfaceLog.getUserCode() + "_" + startTime + "_" + serialNo;
                        put = new Put(Bytes.toBytes(rowKey));
                        if(!StringUtil.isEmpty(interfaceLog.getLabelCode())){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("label"),Bytes.toBytes(interfaceLog.getLabelCode()));
                        }
                        if(!StringUtil.isEmpty(interfaceLog.getInterCode())){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("type"),Bytes.toBytes(interfaceLog.getInterCode()));
                        }
                        if(!StringUtil.isEmpty(interfaceLog.getParams())){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("params"),Bytes.toBytes(interfaceLog.getParams()));
                        }
                        if(interfaceLog.getStartTime()!=null){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("startTime"),Bytes.toBytes(StringUtil.getMsecStrFromDate(interfaceLog.getStartTime())));
                        }
                        if(interfaceLog.getEndTime()!=null){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("endTime"),Bytes.toBytes(StringUtil.getMsecStrFromDate(interfaceLog.getEndTime())));
                        }
                        if(interfaceLog.getCharge()!=null){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("charge"),Bytes.toBytes(Double.toString(interfaceLog.getCharge())));
                        }
                        if(!StringUtil.isEmpty(interfaceLog.getFlag())){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("flag"),Bytes.toBytes(interfaceLog.getFlag()));
                        }
                        if(!StringUtil.isEmpty(interfaceLog.getReason())){
                            put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("reason"),Bytes.toBytes(interfaceLog.getReason()));
                        }
                        if(put.size() > 0){
                            list.add(put);
                        }
                    }
                    log.info(">>>>>>>>>> poll: " + count);
                    //日志入HBase
                    if(list.size() > 0){
                        tb.put(list);
                    }
                    //更新Mysql余额
                    if(users.size() > 0){
                        UserInfo user;
                       for (Map.Entry<String, Double> entry : users.entrySet()){
                           if(entry.getValue() > 0){
                               log.info(">>>>>>>>>> presist user of money: " + entry.getKey() + "--" + entry.getValue());
                               user = new UserInfo();
                               user.setUserCode(entry.getKey());
                               user.setCharge(entry.getValue());
                               userDao.updateRemainByUser(user);
                           }
                       }
                    }
                    //提交offset
                    consumer.commitSync();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                consumer.close();
                initConsumer();
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
    }

    public static void main(String[] args) {
        Put put = new Put("aaaa".getBytes());
        System.out.println(put.size());
        put.addColumn("info".getBytes(), "name".getBytes(), "zz".getBytes());
        System.out.println(put.size());

    }

}
