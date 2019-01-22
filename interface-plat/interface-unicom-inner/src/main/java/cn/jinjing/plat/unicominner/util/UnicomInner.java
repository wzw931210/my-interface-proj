package cn.jinjing.plat.unicominner.util;

import cn.jinjing.plat.entity.SourceLog;
import cn.jinjing.plat.unicominner.pojo.LabelInfo;
import cn.jinjing.plat.unicominner.pojo.LabelRuleMethodParam;
import cn.jinjing.plat.unicominner.pojo.LabelValue;
import cn.jinjing.plat.unicominner.pojo.UnicomUrlMap;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 联通公共接口
 */
public class UnicomInner {
    private static Log log = LogFactory.getLog(UnicomInner.class);
    private static String SAVE_KEY= ConfigUtil.getProperties("save_key");//存盘开关
    private static String LOCAL_UNICOM_INNER_TABLE = ConfigUtil.getProperties("unicom_inner_label_table"); // 存入hbase
    /**
     * 请求基础接口失败重发请求的次数
     */
    private static int BASICDATA_RESEND_TIMES = Config.getInt("basicdata_resend_times");

    /**
     * 请求超时时间
     */
    private static int INNER_REQUEST_TIMEOUT_TIMES = Config.getInt("inner_request_timeout_times");

