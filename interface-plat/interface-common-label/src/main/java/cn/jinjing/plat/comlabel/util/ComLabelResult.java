package cn.jinjing.plat.comlabel.util;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.comlabel.init.ComLabelTokenThread;
import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.SourceLogUtil;
import cn.jinjing.plat.util.StringUtil;
import cn.jinjing.plat.util.UHttpClient;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;

public class ComLabelResult {

    public static Log log = LogFactory.getLog(ComLabelResult.class);

    /**
     * 调电信公共接口
     */
    public static ReLabel getLabelResult (String product,String module,String interfaceCode,HashMap<String,String> httpParams,String userCode){
        ReLabel reLabel = new ReLabel();
        SourceLog dxLog = new SourceLog();
        dxLog.setDataSource("01");
        dxLog.setLabel(interfaceCode);
        dxLog.setUserCode(userCode);
        dxLog.setParams(JSONObject.toJSONString(httpParams));
        dxLog.setStartTime(new Date());
        try{
            //调电信接口
            int k = 3;//错误重试
            while(k-- > 0 ){
//                String reqUrl = ComLabelTokenThread.getReqUrl(product, module, "_"+interfaceCode);
                String reqUrl = ComLabelTokenThread.getReqUrl(product, module, interfaceCode);
                if(StringUtil.isEmpty(reqUrl)){
                    log.error("Token is Null and Retry...");
                    reLabel.setFlag(false);
                    reLabel.setMessage("电信 Token is Null！");
                    continue;
                }
                //发送请求
                UHttpClient.Res res = UHttpClient.doRequest(UHttpClient.Method.get, reqUrl, httpParams, "UTF-8", true);
                dxLog.setEndTime(new Date());
                if(res != null && res.statusCode==200){//成功返回
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(true);
                    JSONObject data = valueMap.getJSONObject("data");
                    Integer value = data.getInteger("value");
                    reLabel.setData(value!=null?Integer.toString(value):"");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    break;
                }else if(res != null && res.statusCode==224){//非本网号码
                    log.error(res.statusCode+":非本网号码！");
                    log.error("非本网号码！");
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage("224");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
                    break;
                }else{//其他错误
                    log.error(res.statusCode+":系统内部错误 and Retry...");
                    log.error(res.content);
                    JSONObject valueMap = JSONObject.parseObject(res.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):"系统内部错误！");
                    dxLog.setStatusCode(Integer.toString(res.statusCode));
                    dxLog.setMessage(valueMap.containsKey("message")?valueMap.getString("message"):null);
                }
            }
            SourceLogUtil.saveDxInterfaceLog(dxLog);
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            reLabel.setFlag(false);
            reLabel.setMessage("系统内部错误！");
        }
        return reLabel;
    }

}
