package cn.jinjing.plat.digital.util;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.digital.pojo.LabelInfo;
import cn.jinjing.plat.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LabelUtil {

    public static Log log = LogFactory.getLog(LabelUtil.class);
    private static String LABEL_INTERFACE = ConfigUtil.getProperties("labelInterface");
    private static String FK_INTERFACE = ConfigUtil.getProperties("fkInterface");
    private static String SAVE_KEY=ConfigUtil.getProperties("save_key");

    public static ReLabel getLabel(String column, String mdn, String month, String localTableName, String product, String module,String userCode){
        ReLabel ro = new ReLabel(false);
        String labelValue = null;
        try {

            if("true".equals(SAVE_KEY)){
                //1.如果开关为开，先查本地表
                String rowKey = mdn + "_" + month + "_" + column;
                //本地数据表名不为空时查询本地库
                if (!StringUtil.isEmpty(localTableName)) {
                    try {
                        labelValue = LocalLabelData.getLocalLabel(localTableName, mdn, month, column);
                    }catch (Exception e){
                        log.error(ExceptionUtil.printStackTraceToString(e));
                    }
                    if (labelValue != null && !labelValue.isEmpty()) {
                        log.info(">>>>>>>>>>查本地数据： " + column + "--" + mdn + "--" + month);
                        ro.setFlag(true);
                        ro.setData(labelValue);
                        ro.setMessage(StatusCode.SUCCESS.getValue());
                        ro.setCode(StatusCode.SUCCESS.getCode());
                    }
                }
            }
            //2.调电信接口
            //查询电信接口前先判断号码，本地号码表名不为空时查询本地号码库，判断号码是否为电信号码
   /*         if (!ro.isFlag() && !LocalLabelData.existsMdn(mdn)) {
                ro.setFlag(false);
                ro.setMessage("本地号码表不存在该号码");
            }
            else */
            if (!ro.isFlag()){
                log.info(">>>>>>>>>>查电信数据： " + "--" + column + "--" + mdn + "--" + month);
                String typeAndLength = LabelMap.labelTypeMap.get(column);
                log.info(">>>>>>>>>>typeAndLength：" + typeAndLength);
                List<LabelInfo> labelList = LabelMap.typeLabelMap.get(typeAndLength);
                String type = typeAndLength.split("_")[0];//接口编号
                int length = Integer.parseInt(typeAndLength.split("_")[1]);//返回结果的长度
                int intType = Integer.parseInt(type);
                String tag = LABEL_INTERFACE;
                if(intType >= 1 && intType <= 32 ){
                    tag = FK_INTERFACE;
                }

                //调用电信接口
                HashMap<String, String> httpParams = new HashMap<>();
                String uuid = UUID.randomUUID().toString();
                httpParams.put("uuid", uuid);
                httpParams.put("uid", mdn);
                httpParams.put("month", month);
                httpParams.put("type", type);
                ReLabel dxLabel = DxLabelResult.getLabelResult(product, module, tag, httpParams,userCode);
                //3.封装返回值
                if (dxLabel.isFlag()) {
                    labelValue = dxLabel.getData();
                    if(!StringUtil.isEmpty(labelValue)){
                        //处理返回值，并存盘
                        labelValue = ValueUtil.splitAndSave(labelList,column,labelValue,localTableName,length,mdn,month);
                    }
                    ro.setFlag(true);
                    ro.setData(labelValue);
                    ro.setMessage(StatusCode.SUCCESS.getValue());
                    ro.setCode(StatusCode.SUCCESS.getCode());
                }
                //非本网手机号
                else if(!dxLabel.isFlag() && "224".equals(dxLabel.getMessage())) {
                    ro.setFlag(true);
                    ro.setMessage(StatusCode.TELCOMERR.getValue());
                    ro.setCode(StatusCode.TELCOMERR.getCode());
                }
                //超速
                else if(!dxLabel.isFlag() && "423".equals(dxLabel.getMessage())) {
                    ro.setFlag(false);
                    ro.setMessage(StatusCode.SYSERR2_1.getValue());
                    ro.setCode(StatusCode.SYSERR2_1.getCode());
                }
                //超速
                else if(!dxLabel.isFlag() && "501".equals(dxLabel.getMessage())) {
                    ro.setFlag(false);
                    ro.setMessage(StatusCode.SYSERR2_2.getValue());
                    ro.setCode(StatusCode.SYSERR2_2.getCode());
                }
                //电信接口内部错误
                else {
                    ro.setFlag(false);
                    ro.setMessage(StatusCode.SYSERR2.getValue());
                    ro.setCode(StatusCode.SYSERR2.getCode());
                }
            }
            if(ro.isFlag() && (ro.getData()== null||("".equals(ro.getData()))) && StringUtil.isEmpty(ro.getMessage())){
                ro.setMessage("no data");
                ro.setCode(StatusCode.SUCCESS.getCode());
            }
        }catch (Exception e){
            ro.setFlag(false);
            ro.setMessage(StatusCode.SYSERR2.getValue());
            ro.setCode(StatusCode.SYSERR2.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return ro;
    }

    public static void main(String[] args) {
        String typeAndLength = LabelMap.labelTypeMap.get("cusAAppNewTag");
        System.out.println(">>>>>>>>>>typeAndLength：" + typeAndLength);
    }

}
