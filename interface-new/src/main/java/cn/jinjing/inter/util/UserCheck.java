package cn.jinjing.inter.util;

import cn.jinjing.inter.pojo.Result;
import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.service.user.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class UserCheck {


    private static UserService userService;
    @Autowired //注入静态bean
    public UserCheck(UserService userService){
        UserCheck.userService = userService;
    }

    public static JSONObject checkUserInfo(Map<String,String> map){
        ReObject ro ;
        JSONObject result = new JSONObject();
        result.put("flag", Result.SUCCESS.getValue());

        //验证QPS
        ro = CheckUserUtil.checkQPS(map.get("userCode"));
        if(!ro.isFlag()) {
            System.out.println("验证qps失败");
            result.put("flag", Result.ERROR.getValue());
            result.put("message", ro.getMessage());
            result.put("code", StatusCode.OVERSPEED.getCode());
        }


        //验证用户权限
//			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
//				ro = CheckUserUtil.checkUser(cacheUser, accessKey, label);
//				if(!ro.isFlag() == false) {
//					System.out.println("验证用户权限失败！");
//					result.put("flag", Result.ERROR.getValue());
//					result.put("message", ro.getMessage());
//					result.put("code", ro.getCode());
//				}
//			}
        return result;
    }
}