    private static LabelValue getInnerLabelResult(Map<String, String> httpParams, String label,String userCode){


        LabelValue reLabel = new LabelValue();
        SourceLog dxLog = new SourceLog();

        // 联通内部接口属于02，数字具体的意义未知。
        dxLog.setDataSource("02");
//        dxLog.setLabel(label);

        String tagName=httpParams.get("apps");
        if(null==tagName){
            tagName=httpParams.get("tagType");
        }
        dxLog.setLabel(tagName);
        dxLog.setUserCode(userCode);
        dxLog.setParams(JSONObject.toJSONString(httpParams));
        dxLog.setStartTime(new Date());
        String reqUrl = "";
        String valueTag = "";
        try{


//            int k = BASICDATA_RESEND_TIMES;//错误重试
//            while(k -- >0){

                // 访问基础接口之前先问下Manager是否允许访问
                //当前请求时间（秒）
                long thisSecond = System.currentTimeMillis();
                //第一次请求时间
                long lastTimeMillis = System.currentTimeMillis();
                //判断是否超时
                boolean isTimeout=false;
                //判断是否还有请求资源
                while(!UnicomRequesRestriction.allowRequest(thisSecond)){
                    Thread.sleep(1000-thisSecond%1000);
                    //如果过了请求处理时间则不再等待
                    if(thisSecond-lastTimeMillis>INNER_REQUEST_TIMEOUT_TIMES){
                        isTimeout=true;
                        break;
                    }
                    thisSecond = System.currentTimeMillis();
                }
                //判断是否超过请求处理时间
                if(isTimeout){
                    log.info("线程忙，请求暂时不能处理："+httpParams);
                    reLabel.setFlag(false);
                    reLabel.setMessage(StatusCode.SYSERR4_9.getValue());
                    reLabel.setCode(StatusCode.SYSERR4_9.getCode());
//                    break;
                }

                reqUrl = UnicomUrlMap.unicomUrlMap.get(label).getUrl();
                valueTag = UnicomUrlMap.unicomUrlMap.get(label).getValueTag();
                //发送请求
                log.info("日志httpParams:"+httpParams);
                UHttpClient.Res result = UnicomHttpClient.doRequest(UHttpClient.Method.post, reqUrl, httpParams, "UTF-8", true);
//                String content = "{\"result\":[{\"tag03\":\"0.0\"}],\"code\":\"200\",\"identity_id\":\"F3526970C67F2E0D635E2A4FC527A2E7\",\"message\":\"success\"}";
//                String content = "{\"result\":[{\"tag13\":\"0.002\"}],\"code\":\"200\",\"identity_id\":\"1A61927CAF124A1CC4B5B01BCEE8AEC8\",\"message\":\"success\"}";
//                String content = "{\"result\":[{\"tag13\":\"0.002\"}],\"code\":\"200\",\"identity_id\":\"6DC6A1A36201FAAF436C39A96414EFB9\",\"message\":\"success\"}";
//                String content = "{\"code\":\"200\",\"errorDesc\":\"查询成功\",\"result\":[{\"tag13\":\"12.345\",\"tag14\":\"12.345\",\"tag15\":\"12.345\",\"tag16\":\"111.3\",\"tag17\":\"222.33\",\"tag18\":\"111.3\",\"tag19\":\"222.33\",\"tag20\":\"111.3\",\"tag21\":\"222.33\",\"tag22\":\"111.3\",\"tag23\":\"222.33\",\"tag24\":\"0.0\",\"tag25\":\"222.33\",\"tag10\":\"0.0\",\"tag11\":\"103.44\",\"tag01\":\"5.0\",\"tag12\":\"33.49\",\"tag02\":\"6.08\",\"tag03\":\"0.0\",\"tag04\":\"0.0\",\"tag05\":\"0.0\",\"tag06\":\"0.0\",\"sequence\":\"201809\",\"tag07\":\"0.0\",\"tag08\":\"0.0\",\"tag09\":\"0.0\"}]}";
//                UHttpClient.Res result = new UHttpClient.Res(200,content);
               log.info("result.content: " + result.content);
                dxLog.setEndTime(new Date());
                JSONObject resultJson = JSONObject.parseObject(result.content);
                if(result.statusCode==200) {//成功返回
                    String status = resultJson.getString("status");
                    String code = resultJson.getString("code");
                    // status=1 && code = "00" 是风控调用成功标记，code=200 是动态调用成功标记
                    if (("1".equals(status) && "00".equals(code)) || code.equals("200")) {//成功 status=1 是风控，code=200 是动态，成功的标志，真是扯，我要不要把它们分开做，不成规矩
                        String valueJson = resultJson.getString(valueTag); //比较扯得一点是，result的里面还有包装

                        reLabel.setData(valueJson != null ? valueJson : "");
                        reLabel.setFlag(true);
                        dxLog.setStatusCode(Integer.toString(result.statusCode));
//                        break;
                    } else {//失败
                        String errorCode = resultJson.getString("code");//错误状态码
                        String errorDesc = resultJson.getString("errorDesc");
                        log.error(status+":调用联通公共接口失败");
                        log.error(result.content);
                        log.error(errorDesc);
                        reLabel.setFlag(false);
                        // 联通风控14错误码status=1 code=14 和动态标签403错误码 code=403，目前只写了后者

                        if("403".equals(code)){
                            reLabel.setCode(StatusCode.SYSERR4_3.getCode());
                            reLabel.setMessage(StatusCode.SYSERR4_3.getValue());
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
//                            break;
                        }else if("1".equals(status) && "14".equals(code)){ // 无效请求,重复再次请求一次
                            log.error("非本网段号码..."+httpParams.get("tagType"));
                            reLabel.setFlag(true);
                            reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4_7.getValue());
                            reLabel.setCode(StatusCode.SYSERR4_7.getCode());
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
//                            break;
                        }else if("408".equals(code)){ // 无效请求,重复再次请求一次
                            log.error("本月此标签未入库..."+httpParams.get("tagType"));
                            reLabel.setFlag(false);
                            reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4_8.getValue());
                            reLabel.setCode(StatusCode.SYSERR4_8.getCode());
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
//                            break;
                        }else if("400".equals(code) ){ // 无效请求,重复再次请求一次
                            log.error("无效请求...");
                            reLabel.setFlag(false);
                            reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4_0.getValue());
                            reLabel.setCode(StatusCode.SYSERR4_0.getCode());
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
                            //记录下日志
                        }else if("2".equals(status) && "15".equals(code)){ // 序列号重复 重复调用
                            log.error("序列号重复 and Retry...");
                            // 重新更新序列号
//                            String sequence = CreateKey.createKey(8);
                            String sequence = CreateKey.generateShortUuid8();
                            httpParams.put("sequence", sequence);

                            reLabel.setFlag(false);
                            reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4.getValue());
                            reLabel.setCode(StatusCode.SYSERR4.getCode());

                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
                            //记录下日志
                        }else if("500".equals(code) ){
                            log.error("联通系统异常...");
                            reLabel.setFlag(false);
                            reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4.getValue());
                            reLabel.setCode(StatusCode.SYSERR4.getCode());
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
//                            break;
                        }else{
                            reLabel.setCode(StatusCode.SYSERR4.getCode());
                            reLabel.setMessage(errorCode+errorDesc);
                            dxLog.setStatusCode(errorCode);
                            dxLog.setMessage(errorDesc);
//                            break;
                        }

                    }
                }else {
                    log.error("系统内部错误 and Retry...");
                    log.error(result.content);
                    reLabel.setFlag(false);
                    reLabel.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : StatusCode.SYSERR4.getValue());
                    reLabel.setCode(StatusCode.SYSERR4.getCode());

                    dxLog.setStatusCode(Integer.toString(result.statusCode));
                    dxLog.setMessage(resultJson.containsKey("message") ? resultJson.getString("message") : null);
                    //记录下日志
                }
