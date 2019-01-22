package cn.jinjing.plat.user.impl;

import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.user.dao.UserInfoMapper;
import cn.jinjing.plat.service.user.UserService;
import cn.jinjing.plat.user.pojo.*;
import cn.jinjing.plat.user.util.CacheUtil;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    public static Log log = LogFactory.getLog(UserServiceImpl.class);
    private static String INTERFACE_LOG_TOPIC = ConfigUtil.getProperties("interface_log_topic");

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 每分钟只能登录一次，登录时根据配置参数决定需不需要更新用户缓存（过期删）
     */
    public ReObject logon(String userCode, String passwd){

        UserInfo user = new UserInfo();
        user.setUserCode(userCode);
        user.setPasswd(MD5.cell32(passwd));
        user = userInfoMapper.logon(user);
        if(user == null){
            return new ReObject(false, "用户名或密码错误");
        }
        //用户合法
        else{
            List<CacheUserLabel> labels = new ArrayList<>();
            //生成20位随机accessKey并缓存
            JSONObject jo = new JSONObject();
            jo.put("userCode", user.getUserCode());
            String accessKey = CreateKey.createKey(20);
            jo.put("accessKey", accessKey);
            jo.put("securityKey", user.getSecurityKey());
            jo.put("qps",user.getQps());
            if(labels.size() > 0){
                jo.put("labels", JSONObject.toJSONString(labels));
            }
            log.info(">>>>>>>>>>>>>>>>>>>>>>> 登录时更新用户accessKey缓存： " + user.getUserCode());
            CacheUtil.addAccessKey(user.getUserCode(), new CacheKey(accessKey, new Date().getTime()));
            return new ReObject(true, jo);
        }
    }

    //记录用户调用数据接口明细
    public ReObject insertLog(InterfaceLog interfaceLog) {
        try {
            String key = CreateKey.createKey(2);
            String msg = JSONObject.toJSON(interfaceLog).toString();
            KafkaProducerUtil.sendMsg(INTERFACE_LOG_TOPIC, key, msg);
            return new ReObject(true);
        } catch (Exception e) {
            log.error(ExceptionUtil.printStackTraceToString(e));
            return new ReObject(false);
        }
    }



    //获取用户标签缓存信息
    public CacheUserLabel getUserLabelInfo(String userCode, String labelCode, String type, String telcom){
        CacheUserLabel cacheUserLabel = CacheUtil.getCacheUserLabel(userCode, labelCode, type, telcom);
        if(cacheUserLabel != null){
            CacheKey cacheKey = CacheUtil.getAccessKey(userCode);
            if(cacheKey != null){
                cacheUserLabel.setAccessKey(cacheKey.getAccessKey());
            }
        }
        return cacheUserLabel;
    }

    /**
     * 获取所有userCode
     * @return List<String>
     */
    @Override
    public List<String> getAllUserCode() {
        List<UserInfo> list = userInfoMapper.queryAllUser();
        List<String> result = new ArrayList<>();
        for(UserInfo userInfo : list){
            result.add(userInfo.getUserCode());
        }
        return result;
    }

}
