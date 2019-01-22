package cn.jinjing.plat.shuzun.util;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShuZunLabel {

    private static Log log = LogFactory.getLog(ShuZunLabel.class);
    private static String REQUEST_URL = ConfigUtil.getProperties("shuzun_api_url_test");//测试url
//    private static String REQUEST_URL = ConfigUtil.getProperties("shuzun_api_url");//正式url

    /**
     *
     */
    public static ReLabel getLabelResult(Map<String, String> httpParams, String label,String userCode){
        ReLabel reLabel = new ReLabel();
        SourceLog shuzunLog = new SourceLog();
        shuzunLog.setDataSource("04");
        shuzunLog.setLabel(label);
        shuzunLog.setUserCode(userCode);
        shuzunLog.setParams(JSONObject.toJSONString(httpParams));
        shuzunLog.setStartTime(new Date());

        try{
            int k = 3;
            while ( k-- >0) {
                String reqUrl = REQUEST_URL;

                //发送请求
                UHttpClient.Res result = UHttpClient.doRequest(UHttpClient.Method.post, reqUrl, httpParams, "UTF-8", true);

//                String content = "{\"resCode\":\"0000\",\"resMsg\":\"请求成功\",\"tranNo\":\"20180731111048f1130103-c\",\"sign\":\"9195B7B72BA366BBB8D1206C7E19B892\",\"data\":[{\"statusCode\":1,\"statusMsg\":\"查询成功,查得结果\",\"quotaID\":\"Z0008\",\"quotaValue\":\"江苏 南京\",\"channel\":2}]}";
//                UHttpClient.Res result = new UHttpClient.Res(200,content);
                shuzunLog.setEndTime(new Date());
                if(result.statusCode == 200){
                    JSONObject resultJson = JSONObject.parseObject(result.content);
                    String resCode = resultJson.getString("resCode");//状态码
                    if("0000".equals(resCode)){//成功
                        JSONArray jsonArray = resultJson.getJSONArray("data");
                        List<String> list = jsonArray.toJavaList(String.class);
                        JSONObject valueJson =  JSONObject.parseObject(list.get(0));//数据
                        String value = valueJson.getString("quotaValue");
                        reLabel.setData(value != null ? value : "");
                        reLabel.setFlag(true);
                        shuzunLog.setStatusCode(Integer.toString(result.statusCode));
                        break;
                    }else{
                        String resMsg = resultJson.getString("resMsg") != null ? resultJson.getString("resMsg") : "";
                        log.error(resCode+":调用数尊接口失败");
                        log.error(resMsg);
                        reLabel.setFlag(false);
                        reLabel.setMessage(resCode);
                        reLabel.setData(resMsg);
                        shuzunLog.setStatusCode(resCode);
                        shuzunLog.setMessage(resMsg);
                        break;
                    }
                }else{
                    log.error("系统内部错误 and Retry...");
                    log.error(result.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage(result.content);
                    shuzunLog.setStatusCode(Integer.toString(result.statusCode));
                    shuzunLog.setMessage(result.content);
                }
            }
        }catch(Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            reLabel.setFlag(false);
            reLabel.setMessage("系统内部错误！");
        }
        SourceLogUtil.saveDxInterfaceLog(shuzunLog);
        return reLabel;
    }

    public static void main(String [] args){
        Map<String, String> httpParams = new LinkedHashMap<>();
//        ShuZunLabel.getLabelResult(httpParams,"provinceCity");
    }
}
