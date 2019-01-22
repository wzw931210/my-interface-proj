package cn.jinjing.plat.unicomlabel.util;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.SourceLogUtil;
import cn.jinjing.plat.util.UHttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Map;

/**
 * 联通公共接口
 */
public class UnicomLabel {
    private static Log log = LogFactory.getLog(UnicomLabel.class);

    public static ReLabel getLabelResult(Map<String, String> httpParams, String label,String userCode){
        ReLabel reLabel = new ReLabel();
        SourceLog dxLog = new SourceLog();
        dxLog.setDataSource("03");
        dxLog.setLabel(label);
        dxLog.setUserCode(userCode);
        dxLog.setParams(JSONObject.toJSONString(httpParams));
        dxLog.setStartTime(new Date());
        String reqUrl;
        String valueTag;
        try{
            int k = 3;//错误重试

            while(k -- >0){

                reqUrl = UnicomConfigUtil.getProperties(label).split(",")[1];
                valueTag = UnicomConfigUtil.getProperties(label).split(",")[0];

                log.info("》》》》》》》》》》联通url--"+reqUrl);

                //发送请求
                UHttpClient.Res result = UnicomHttpClient.doRequest(UHttpClient.Method.post, reqUrl, httpParams, "UTF-8", true);
                dxLog.setEndTime(new Date());
                JSONObject resultJson = JSONObject.parseObject(result.content);
                if(result.statusCode==200) {//成功返回
                    String status = resultJson.getString("status");
                    String code = resultJson.getString("code");
                    if ("1".equals(status)) {//成功
                        if("14".equals(code)) {//不支持的号码
                            reLabel.setData(resultJson.getString("errorDesc"));
                            reLabel.setFlag(true);
                            break;
                        }
                        String value = resultJson.getString(valueTag);
                        if("m1talk".equals(label)){
                            value = resultJson.getString("calledscore")+"-"+resultJson.getString("callerscore");
                        }
                        reLabel.setData(value != null ? value : "");
                        reLabel.setFlag(true);
                        log.info("》》》》》》》》》》联通标签"+label+"调用成功");
                        dxLog.setStatusCode(Integer.toString(result.statusCode));
                        log.info("::::::::::返回结果："+result.content);
                        break;
                    } else {//失败
                        String errorCode = resultJson.getString("code");//错误状态码
                        String errorDesc = resultJson.getString("errorDesc");
                        log.error(">>>>>>>>>>>"+status+":调用联通公共接口失败,http状态码为200");
                        log.error(result.content+errorDesc);
                        reLabel.setFlag(false);
                        reLabel.setMessage(errorCode+errorDesc);

                        dxLog.setStatusCode(errorCode);
                        dxLog.setMessage(errorDesc);
                        break;
                    }
                }else {
                    log.error("系统内部错误 and Retry...");
                    log.error(result.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : "系统内部错误！");
                    dxLog.setStatusCode(Integer.toString(result.statusCode));
                    dxLog.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : null);
                }
            }
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            reLabel.setFlag(false);
            reLabel.setMessage("系统内部错误！");
        }
        SourceLogUtil.saveDxInterfaceLog(dxLog);
        return reLabel;
    }
}