//            }
        }catch (Exception e){
            log.error(ExceptionUtil.printStackTraceToString(e));
            reLabel.setFlag(false);
            reLabel.setCode(StatusCode.SYSERR4.getCode());
            reLabel.setMessage("系统内部错误！");
        }
        SourceLogUtil.saveDxInterfaceLog(dxLog);
        return reLabel;
    }

    /**
     * 获取风控标签的值
     * @param httpParams
     * @param label
     * @param tagType
     * @param mdn
     * @param month
     * @return
     */
    public static LabelValue getRiskControlLabelResult(Map<String, String> httpParams, String label,String tagType, String mdn, String month,String userCode){
        LabelValue reLabel = UnicomInner.getInnerLabelResult(httpParams, label,userCode);
        // 调用失败，直接返回。
        if(!reLabel.isFlag()){
            return  reLabel;
        }
        // 调用成功，拆分结果后返回值
        else{
            String pieceRuls[] = Config.getString("unicominner_tagpiece_" + tagType).split("/");  // 拼接规则的数组
            String beforePoint = pieceRuls[0]; // 小数点前的位数
            String behindPoint = pieceRuls[1]; // 小数点后的位数
            String tagArray[] = pieceRuls[2].split(","); // 拼接标签的排序数组
            String tagNumber[] = pieceRuls[3].split(","); // 拼接标签每个有几个数字

            String dataJson = reLabel.getData(); // 联通返回的值

            String data = JSONObject.parseObject(dataJson).getString("checkResult");
            log.info("结果值dataJson->"+dataJson);
            log.info("结果值dataJson->"+data);
            String dataArray[] = data.split("\\.");// 返回结果按小数点拆分
            // 这种是只有一个标签的情况
            if(beforePoint.equals("0")){
                // 存到hbase
                if ( "true".equals(SAVE_KEY)) { // 原来非空才存，现在改为存储所有值
                    LocalLabelData.putLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, label, data);
                }
                reLabel.setData(data);
                return reLabel;

            }else {
                String completeData = StringUtils.leftPad(dataArray[0], Integer.parseInt(beforePoint), "0") + StringUtils.rightPad(dataArray[1], Integer.parseInt(behindPoint), "0"); // 将结果缺0的补0,补全后的结果

                // 平行循环标签和标签位数，从而得出标签值，存盘，更改返回值
                for (int i = 0;i <tagArray.length; i++) {
                    String tag = tagArray[i];
                    String tagValue = completeData.substring(0,Integer.parseInt(tagNumber[i])); // 截取结果
                    completeData = completeData.substring(Integer.parseInt(tagNumber[i]), completeData.length()); // data值为剩余值
                    // 如果这个标签和请求标签一直，将reLabel，即返回标签的data值改为标签值
                    if(tag.equals(label)){
                        reLabel.setData(tagValue);
                    }
                    // 存到hbase
                    if ("true".equals(SAVE_KEY)) {  // 原来非空才存，现在改为存储所有值
                        LocalLabelData.putLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, tag, tagValue);
                    }
                }
                return reLabel;
            }
        }

    }

    /**
     * 获取动态标签的值
     * @param httpParams
     * @param label
     * @param mdn
     * @param month
     * @return
     */
    public static LabelValue getDynamicLabelResult(Map<String, String> httpParams, String label,String mdn, String month,String userCode) {
        LabelValue reLabel = new LabelValue();

        // 如果是top3的标签
        if(label.endsWith("TopThree")){
            String tags[] = httpParams.get("apps").split("\\|");

            // top1和top3前1位
            httpParams.put("apps",tags[0]);
            LabelValue tmpReLabel1 = UnicomInner.getInnerLabelResult(httpParams,label,userCode); // 临时标签值

            // 调用失败，直接返回。
            if(!tmpReLabel1.isFlag()){
                return tmpReLabel1;
            }

            // top2和top3后2位
            httpParams.put("apps",tags[1]);
            LabelValue tmpReLabel2 = UnicomInner.getInnerLabelResult(httpParams, label,userCode); // 临时标签值

            // 调用失败，直接返回。
            if(!tmpReLabel2.isFlag()){
                return tmpReLabel2;
            }
            // 得到联通返回的值 具体到tag01:0.0， {0,0}的值
            String tmpValue1[] = JSONArray.parseArray(tmpReLabel1.getData()).getJSONObject(0).getString(tags[0]).split("\\.");
            String tmpValue2[] = JSONArray.parseArray(tmpReLabel2.getData()).getJSONObject(0).getString(tags[1]).split("\\.");

            // pieceRules的格式为 3/2/getS3EbankAppTopThree/3,2，所以[0]就是小数点前的位数，[1]就是小数点后的位数
            String pieceRules1[] = Config.getString("unicominner_tagpiece_" + tags[0]).split("/");  // 拼接规则的数组
            String pieceRules2[] = Config.getString("unicominner_tagpiece_" + tags[1]).split("/");  // 拼接规则的数组

            // 补0后拼接的值
            String completedData1 = StringUtils.leftPad(tmpValue1[0], Integer.parseInt(pieceRules1[0]), "0") + StringUtils.rightPad(tmpValue1[1], Integer.parseInt(pieceRules1[1]), "0");
            String completedData2 = StringUtils.leftPad(tmpValue2[0], Integer.parseInt(pieceRules2[0]), "0") + StringUtils.rightPad(tmpValue2[1], Integer.parseInt(pieceRules2[1]), "0");

            // 标签真正的值
            String tagValue = "";
            // top1的值
            String tagValueTemp1 = completedData1.substring(0,3);
            tagValueTemp1 = TagValueUtil.handleTagValue(label,tagValueTemp1); // 统一处理标签值
            // top2的值
            String tagValueTemp2 = completedData2.substring(0,3);
            tagValueTemp2 = TagValueUtil.handleTagValue(label,tagValueTemp2); // 统一处理标签值
            //top3的值
            String tagValueTemp3 = completedData1.substring(3,4) + completedData2.substring(3,5);
            tagValueTemp3 = TagValueUtil.handleTagValue(label,tagValueTemp3); // 统一处理标签值

            tagValue = tagValueTemp1 + "|" + tagValueTemp2 + "|" + tagValueTemp3;

            // 存到hbase
            if ("true".equals(SAVE_KEY)) { // 原来 值不为空且top3值不为空 才存，现在改为存储所有值
                //存储TOP3的值
                LocalLabelData.putLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, label, tagValue);
                //存储TOP1的值
                String labelTop1=pieceRules1[2];
                LocalLabelData.putLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, labelTop1, tagValueTemp1);
            }
            reLabel.setData(tagValue);
            reLabel.setFlag(true);

            return  reLabel;
        }
        // 非top3的标签
        else {
            reLabel = UnicomInner.getInnerLabelResult(httpParams, label,userCode);

            // 调用失败，直接返回。
            if (!reLabel.isFlag()) {
                return reLabel;
            }

            // 得到联通返回的值 具体到tag01:0.0， {0,0}的值
//            String tmpValue[] = JSONArray.parseArray(reLabel.getData()).getJSONObject(0).getString(httpParams.get("apps")).split("\\.");
//            JSONArray jsa =JSONArray.parseArray(reLabel.getData());
//            JSONObject jsonObject= jsa.getJSONObject(0);
//            String apps=httpParams.get("apps");
//            String tmvstr=jsonObject.getString(apps);
//            String[] tmpValue=tmvstr.split("\\.");
            String tmpValue[] = JSONArray.parseArray(reLabel.getData()).getJSONObject(0).getString(httpParams.get("apps")).split("\\.");

            // pieceRules的格式为 3/2/getIfBadFinance,getIfMultiFinance,getWorkstabilitylevel,getLifeStabilityLevel,getIncomeLevel/1,1,1,1,1，所以[0]就是小数点前的位数，[1]就是小数点后的位数
            String pieceRules[] = Config.getString("unicominner_tagpiece_" + httpParams.get("apps")).split("/");  // 拼接规则的数组

            // 补0后拼接的值
            String completeData = StringUtils.leftPad(tmpValue[0], Integer.parseInt(pieceRules[0]), "0") + StringUtils.rightPad(tmpValue[1], Integer.parseInt(pieceRules[1]), "0");

            // 标签和对应的标签值列
            String tagArray[] = pieceRules[2].split(","); // 标签列
            String tagNumber[] = pieceRules[3].split(","); // 标签值的规则列

            String tagValue = "";



            // 遍历给标签赋值
            for(int i = 0; i < tagArray.length; i++){
                tagValue = completeData.substring(0,Integer.parseInt(tagNumber[i])); // 截取结果
                tagValue = TagValueUtil.handleTagValue(label,tagValue); // 统一处理标签值
                completeData = completeData.substring(Integer.parseInt(tagNumber[i]), completeData.length()); // data值为剩余值

                tagValue = RuleUtil.checkHandleValue(tagArray[i],tagValue);
                // 存到hbase
                if ("true".equals(SAVE_KEY)) { // 原来 值不为空且top3值不为空 才存，现在改为存储所有值
                    LocalLabelData.putLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, tagArray[i], tagValue);
                }
                // 把结果返回给调用客户
                if (label.equals(tagArray[i])) {
                    reLabel.setData(tagValue);
                }

            }

            return reLabel;
        }
    }



}
