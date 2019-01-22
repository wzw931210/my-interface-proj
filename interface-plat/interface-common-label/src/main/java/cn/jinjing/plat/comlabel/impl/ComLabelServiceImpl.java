package cn.jinjing.plat.comlabel.impl;

import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.comlabel.pojo.ProvinceMap;
import cn.jinjing.plat.comlabel.util.ComLabelResult;
import cn.jinjing.plat.service.comlabel.ComLabelService;
import cn.jinjing.plat.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ComLabelServiceImpl implements ComLabelService {

    public static Log log = LogFactory.getLog(ComLabelServiceImpl.class);
    private static String LOCAL_TABLE_NAME = ConfigUtil.getProperties("com_label_table");
    private static String DX_COM_LABEL_PRODUCT = ConfigUtil.getProperties("com_label_product");
    private static String DX_COM_LABEL_MODULE = ConfigUtil.getProperties("com_label_module");
    private static String PROVINCECITY_COLUMNNAME = ConfigUtil.getProperties("com_label_provinceCity_columnName");
    private static String ENCRYPT=ConfigUtil.getProperties("dx_com_encrypt");

    private static String COM_MONTH="000000";

    private static String SAVE_KEY=ConfigUtil.getProperties("save_key");//存盘开关

    /**
     * 查询公共接口
     * @param month
     * @param label
     * @param mdn
     * @return
     */
    @Override
    public ReLabel getComLabel(String month, String label, String mdn,String userCode){
        ReLabel rl = new ReLabel(false);
        String labelValue = null;
        String localTableName = LOCAL_TABLE_NAME;
        String product =  DX_COM_LABEL_PRODUCT;
        String module = DX_COM_LABEL_MODULE;
        try {
                //先查询本地库，若查不到再查询电信内部接口
                if(!StringUtil.isEmpty(localTableName) && "true".equals(SAVE_KEY)){
                    try {
                        labelValue = LocalLabelData.getLocalLabel(localTableName, mdn, month, label);
                    }catch (Exception e){
                        log.error(ExceptionUtil.printStackTraceToString(e));
                    }
                    if(!StringUtil.isEmpty(labelValue)){
                        log.info(">>>>>>>>>>本地数据： " + label + "--" + mdn + "--");
                        rl.setFlag(true);
                        rl.setData(labelValue);
                        rl.setMessage(StatusCode.SUCCESS.getValue());
                        rl.setCode(StatusCode.SUCCESS.getCode());
                    }
                }

                if(!rl.isFlag() ){
                    String province = "";
                    if(!"latestCity".equals(label)) {   //准实时位置城市不需要省份
                        //获取省份
//                    String province = "SH";
                        if ("true".equals(SAVE_KEY)) {
                            //先从本地表查询省份信息
                            try {
                                province = LocalLabelData.getLocalLabel(localTableName, mdn, "000000", PROVINCECITY_COLUMNNAME);
                            } catch (Exception e) {
                                log.error(ExceptionUtil.printStackTraceToString(e));
                            }
                        }
                        if (StringUtil.isEmpty(province)) {
                            //本地没有，则调用电信接口获取省份
                            HashMap<String, String> paraMap = new HashMap<>();
                            paraMap.put("mdn", mdn);
                            paraMap.put("type", "MD5");
                            String pro_interfaceCode = PROVINCECITY_COLUMNNAME;
                            ReLabel proLabel = ComLabelResult.getLabelResult(product, module, pro_interfaceCode, paraMap,userCode);
                            String provinceCode = proLabel.getData();
                            if (!StringUtil.isEmpty(provinceCode)) {
                                province = ProvinceMap.provinceMap.get("province_" + provinceCode.substring(0, 2));
                            }
                            if (!StringUtil.isEmpty(province) && "true".equals(SAVE_KEY)) {
                                //保存省份标签到本地库
                                LocalLabelData.putLocalLabel(localTableName, mdn, "000000", PROVINCECITY_COLUMNNAME, province);
                            } else {
                                rl.setFlag(false);
                                rl.setMessage(StatusCode.PROVINCEERR.getValue());
                                rl.setCode(StatusCode.PROVINCEERR.getCode());
                                log.error("获取号码省份失败!");
                                return rl;
                            }
                        }
                    }

                    //调公共接口,并将数据保存到本地Hbase表，下次调用直接先查本地表
                    log.info(">>>>>>>>>>电信数据： " + "--" + label + "--" + mdn);
                    HashMap<String, String> httpParams = new HashMap<>();
                    httpParams.put("mdn", mdn);
                    httpParams.put("type", "MD5");
                    httpParams.put("auth","1");
                    httpParams.put("province",province);
                    if(month != null && !"".equals(month) && !"000000".equals(month)){
                        httpParams.put("month",month);
                    }
                    String interfaceCode = label;
                    ReLabel comLabel = ComLabelResult.getLabelResult(product, module, interfaceCode, httpParams,userCode);
                    if(comLabel.isFlag()){
                        labelValue = comLabel.getData();

                        if(!StringUtil.isEmpty(labelValue)&& "true".equals(SAVE_KEY)){
                            //保存结果数据到本地库
                            LocalLabelData.putLocalLabel(localTableName, mdn, month, label, labelValue);
                        }
                        rl.setFlag(true);
                        rl.setData(labelValue);
                        rl.setMessage(StatusCode.SUCCESS.getValue());
                        rl.setCode(StatusCode.SUCCESS.getCode());
                    }
                    //非本网手机号
                    else if(!comLabel.isFlag() && "224".equals(comLabel.getMessage())) {
                        rl.setFlag(true);
                        rl.setMessage(StatusCode.TELCOMERR.getValue());
                        rl.setCode(StatusCode.TELCOMERR.getCode());
                    }
                    //电信接口内部错误
                    else {
                        rl.setFlag(false);
                        rl.setMessage(StatusCode.SYSERR3.getValue());
                        rl.setCode(StatusCode.SYSERR3.getCode());
                    }
                }
                if(rl.isFlag() && rl.getData()== null && StringUtil.isEmpty(rl.getMessage())){
                    rl.setMessage("no data");
                    rl.setCode(StatusCode.SUCCESS.getCode());
                }
        }catch (Exception e){
            rl.setFlag(false);
            rl.setMessage(StatusCode.SYSERR3.getValue());
            rl.setCode(StatusCode.SYSERR3.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return rl;
    }

    /**
     * 2、3要素验证
     * @param label
     * @param mdn
     * @param name
     * @return
     */
    @Override
    public ReLabel getElement( String label, String mdn,String name,String userCode){
        ReLabel rl = new ReLabel(false);
        String labelValue = null;
        String product =  DX_COM_LABEL_PRODUCT;
        String module = DX_COM_LABEL_MODULE;
        try {
            if(!rl.isFlag()){
                //调公共接口
                log.info(">>>>>>>>>>电信数据： " + "--" + label + "--" + mdn);
                HashMap<String, String> httpParams = new HashMap<>();
                httpParams.put("mdn", mdn);
                httpParams.put("encrypt", ENCRYPT);
                httpParams.put("name", name);
                ReLabel comLabel = ComLabelResult.getLabelResult(product, module, label, httpParams,userCode);
                if(comLabel.isFlag()){
                    labelValue = comLabel.getData();
                    rl.setFlag(true);
                    rl.setData(labelValue);
                    rl.setMessage(StatusCode.SUCCESS.getValue());
                    rl.setCode(StatusCode.SUCCESS.getCode());
                }
                //非本网手机号
                else if(!comLabel.isFlag() && "224".equals(comLabel.getMessage())) {
                    rl.setFlag(true);
                    rl.setMessage(StatusCode.TELCOMERR.getValue());
                    rl.setCode(StatusCode.TELCOMERR.getCode());
                }
                //电信接口内部错误
                else {
                    rl.setFlag(false);
                    rl.setMessage(StatusCode.SYSERR3.getValue());
                    rl.setCode(StatusCode.SYSERR3.getCode());
                }
            }
            if(rl.isFlag() && rl.getData()== null && StringUtil.isEmpty(rl.getMessage())){
                rl.setMessage("no data");
                rl.setCode(StatusCode.SUCCESS.getCode());
            }
        }catch (Exception e){
            rl.setFlag(false);
            rl.setMessage(StatusCode.SYSERR3.getValue());
            rl.setCode(StatusCode.SYSERR3.getCode());
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
        return rl;
    }
}
