package cn.jinjing.plat.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.Random;

public class KafkaProducerUtil {
    private static Log log = LogFactory.getLog(KafkaProducerUtil.class);

    private static final String SERVERS = ConfigUtil.getProperties("kafka.bootstrap.servers");
    private static final String ACKS = ConfigUtil.getProperties("kafka.acks");
    private static final int RETRIES = Integer.parseInt(ConfigUtil.getProperties("kafka.retries"));
    private static KafkaProducer<String, String> producer;

    static {
        // 修改kafka日志输出级别
        Logger.getLogger("org.apache.kafka").setLevel(Level.OFF);
        Logger.getLogger("kafka.client").setLevel(Level.OFF);
        Logger.getLogger("kafka.producer").setLevel(Level.OFF);
        Logger.getLogger("kafka.utils").setLevel(Level.OFF);
        Logger.getLogger("kafka.consumer").setLevel(Level.OFF);
        Logger.getLogger("kafka.network").setLevel(Level.OFF);
        Logger.getLogger("org.apache.zookeeper").setLevel(Level.OFF);
        //初始化 producer
        initProducer();
    }

    private static void initProducer(){
        log.info(">>>>>>>>>>>>>>> Init Kafka Producer...");
        Properties props = new Properties();
        props.put("bootstrap.servers", SERVERS);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", ACKS);
        props.put("retries", RETRIES);
        props.put("retry.backoff.ms", "100");
        props.put("batch.size","262144");//256KB
        props.put("buffer.memory","33554432");//32M
        props.put("linger.ms","10000");//10s
        props.put("max.block.ms","60000");//60s
        props.put("max.in.flight.requests.per.connection","5");
        producer = new KafkaProducer<String, String>(props);
    }

    public static void sendMsg(String topic, String key, String msg) {
        int retry = 3;
        if(producer == null){
            initProducer();
        }
        boolean success = true;
        while(retry-- > 0){
            try {
                producer.send(new ProducerRecord<String, String>(topic, key, msg));
                success = true;
            } catch (Exception e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
                if(producer != null){
                    producer.flush();
                    producer.close();
                }
            }

            if(success){
                break;
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.error("Send Message Fail and Retry...");
            }
        }

    }
}
