package cn.jinjing.plat.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JsonUtil {
    private static Log log = LogFactory.getLog(JsonUtil.class);

    public static <T> T StringToBean(String str, Class<T> clazz){
        T bean = null;
        try {
            if(str != null && !"".equals(str)){
                JsonObject json = new JsonParser().parse(str).getAsJsonObject();
                bean = new Gson().fromJson(json, clazz);
            }
        }catch (Exception e){
            bean = null;
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return bean;
    }

}
