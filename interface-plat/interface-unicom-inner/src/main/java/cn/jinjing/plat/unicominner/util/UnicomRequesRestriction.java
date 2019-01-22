package cn.jinjing.plat.unicominner.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 清理请求计数key值线程
 */
public class UnicomRequesRestriction extends Thread{
    private static Log log = LogFactory.getLog(UnicomRequesRestriction.class);
    /**
     * 每秒请求计数器
     */
    private static ConcurrentMap<Long, Integer> concurrentMapCounts = new ConcurrentHashMap<>();
    private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     *#清理key 值线程多少秒执行一次 毫秒
     */
    private static int CLEAR_KEY_EXEC_TIMES = Integer.parseInt(Config.getString("clear_key_exec_times"));
    /**
     * 每秒可请求总次数
     */
    private static int TOTAL_REQUEST_TIMES = Integer.parseInt(Config.getString("total_request_times"));
    /**
     * 计算当前访问线程数量是否需要限制
     * @param thisMilliSecond
     * @return
     */
    static synchronized boolean allowRequest(long thisMilliSecond) {
        Long key=thisMilliSecond / 1000;
        Integer oldCount = concurrentMapCounts.get(key);
        Integer newCount = 1;
        //判断是否存在值
        if (oldCount == null) {
            //不存在则放入计数，可以处理请求
            concurrentMapCounts.put(key, newCount);
//            log.info(sdf.format(key*1000)+"开始记录请求次数"+concurrentMapCounts.get(key));
            return true;
        } else {
            //存在则判断数量是否超过限定值
            if(oldCount>= TOTAL_REQUEST_TIMES){
                log.info(sdf.format(key*1000)+"请求次数已经达到上限"+oldCount);
                //如果超过限定值则需要等待
                return false;
            }else{
                //没有超过则+1 ，可以处理请求
                newCount = oldCount + 1;
//                log.info(sdf.format(key*1000)+"请求次数+1"+concurrentMapCounts.get(key));
                //值替换，每次替换时都会比较上面拿到oldCount是否就是当前map里面的值，是才替换，否则继续获取
                concurrentMapCounts.replace(key, oldCount, newCount);
                return true;
            }
        }
    }
    @Override
    public void run(){
        while(true){
            try {
                log.info("清理key值线程......记录key值数量："+concurrentMapCounts.size());
                //清理5秒以前的KEY值
                //获取当前秒值
                long nowTimeMillis = System.currentTimeMillis()/1000;

                //遍历所有建值
                for(Long key : concurrentMapCounts.keySet()) {
                    //清理5秒之前的key
                    if(nowTimeMillis-key>5){
                        log.info(sdf.format(key*1000)+"请求次数"+concurrentMapCounts.get(key));
                        concurrentMapCounts.remove(key);
                    }

                }
                Thread.sleep(CLEAR_KEY_EXEC_TIMES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
