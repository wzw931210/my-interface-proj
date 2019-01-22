package cn.jinjing.inter.controller;

import cn.jinjing.inter.pojo.Result;
import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.inter.util.*;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.service.user.UserService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sundata")
public class YGSendDataController {

    @Autowired
    private UserService userService;

    private static Log log = LogFactory.getLog(YGSendDataController.class);
    private static String MDN_TYPE = ConfigUtil.getProperties("YG_mdn_type");
    private static String teleCom = ConfigUtil.getProperties("YG_teleCom");
    private static String key = ConfigUtil.getProperties("YG_key");
    private static String resultFilePath = ConfigUtil.getProperties("result_file_path");

    @RequestMapping("/{getSunBasicData}")//getSunDEBData 、getSunBasicData
    @ResponseBody
    public String sendYGData(@PathVariable("getSunBasicData") String label,HttpServletRequest request){
        StringBuilder reqData = new StringBuilder();
        JSONObject result = new JSONObject();
        result.put("flag", Result.SUCCESS.getValue());
        String line;
        String securityKey = key;
        String userCode;
        String month;
        String batch_id;//批次号
        String batch_sub_id;//子批次号
        String ClearText;//明文

        try {
            BufferedReader reader = request.getReader();
            while((line = reader.readLine()) != null) {
                reqData.append(line);
            }

            ClearText = AESUtil.decrypt(reqData.toString(), securityKey);
            System.out.println("--------*******明文*******------"+ClearText);
            Map<String, Object> reqMap = JsonUtil.jsonToMap(ClearText);
            userCode = (String)reqMap.get("userCode");
            month = (String)reqMap.get("month");
            batch_id = (String)reqMap.get("batch_id");
            batch_sub_id = (String)reqMap.get("batch_sub_id");

            Map<String, String> checkMap = new HashMap<>();
            checkMap.put("userCode",userCode);
            checkMap.put("label",label);
            checkMap.put("MDN_TYPE",MDN_TYPE);
            checkMap.put("teleCom",teleCom);
            result = UserCheck.checkUserInfo(checkMap);

            //获取用户缓存信息
            CacheUserLabel cacheUser = null;
            if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
                cacheUser = userService.getUserLabelInfo(userCode,label,MDN_TYPE,teleCom);
                if(cacheUser == null) {
                    System.out.println("获取用户缓存信息失败");
                    result.put("flag", Result.ERROR.getValue());
                    result.put("message", StatusCode.USERINFO.getValue());
                    result.put("code", StatusCode.USERINFO.getCode());
                }
            }

            result.put("batch_id",batch_id);
            result.put("batch_nums",0);
            result.put("month",month);
            result.put("batch_sub_id",batch_sub_id);
            result.put("batch_sub_nums",0);

            if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
                File fileDir = new File(resultFilePath);
                File[] files = fileDir.listFiles();
                boolean flag = false;
                if (files != null && files.length > 0) {
                    if ("getSunBasicData".equals(label)) {//整合基础接口
                        for (File file : files) {
                            String fileName = file.getName();
                            if (fileName.endsWith("U_sun.OK")) {
                                //存在该批次，子批次的文件
                                if (fileName.contains(batch_id) && fileName.contains(batch_sub_id) && fileName.contains(month)) {
                                    String batchSubId = fileName.split("_")[4];
                                    if(batch_sub_id.equals(batchSubId)) {
                                        flag = true;
                                        log.info(">>>>>>>>>>>>>开始读整合文件：" + fileName);
                                        result = FileUtil.readFile2(file);
                                    }
                                }
                            }
                        }
                    } else if ("getSunDEBData".equals(label)){//整合借贷接口
                        for (File file : files) {
                            String fileName = file.getName();
                            if (fileName.endsWith("U_deb.OK")) {
                                //存在该批次，子批次的文件
                                if (fileName.contains(batch_id) && fileName.contains(batch_sub_id) && fileName.contains(month)) {
                                    String batchSubId = fileName.split("_")[4];
                                    if(batch_sub_id.equals(batchSubId)) {
                                        flag = true;
                                        log.info(">>>>>>>>>>>>>开始读借贷文件：" + fileName);
                                        result = FileUtil.readFile2(file);
                                    }
                                }
                            }
                        }
                    }else {
                        result.put("flag", Result.ERROR.getValue());
                        result.put("message", StatusCode.USERINFO.getValue());
                        result.put("code", StatusCode.USERINFO.getCode());
                    }
                    if (!flag) {
                        log.info(">>>>>>>>>>>>>暂无阳光数据结果--" + resultFilePath);
                        System.out.println(">>>>>>>>>>>>>暂无阳光数据结果--");
                        result.put("flag", Result.ERROR.getValue());
                        result.put("code", StatusCode.YGNUMERR1.getCode());
                        result.put("message", StatusCode.YGNUMERR1.getValue());
                    }

                } else {
                    log.info(">>>>>>>>>>>>>阳光结果目录为空--" + resultFilePath);
                    System.out.println(">>>>>>>>>>>>>阳光结果目录为空--");
                    result.put("flag", Result.ERROR.getValue());
                    result.put("code", StatusCode.YGNUMERR1.getCode());
                    result.put("message", StatusCode.YGNUMERR1.getValue());
                }
            }
        }catch(Exception e) {
            log.info(">>>>>>>>>返回阳光数据失败！");
            System.out.println(ExceptionUtil.printStackTraceToString(e));
            result.put("flag", Result.ERROR.getValue());
            result.put("message", StatusCode.SYSERR1.getValue());
            result.put("code", StatusCode.SYSERR1.getCode());
        }finally {
            //记日志
            InterfaceLog interfaceLog = new InterfaceLog();
            interfaceLog.setUserCode("YGBX");
            interfaceLog.setLabelCode(label);
            interfaceLog.setInterCode(MDN_TYPE);

            interfaceLog.setStartTime(new Date());
            interfaceLog.setEndTime(new Date());
            if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
                interfaceLog.setCharge(0.0);
                interfaceLog.setFlag("1");
            }else {
                interfaceLog.setCharge(0.0);
                interfaceLog.setFlag("0");
                interfaceLog.setReason(result.getString("message"));
            }
            try {
                userService.insertLog(interfaceLog);
            } catch (Exception e) {
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
        }
        //返回数据
        if(securityKey != null && ! "".equals(securityKey)) {
//            System.out.println(result.toString());
            return AESUtil.encrypt(result.toString(), securityKey);
        }else {
            return result.toString();
        }

    }

}
