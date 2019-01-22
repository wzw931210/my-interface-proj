package cn.jinjing.plat.user.util;

import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.user.pojo.CacheKey;
import cn.jinjing.plat.user.pojo.UserInfo;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.JsonUtil;
import cn.jinjing.plat.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CacheUtil {

    private static Log log = LogFactory.getLog(CacheUtil.class);
    //Redis中缓存cacheUserLabel的key前缀
    private static final int EXPIRE_MIN = Integer.parseInt(ConfigUtil.getProperties("redis.cache.expire.min"));
    //Redis中缓存cacheUserLabel的key前缀
    private static final String CACHELABEL_KEY_PREFIX = ConfigUtil.getProperties("redis.cacheuser.key.prefix");
    //Redis中缓存Accesskey的key
    private static final String ACCESSKEY_KEY = ConfigUtil.getProperties("redis.accesskey.key");
    //cacheUserLabel 更新时间field
    private static final String CACHE_USER_UPDATE_TIME_KEY = "updateTime.key";

    /**
     * 添加用户配置信息
     * @param cacheUserLabels
     */
    public static void addUserLabels(UserInfo user, List<CacheUserLabel> cacheUserLabels){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        String key = CACHELABEL_KEY_PREFIX + "." + user.getUserCode();
        String field = "";
        String value = "";
        for(CacheUserLabel cacheUserLabel : cacheUserLabels){
            //labelcode_type_telcom
            field = cacheUserLabel.getLabelCode() + "_" + cacheUserLabel.getType() + "_" + cacheUserLabel.getTelcom();
            value = JSONObject.toJSONString(cacheUserLabel);
            jc.hset(key, field, value);
        }
        jc.expire(key, EXPIRE_MIN * 60 * 1000);
    }

    /**
     * 获取用户标签缓存配置
     * @param userCode
     * @return
     */
    public static CacheUserLabel getCacheUserLabel(String userCode, String labelCode, String type, String telcom){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        CacheUserLabel cacheUserLabel = null;
        String key = CACHELABEL_KEY_PREFIX + "." + userCode;
        String field = labelCode + "_" + type + "_" + telcom;
        String str = jc.hget(key, field);
        if(str != null && !"".equals(str)){
            cacheUserLabel = JsonUtil.StringToBean(str, CacheUserLabel.class);
        }
        return cacheUserLabel;
    }

    /**
     * 设置用户标签缓存更新时间毫秒
     * @return
     */
    public static void setCacheUserUpdateTime(){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        String key = CACHELABEL_KEY_PREFIX + "." + CACHE_USER_UPDATE_TIME_KEY;
        jc.setex(key, EXPIRE_MIN * 60 * 1000, Long.toString(System.currentTimeMillis()));
    }

    /**
     * 获取用户标签缓存更新时间毫秒
     * @return
     */
    public static Long getCacheUserUpdateTime(){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        String key = CACHELABEL_KEY_PREFIX + "." + CACHE_USER_UPDATE_TIME_KEY;
        String str = jc.get(key);
        Long millis = 0L;
        if(str != null && !"".equals(str)){
            try {
                millis = Long.parseLong(str);
            } catch (NumberFormatException e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
                log.error("string is : " + str);
                millis = 0L;
            }
        }
        return millis;
    }

    /**
     * 添加用户accesskey
     * @param userCode
     * @param cacheKey
     */
    public static void addAccessKey(String userCode, CacheKey cacheKey){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        jc.hset(ACCESSKEY_KEY, userCode, JSONObject.toJSONString(cacheKey));
        log.info("》》》》》accessKey 写入redis》》》》");
    }

    /**
     * 删除用户accesskey
     * @param userCode
     */
    public static void removeAccessKey(String userCode){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        jc.hdel(ACCESSKEY_KEY, userCode);
    }

    /**
     * 获取用户accesskey
     * @param userCode
     */
    public static CacheKey getAccessKey(String userCode){
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        String str = jc.hget(ACCESSKEY_KEY, userCode);
        CacheKey cacheKey = null;
        if(str != null && !"".equals(str)){
            cacheKey = JsonUtil.StringToBean(str, CacheKey.class);
        }
        return cacheKey;
    }

    /**
     * 获取用户accesskey
     */
    public static Map<String, String> getAllAccessKey(){
        Map<String, String> map ;
        JedisCluster jc = RedisUtil.getInstance().getJedisCluster();
        try {
            map = jc.hgetAll(ACCESSKEY_KEY);
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            map = null;
        }
        return map;
    }

    public static void main(String[] args) {
        CacheUserLabel cacheUserLabel =  getCacheUserLabel("admin","age","0","0");
        System.out.println(cacheUserLabel.getLabelCode());
    }
}
