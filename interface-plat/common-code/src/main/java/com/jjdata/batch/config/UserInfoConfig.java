package com.jjdata.batch.config;

import com.jjdata.batch.util.ConfigUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ho.yaml.Yaml;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shipeien
 * @version 1.0
 * @Title: UserInfoConfig
 * @ProjectName run-batch
 * @Description: 用户信息初始化
 * @email shipeien@jinjingdata.com
 * @date 2018/12/1714:11
 */
public class UserInfoConfig {
    protected static Log logger = LogFactory.getLog(UserInfoConfig.class);
    public static Map<String, String> MAP_USER_INFO = new ConcurrentHashMap<>();
    //初始化要处理的标签信息
    public static void initUserInfo(){
        try {
            logger.info("初始化用户信息...");
            InputStreamReader is = new InputStreamReader(ConfigUtils.class.getClassLoader().getResourceAsStream("user-info.yml"), "UTF-8");
            Map father = Yaml.loadType(is, HashMap.class);
            for(Object key:father.keySet()){
                if("user".equals(key)){
                    //先填入
                    List<HashMap<String,String>> userMapList= (List<HashMap<String, String>>) father.get(key);
                    for(HashMap tagTypeMap:userMapList ){
                        MAP_USER_INFO.put((String)tagTypeMap.get("code"),(String)tagTypeMap.get("password"));
                    }
                }
            }
            for(String tagFatherName:MAP_USER_INFO.keySet()){
                logger.info(" 用户信息---->"+tagFatherName+" - "+MAP_USER_INFO.get(tagFatherName));
            }
            logger.info("初始化用户信息完成...");
        }catch (Exception e){
            logger.error("初始化用户信息异常",e);
            e.printStackTrace();
        }
    }
}
