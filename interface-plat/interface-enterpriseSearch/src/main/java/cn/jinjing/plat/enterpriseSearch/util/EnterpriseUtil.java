package cn.jinjing.plat.enterpriseSearch.util;

import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.UHttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Map;

public class EnterpriseUtil {

    private static Log log = LogFactory.getLog(EnterpriseUtil.class);
    private static String ENTERPRISE_URL = ConfigUtil.getProperties("enterprise_url");

    public static ReObject getReObject(Map<String, String> httpParams){
        ReObject reObject = new ReObject(false);
        SourceLog enterpriseLog = new SourceLog();
        enterpriseLog.setDataSource("06");//企业搜索
        enterpriseLog.setLabel("enterpriseSearch");
        enterpriseLog.setParams(JSONObject.toJSONString(httpParams));
        enterpriseLog.setStartTime(new Date());
        log.info("++++++企业搜索请求开始时间："+ System.currentTimeMillis());
        String reqUrl = ENTERPRISE_URL;

        try {
            int tryKey = 3;
            while (tryKey-- >0) {

                //发送请求
                UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.get, reqUrl, httpParams, "UTF-8", true);

                // TODO 测试
//                String result = "";
//                UHttpClient.Res res = new UHttpClient.Res(200,result);

                log.info("++++++企业搜索请求结束时间："+ System.currentTimeMillis());
                enterpriseLog.setEndTime(new Date());
                if (res.statusCode == 200) {//成功返回
                    JSONObject resultJson = JSONObject.parseObject(res.content);
                    String resultCode = resultJson.getString("resultCode");//状态码
                    if("0".equals(resultCode)){
                        reObject.setMessage(resultJson.getString("resultMsg"));
                        reObject.setFlag(true);
                        JSONObject data = resultJson.getJSONObject("resultData");
                        reObject.setData(data);
                        enterpriseLog.setStatusCode(Integer.toString(res.statusCode));
                        break;
                    }else{
                        reObject.setFlag(false);
                        reObject.setMessage(resultJson.getString("resultMsg"));
                        log.error(resultCode+":调用企业搜索接口失败 and Retry...");
                        log.error(resultJson.getString("resultMsg"));
                        enterpriseLog.setStatusCode(resultCode);
                        enterpriseLog.setMessage(resultJson.getString("resultMsg"));
                    }
                }else {
                    log.error("系统内部错误 and Retry...");
                    log.error(res.content);
                    enterpriseLog.setStatusCode(String.valueOf(res.statusCode));
                    reObject.setFlag(false);
                    reObject.setMessage("系统内部错误,状态码："+res.statusCode);
                }
            }
        }catch (Exception e){
            reObject.setFlag(false);
            reObject.setMessage("接口内部错误");
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return reObject;
    }
}
