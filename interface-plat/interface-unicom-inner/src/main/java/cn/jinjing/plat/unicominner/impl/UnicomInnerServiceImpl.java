package cn.jinjing.plat.unicominner.impl;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.service.unicominner.UnicomInnerService;
import cn.jinjing.plat.unicominner.pojo.LabelInfo;
import cn.jinjing.plat.unicominner.pojo.LabelValue;
import cn.jinjing.plat.unicominner.util.Config;
import cn.jinjing.plat.unicominner.util.FindTagConfig;
import cn.jinjing.plat.unicominner.util.RuleUtil;
import cn.jinjing.plat.unicominner.util.UnicomInner;
import cn.jinjing.plat.util.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnicomInnerServiceImpl implements UnicomInnerService {

    private static Log log = LogFactory.getLog(UnicomInnerServiceImpl.class);
    private static String LOCAL_UNICOM_INNER_TABLE = ConfigUtil.getProperties("unicom_inner_label_table");
    private static String ORG_CODE = ConfigUtil.getProperties("unicom_org_code");//机构代码
    private static String ORG_PASSWORD = ConfigUtil.getProperties("unicom_org_password");
    private static String PARTNER_CODE = ConfigUtil.getProperties("unicom_inner_dynamic_partner_code");
    private static String PARTNER_KEY = ConfigUtil.getProperties("unicom_inner_dynamic_partner_key");


    private static String SAVE_KEY=ConfigUtil.getProperties("save_key");//存盘开关

    @Override
    public ReLabel getUnicomInnerLabel(String month, String label, String mdn,String userCode){
        ReLabel reLabel = new ReLabel(false);

        try {
            // 约定的标签名开头，指的是客户一次调取所有标签
            if(label.startsWith("total")) {
                Map<String, String> reLabelDataMap = new HashMap<>();
                String labelArray[] = Config.getString("unicominner_customized_tag_" + label).split(",");
                // 获取单个联通接口的值 *** 注意注意，这里的label还要变
                for (String singleLabel : labelArray) {
                    reLabel = this.getSingleLabelValue(month, singleLabel, mdn,userCode);
                    // 如果有一个为错误，报错，所有返回为空。
                    if (!reLabel.isFlag()) {
                        return reLabel;
                    }
                    reLabelDataMap.put(singleLabel, reLabel.getData());
                }

                reLabel.setData(JSON.toJSONString(reLabelDataMap));

            }
            // 调取单个标签
            else {
                reLabel = this.getSingleLabelValue(month, label, mdn,userCode);
            }

        }catch(Exception e){
            reLabel.setFlag(false);
            reLabel.setMessage(StatusCode.SYSERR4.getValue());
            reLabel.setCode(StatusCode.SYSERR4.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return reLabel;
    }

    /***
     * 获取单个标签值
     * @param month
     * @param label
     * @param mdn
     * @return
     */
    public ReLabel getSingleLabelValue(String month, String label, String mdn,String userCode) throws Exception{
        ReLabel reLabel = new ReLabel(false);
        String labelValue = "";
        LabelInfo labelInfoModel=FindTagConfig.MAP_LABEL_INFO_ALL.get(label);
        //判定标签版本
        if(RuleUtil.checkLabelVersion(labelInfoModel.getVersion(),month)){
            //先查询本地表
            if (!StringUtil.isEmpty(LOCAL_UNICOM_INNER_TABLE) && "true".equals(SAVE_KEY)) {
                try {
                    labelValue = LocalLabelData.getLocalLabel(LOCAL_UNICOM_INNER_TABLE, mdn, month, label);
                }catch (Exception e){
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                if (!StringUtil.isEmpty(labelValue)) {
                    log.info(">>>>>>>>>>>>本地数据>>>>>>>>>>" + label + "--" + mdn + "--");
                    reLabel.setFlag(true);
                    reLabel.setData(labelValue);
                    reLabel.setMessage(StatusCode.SUCCESS.getValue());
                    reLabel.setCode(StatusCode.SUCCESS.getCode());
                }
            }else{
                log.info(">>>>>>>>>>>>存盘关闭>>>>>>>>>>" + label + "--" + mdn + "--");
            }

            if (!reLabel.isFlag()) {
                log.info(">>>>>>>>>>联通数据： " + "--" + label + "--" + mdn);
                String labelInfo = Config.getString("unicominner_tag_" + label); // 标签名前加了一个前缀

                // 返回标签的值，象征性的初始化一下。
                LabelValue ltLabel = new LabelValue();

                // 风控标签获取结果
                if (labelInfo.startsWith("risk_control")) {
//                String sequence = CreateKey.createKey(8);
                    String sequence = CreateKey.generateShortUuid8();
                    Map<String, String> httpParams = new HashMap<>();
                    httpParams.put("sendTelNo", mdn);
                    httpParams.put("sequence", sequence);
                    httpParams.put("orgCode", ORG_CODE);
                    String curTime = StringUtil.getStrFromDate(new Date(), "yyyyMMddHHmmss");
                    httpParams.put("curTime", curTime);
                    String md5Str = mdn + "_" + ORG_CODE + "_" + ORG_PASSWORD + "_" + curTime + "_" + sequence;
                    String orgSeq = MD5.cell32(md5Str);
                    httpParams.put("orgSeq", orgSeq);

                    // 月份为额外标记
                    httpParams.put("serData", month);


                    //*** 加入对应的风控 httpParams
                    String tagType = labelInfo.split(",")[1];
                    // 确定请求的
                    httpParams.put("tagType", tagType);
                    // *** 测试专用
                    System.out.println("httpParams:" + httpParams.toString());

                    // 请求联通接口，获取一组标签，包含请求标签在内
                    ltLabel = UnicomInner.getRiskControlLabelResult(httpParams, label, tagType, mdn, month,userCode);

                }
                // 动态标签获取结果
                else if (labelInfo.startsWith("dynamic")) {
                    //*** 加入对应的动态 httpParams
                    Map<String, String> httpParams = new HashMap<>();
                    String curTime = StringUtil.getStrFromDate(new Date(), "yyyyMMddHHmmss");
                    String key = MD5.cell32(PARTNER_KEY + curTime);
                    httpParams.put("key", key);

                    //*** 加入对应的动态 httpParams
                    String tagType = labelInfo.split(",")[1];

                    httpParams.put("apps", tagType);
                    httpParams.put("partnerCode", PARTNER_CODE);
                    httpParams.put("timestamp", curTime);
                    httpParams.put("identity_code", mdn);
                    httpParams.put("identity_id", mdn);
                    httpParams.put("sequence", month);

                    System.out.println("params: " + httpParams);


                    // 请求联通接口，获取一组标签，包含请求标签在内
                    ltLabel = UnicomInner.getDynamicLabelResult(httpParams, label, mdn, month,userCode);
                }
                // 没有匹配到任何类型的标签, 说明出现了错误。
                else{
                    reLabel.setFlag(false);
                    reLabel.setMessage(StatusCode.SYSERR4.getValue());
                    reLabel.setCode(StatusCode.SYSERR4.getCode());
                }

                // 对标签结果处理
                if (ltLabel.isFlag()) {
                    labelValue = ltLabel.getData() != null ? ltLabel.getData() : "";

                    reLabel.setFlag(true);
                    reLabel.setData(labelValue);
                    reLabel.setMessage(StatusCode.SUCCESS.getValue());
                    reLabel.setCode(StatusCode.SUCCESS.getCode());
                } else {
                    reLabel.setFlag(false);
                    reLabel.setMessage(ltLabel.getMessage());
                    reLabel.setCode(ltLabel.getCode());
                }
            }
        }else{
            log.info("【"+label+"】-"+month+" 标签无版本信息....");
            reLabel.setFlag(false);
            reLabel.setData(StatusCode.SYSERR4_8_1.getValue());
            reLabel.setMessage(StatusCode.SYSERR4_8_1.getValue());
            reLabel.setCode(StatusCode.SYSERR4_8_1.getCode());
        }


        return  reLabel;
    }

}

