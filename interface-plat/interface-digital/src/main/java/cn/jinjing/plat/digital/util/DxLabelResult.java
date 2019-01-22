package cn.jinjing.plat.digital.util;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.digital.init.TokenThread;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;

public class DxLabelResult {

    public static Log log = LogFactory.getLog(DxLabelResult.class);

    /**
     * 调电信内部接口
     */
    public static ReLabel getLabelResult (String product,String module,String interfaceCode,HashMap<String,String> httpParams,String userCode){
        ReLabel reLabel = new ReLabel();
        SourceLog dxLog = new SourceLog();
        dxLog.setDataSource("00");
        dxLog.setLabel(interfaceCode);
        dxLog.setUserCode(userCode);
        dxLog.setParams(JSONObject.toJSONString(httpParams));
        dxLog.setStartTime(new Date());
        try{
            //调电信接口
//            int k = 3;//错误重试
//            while(k-- > 0 ){
                String reqUrl = TokenThread.getReqUrl(product, module, "_"+interfaceCode);
                if(StringUtil.isEmpty(reqUrl)){
                    log.error("Token is Null and Retry...");
                    log.info(">>>>>>url:"+reqUrl);
                    reLabel.setFlag(false);
                    reLabel.setMessage("电信 Token is Null！");
//                    continue;
                }
                //发送请求
                UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.get, reqUrl, httpParams, "UTF-8", true);
                dxLog.setEndTime(new Date());
//                log.info(">>>>请求完成！");
                if(res != null && res.statusCode==200){//成功返回
//                    log.info(">>>>请求完成！"+res.content);
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(true);
                    JSONObject data = valueMap.getJSONObject("data");
                    String value = data.getString("value");
                    reLabel.setData(value != null?value:"");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
//                    break;
                }
                //非本网号码
                else if(res != null && res.statusCode==224){
                    log.error(res.statusCode+":非本网号码！");
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage("224");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
//                    break;
                }
                //非本网号码
                else if(res != null && res.statusCode==222){
                    log.error(res.statusCode+":达到或超过半数的接口空值！");
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage("222");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
//                    break;
                }
                //超速
                else if(res != null && res.statusCode==423){
                    log.error(res.statusCode+":超速了！");
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage("423");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
//                    break;
                }
                //其他错误
                else{
                    log.error(res.statusCode+":系统内部错误 and Retry...");
                    log.error(res.content);
                    reLabel.setFlag(false);
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    if(res.statusCode==500&&null!=res.content&&res.content.indexOf("Request speed is out of Limit")!=-1){
                        log.error(res.statusCode+"系统超速");
                        reLabel.setMessage("501");
                    }else{
                        log.error(res.statusCode+":系统内部错误 and Retry...");
                        reLabel.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):"系统内部错误！");
                    }

//                    reLabel.setFlag(false);

                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
                }
//            }
            SourceLogUtil.saveDxInterfaceLog(dxLog);
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            reLabel.setFlag(false);
            reLabel.setMessage("系统内部错误！");
        }
        return reLabel;
    }

}
