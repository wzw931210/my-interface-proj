package cn.jinjing.inter.controller;

import java.io.BufferedReader;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.jinjing.inter.util.*;
import cn.jinjing.plat.api.entity.InterfaceLog;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.jinjing.inter.pojo.Result;
import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.service.user.UserService;

@Controller
@RequestMapping("/total")
public class YgReceiveController {
	
	@Autowired
	private UserService userService;
	
	private static String teleCom = ConfigUtil.getProperties("YG_teleCom");
	private static String MDN_TYPE = ConfigUtil.getProperties("YG_mdn_type");
	private static String key = ConfigUtil.getProperties("YG_key");
	private static String path = ConfigUtil.getProperties("save_file_path");
	private static Log log = LogFactory.getLog(YgReceiveController.class);
	
	
	@RequestMapping("/{getTotalYG}")
	@ResponseBody
	public String getYgData(@PathVariable("getTotalYG") String label,HttpServletRequest request) {
		StringBuilder reqData = new StringBuilder();
		ReObject ro ;
		JSONObject result = new JSONObject();
		result.put("flag", Result.SUCCESS.getValue());
		String securityKey = key;
		String line;
		String ClearText;
		String userCode;
		String month;
		String accessKey;
		String data;
		String batch_id;//批次号
		long batch_nums; //该批次传输总数
		String batch_sub_id;//子批次号
		long batch_sub_nums;//该子批次号传输总数
		try {
			
			BufferedReader reader = request.getReader();
			while((line = reader.readLine()) != null) {
				reqData.append(line);
			}
			log.info("--------******密文********--:::"+reqData.toString());
			ClearText = AESUtil.decrypt(reqData.toString(), securityKey);
			log.info("--------*******明文*******------"+ClearText);
			Map<String, Object> reqMap = JsonUtil.jsonToMap(ClearText);
			userCode = (String)reqMap.get("userCode");
			month = (String)reqMap.get("month");
			accessKey = (String)reqMap.get("accessKey");
			batch_id = (String)reqMap.get("batch_id");
			batch_nums = Integer.parseInt((String)reqMap.get("batch_nums"));
			batch_sub_id = (String)reqMap.get("batch_sub_id");
			batch_sub_nums = Integer.parseInt((String)reqMap.get("batch_sub_nums"));

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

			JSONObject dataJson = new JSONObject();
			dataJson.put("batch_id",batch_id);
			dataJson.put("batch_nums",batch_nums);
			dataJson.put("batch_sub_id",batch_sub_id);
			dataJson.put("batch_sub_nums",batch_sub_nums);
			result.put("data",dataJson);

			//接收数据
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				List<JSONObject> jsonArray = (List<JSONObject>)reqMap.get("data");
				System.out.println(">>>>>>>>>条数："+jsonArray.size());
				if(jsonArray != null && jsonArray.size()>0) {
					if(batch_sub_nums == jsonArray.size()) {	//接收数量跟报文中数量一致
						String mon = StringUtil.getStrFromDate(new Date(),"yyyyMMdd");
						String tmpName = userCode+"_"+month+"_"+batch_id+"_"+batch_nums+"_"+batch_sub_id+"_"+batch_sub_nums+"_"
								+mon+"_"+batch_id.substring(0,3)+".WAIT";//文件命名
						File tmpFile = new File(path + tmpName);
						if(!tmpFile.exists()) {
							tmpFile.createNewFile();
						}
						log.info("写入文件开始！！！");
						for(JSONObject json : jsonArray) {
							data = json.getString("mdn")+","+json.getString("telCom");
							FileUtil.writeToFile(data, tmpFile);
						}
						String fileName = userCode+"_"+month+"_"+batch_id+"_"+batch_nums+"_"+batch_sub_id+"_"+batch_sub_nums+"_"
								+mon+"_"+batch_id.substring(0,3)+".OK";//文件重命名
						tmpFile.renameTo(new File(path + fileName));
						log.info("写入文件成功！！！");
						//返回结果
						result.put("flag", Result.SUCCESS.getValue());
						result.put("message", "传输成功！");
						result.put("code", "00");
					}else {
						result.put("flag", Result.ERROR.getValue());
						result.put("message", StatusCode.YGNUMERR.getValue());
						result.put("code", StatusCode.YGNUMERR.getCode());
					}
				}else {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", StatusCode.YGNUMERR.getValue());
					result.put("code", StatusCode.YGNUMERR.getCode());
				}
			}
		}catch(Exception e) {
			log.info("接收阳光数据失败！");
			System.out.println(ExceptionUtil.printStackTraceToString(e));
			result.put("flag", Result.ERROR.getValue());
			result.put("message", "接收阳光数据失败！");
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
			System.out.println(result.toString());
			return AESUtil.encrypt(result.toString(), securityKey);
		}else {
			return result.toString();
		}
	}



}
