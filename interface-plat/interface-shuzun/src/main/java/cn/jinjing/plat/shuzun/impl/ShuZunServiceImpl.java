package cn.jinjing.plat.shuzun.impl;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.service.shuzun.ShuZunService;
import cn.jinjing.plat.shuzun.util.ShuZunLabel;
import cn.jinjing.plat.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ShuZunServiceImpl implements ShuZunService {

    private static Log log = LogFactory.getLog(ShuZunServiceImpl.class);
    private static String SHUZUN_LABEL_TABLE = ConfigUtil.getProperties("shuzun_label_table");
    private static String ACCOUNT_ID = ConfigUtil.getProperties("account_id");
    private static String PRIVATE_KEY = ConfigUtil.getProperties("private_key");
    private static String SAVE_KEY=ConfigUtil.getProperties("save_key");//存盘开关

    @Override
    public ReLabel getShuZunLabel(String month, String label, String mdn,String userCode){
        ReLabel reLabel = new ReLabel(false);
        String labelValue = "";

        //先查询本地表
            if (!StringUtil.isEmpty(labelValue) && "true".equals(SAVE_KEY)) {
                try {
                    labelValue = LocalLabelData.getLocalLabel(SHUZUN_LABEL_TABLE, mdn, month, label);
                }catch (Exception e){
                    log.error(ExceptionUtil.printStackTraceToString(e));
                }
                log.info(">>>>>>>>>>>>本地数据>>>>>>>>>>" + label + "--" + mdn + "--");
                reLabel.setFlag(true);
                reLabel.setData(labelValue);
                reLabel.setMessage(StatusCode.SUCCESS.getValue());
                reLabel.setCode(StatusCode.SUCCESS.getCode());
            }

        try {
            //调数尊接口
            if (!reLabel.isFlag()) {
                log.info(">>>>>>>>>>数尊数据： " + "--" + label + "--" + mdn);
                String select = ConfigUtil.getProperties(label);
                String tranNo = StringUtil.getTranNo();
                StringBuilder signStr = new StringBuilder();
                Map<String, String> httpParams = new LinkedHashMap<>();
                httpParams.put("accountID", ACCOUNT_ID);
                httpParams.put("mobile", mdn);
                httpParams.put("select", select);
                httpParams.put("tranNo", tranNo);

                for (String key : httpParams.keySet()) {
                    signStr.append(key);
                    signStr.append(httpParams.get(key));
                }
                signStr.append(PRIVATE_KEY);
                String sign = MD5.cell32(signStr.toString());
                httpParams.put("sign", sign);

                ReLabel result = ShuZunLabel.getLabelResult(httpParams, label,userCode);
//                ReLabel result = new ReLabel();//TODO
//                result.setFlag(true);//TODO
//                result.setData("1");//TODO
                labelValue = result.getData();
                if (result.isFlag()) {
                    if (!StringUtil.isEmpty(labelValue)&& "true".equals(SAVE_KEY)) {
                        //存盘
                        LocalLabelData.putLocalLabel(SHUZUN_LABEL_TABLE, mdn, month, label, labelValue);
                    }
                    reLabel.setFlag(true);
                    reLabel.setData(labelValue);
                    reLabel.setMessage(StatusCode.SUCCESS.getValue());
                    reLabel.setCode(StatusCode.SUCCESS.getCode());
                } else {
                    reLabel.setFlag(false);
                    reLabel.setMessage(StatusCode.SYSERR6.getValue());
                    reLabel.setCode(StatusCode.SYSERR6.getCode());
                    reLabel.setData(result.getData() == null ? result.getData() : "");

                }
            }
            if (reLabel.isFlag() && reLabel.getData() == null && StringUtil.isEmpty(reLabel.getMessage())) {
                reLabel.setMessage("no data");
                reLabel.setMessage(StatusCode.SUCCESS.getValue());
                reLabel.setCode(StatusCode.SUCCESS.getCode());
            }
        }catch(Exception e){
            reLabel.setFlag(false);
            reLabel.setMessage(StatusCode.SYSERR6.getValue());
            reLabel.setCode(StatusCode.SYSERR6.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return reLabel;
    }
    public static void main(String [] args){
        ShuZunServiceImpl shuZunServiceImpl = new ShuZunServiceImpl();
//        shuZunServiceImpl.getShuZunLabel("201807","provinceCity","18964062950");
    }
}
