package cn.jinjing.plat.unicomlabel.impl;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.service.unicomlabel.UnicomLabelService;
import cn.jinjing.plat.unicomlabel.util.UnicomLabel;
import cn.jinjing.plat.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.swing.tree.ExpandVetoException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UnicomLabelServiceImpl implements UnicomLabelService {

    private static Log log = LogFactory.getLog(UnicomLabelServiceImpl.class);
    private static String ORG_CODE = ConfigUtil.getProperties("unicom_org_code");//机构代码
    private static String ORG_PASSWORD = ConfigUtil.getProperties("unicom_org_password");
    private static String LOCAL_TABLE_NAME = ConfigUtil.getProperties("localTableName");

    private static String SAVE_KEY=ConfigUtil.getProperties("save_key");//存盘开关

    @Override
    public ReLabel getUnicomLabel(String month, String label, String mdn,String userCode){
        ReLabel reLabel = new ReLabel(false);
        String labelValue = "";
        try {
            //先查询本地表
            if (!StringUtil.isEmpty(LOCAL_TABLE_NAME) && "true".equals(SAVE_KEY)) {
                try {
                    labelValue = LocalLabelData.getLocalLabel(LOCAL_TABLE_NAME, mdn, month, label);
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
            }

            //调联通接口
            if (!reLabel.isFlag()) {
                String sequence = CreateKey.createKey(8);
                Map<String, String> httpParams = new HashMap<>();
                httpParams.put("sendTelNo", mdn);
                httpParams.put("sequence", sequence);
                String curTime = StringUtil.getStrFromDate(new Date(), "yyyyMMddHHmmss");
                httpParams.put("curTime", curTime);
                if("averageOfTelephoneBillInRecent3Months".equals(label)){//手机号-近三个月话费均值
                    httpParams.put("month", month);
                    httpParams.put("sendTime", StringUtil.getStrFromDate(new Date(), "yyyyMMddhhmmssSSS"));
                }else{
                    httpParams.put("date", month);
                }
                httpParams.put("orgCode", ORG_CODE);
                String md5Str = mdn + "_" + ORG_CODE + "_" + ORG_PASSWORD + "_" + curTime + "_" + sequence;
                String orgSeq = MD5.cell32(md5Str);
                httpParams.put("orgSeq", orgSeq);
                log.info(">>>>>>>>>>联通数据： " + "--" + label +     "--" + mdn+"--"+httpParams);
                ReLabel ltLabel = UnicomLabel.getLabelResult(httpParams, label,userCode);

                if (ltLabel.isFlag()) {
                    labelValue = ltLabel.getData() != null ? ltLabel.getData() : "";
                    //存入Hbase表
                    if (!StringUtil.isEmpty(labelValue) && "true".equals(SAVE_KEY)) {
                        if(!"realTimeLoc".equals(label)) {//实时位置不用存盘
                            LocalLabelData.putLocalLabel(LOCAL_TABLE_NAME, mdn, month, label, labelValue);
                        }
                    }
                    reLabel.setFlag(true);
                    reLabel.setData(labelValue);
                    reLabel.setMessage(StatusCode.SUCCESS.getValue());
                    reLabel.setCode(StatusCode.SUCCESS.getCode());
                } else {
                    reLabel.setFlag(false);
                    reLabel.setMessage(StatusCode.SYSERR5.getValue());
                    reLabel.setCode(StatusCode.SYSERR5.getCode());
                }
            }
            if (reLabel.isFlag() && reLabel.getData() == null && StringUtil.isEmpty(reLabel.getMessage())) {
                reLabel.setMessage("no data");
                reLabel.setMessage(StatusCode.SUCCESS.getValue());
                reLabel.setCode(StatusCode.SUCCESS.getCode());
            }
        }catch(Exception e){
            reLabel.setFlag(false);
            reLabel.setMessage(StatusCode.SYSERR5.getValue());
            reLabel.setCode(StatusCode.SYSERR5.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return reLabel;
    }


    /**
     * 三要素核验
     * @param mdn MD5格式手机号
     * @param certType 证件类型
     * @param certCode 证件号
     * @param userName 姓名
     * @return 核验结果
     */
    @Override
    public ReLabel get3Elements(String label,String mdn,String certType,String certCode,String userName ,String userCode){
        ReLabel reLabel = new ReLabel(false);
        String labelValue = "";
        try {
            //调联通接口

//            String sequence = CreateKey.createKey(8);
            String sequence = CreateKey.generateShortUuid8();
            Map<String, String> httpParams = new HashMap<>();
            httpParams.put("sendTelNo", mdn);
            httpParams.put("certType", certType);
            httpParams.put("certCode", certCode);
            httpParams.put("username", userName);
            httpParams.put("sequence", sequence);
            httpParams.put("orgCode", ORG_CODE);
            String curTime = StringUtil.getStrFromDate(new Date(), "yyyyMMddHHmmss");
            httpParams.put("curTime", curTime);
            String md5Str = mdn + "_" + ORG_CODE + "_" + ORG_PASSWORD + "_" + curTime + "_" + sequence;
            String orgSeq = MD5.cell32(md5Str);
            httpParams.put("orgSeq", orgSeq);
            log.info(">>>>>>>>>"+ label + "--" + userName + "--" + mdn);
            ReLabel ltLabel = UnicomLabel.getLabelResult(httpParams, label,userCode);
            if (ltLabel.isFlag()) {
                labelValue = ltLabel.getData() != null ? ltLabel.getData() : "";
                reLabel.setFlag(true);
                reLabel.setData(labelValue);
                reLabel.setMessage(StatusCode.SUCCESS.getValue());
                reLabel.setCode(StatusCode.SUCCESS.getCode());
            }else {
                reLabel.setFlag(false);
                reLabel.setMessage(StatusCode.SYSERR7.getValue());
                reLabel.setCode(StatusCode.SYSERR7.getCode());
            }
        }catch(Exception e){
            reLabel.setFlag(false);
            reLabel.setMessage(StatusCode.SYSERR7.getValue());
            reLabel.setCode(StatusCode.SYSERR7.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }

        return reLabel;
    }
}

