package cn.jinjing.plat.digital.util;

import cn.jinjing.plat.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.JedisCluster;


public class CacheUtil {

    private static Log log = LogFactory.getLog(CacheUtil.class);
    //Redis中缓存Token的key
    private static final String DIGITALTOKEN_KEY = ConfigUtil.getProperties("redis.digitaltoken.key");
    //失败重试次数
    private static int RETRY = 3;

    //获取Token
    public static String getToken() {
        int  retry = RETRY;
        boolean flag = false;
        String token = null;

        while(retry-- > 0){
            try {
                JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
                token = jc.get(DIGITALTOKEN_KEY);
                if(!StringUtil.isEmpty(token)){
                    flag = true;
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
            }

            if(flag){
                break;
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.error("Get Token From Redis Error and Retry...");
            }
        }

        if (token != null) {
            return token.split("#")[0];
        } else {
            return "";
        }
    }

    //获取Token和置时间
    public static String getTokenAndTime() {
        int  retry = RETRY;
        boolean flag = false;
        String token = null;

        while(retry-- > 0){
            try {
                JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
                token = jc.get(DIGITALTOKEN_KEY);
                if(!StringUtil.isEmpty(token)){
                    flag = true;
                }
            } catch (Exception e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
            }

            if(flag){
                break;
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.error("Get Token From Redis Error and Retry...");
            }
        }


        if (token != null) {
            return token;
        } else {
            return "";
        }
    }

    //设置Token和时间
    public static void putTokenAndTime(String tokenID) {
        int  retry = RETRY;
        boolean flag = false;

        if(tokenID == null){
            tokenID = "";
        }

        while(retry-- > 0){
            try {
                JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
                jc.set(DIGITALTOKEN_KEY, tokenID + "#" + System.currentTimeMillis());
                flag = true;
            } catch (Exception e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
            }

            if(flag){
                break;
            }else{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.error("Put Token To Redis Error and Retry...");
            }
        }
    }


}
