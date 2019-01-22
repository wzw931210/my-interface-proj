package cn.jinjing.inter.controller;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.jinjing.inter.pojo.Result;
import cn.jinjing.inter.util.AESUtil;
import cn.jinjing.inter.util.CheckUserUtil;
import cn.jinjing.inter.util.ExceptionUtil;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.service.postal.PostalService;
import cn.jinjing.plat.service.user.UserService;

@Controller
@RequestMapping("/addr")
public class AddressController {
	
	public static Log log = LogFactory.getLog(AddressController.class);
	private static String MDN_TYPE = "2";
	private static String TEL_COM = "3";
	
	@Autowired
	private UserService userService;
	@Autowired
	private PostalService postalService;
	
	
	/**
	 * 
	 * @param divLimit 行政区划，查询地址联想时 【必填】
	 * @param  addressList 地址字符串，多个地址用逗号隔开，查询地址联想时只能传一个地址
	 * @return 返回标签值
	 */
	@RequestMapping("/{getAddr}")
	@ResponseBody
	public String getAddress(@PathVariable("getAddr") String label,String userCode, String accessKey,String divLimit,String addressList,String encrypt) {
		Date startTime = new Date();
		JSONObject result = new JSONObject();
		result.put("flag", Result.SUCCESS.getValue());
		ReObject ro = null;
		double price = 0;
		String securityKey = "";
		String addrList = "";
		try {
			
			addrList = URLDecoder.decode(addressList,"utf-8");
		
			
			//验证QPS
			ro = CheckUserUtil.checkQPS(userCode);
			if(ro.isFlag() == false) {
				result.put("flag", Result.ERROR.getValue());
				result.put("message", ro.getMessage());
			}
			
			//获取用户缓存信息
			CacheUserLabel cacheUser = null;
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				cacheUser = userService.getUserLabelInfo(userCode,label,MDN_TYPE,TEL_COM);
				if(cacheUser == null) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", "用户权限不足！");
				}else {
					securityKey = cacheUser.getSecurityKey();
					price = cacheUser.getPrice();
				}
			}
			
			//验证用户权限
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				ro = CheckUserUtil.checkUser(cacheUser, accessKey, label);
				if(ro.isFlag() == false) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", ro.getMessage());
				}
			}
			
			//选择数据源，调接口
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				String dataSource = cacheUser.getDataSource();
				if(dataSource != null && "05".equals(dataSource) ) {
					//邮政地址库
					List<String> list = java.util.Arrays.asList(addrList.split("|"));
					ro = postalService.getPostalLabel(list, label, divLimit);
					if(ro.isFlag() == false) {
						result.put("flag", Result.ERROR.getValue());
						result.put("message", ro.getMessage());
					}else {//调取数据接口成功
						result.put("flag", Result.SUCCESS.getValue());
						if(ro.getMessage() != null) {
							result.put("message", ro.getMessage());
						}
						if(ro.getData() != null) {
							result.put("data", ro.getData());
						}
					}
				}else {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", "没有访问权限");
				}
				
			}
			
		}catch(Exception e) {
			result.put("flag", Result.ERROR.getValue());
			result.put("message", e.getMessage());
			log.error(ExceptionUtil.printStackTraceToString(e));
		}finally {
			//记日志
			InterfaceLog interfaceLog = new InterfaceLog();
			interfaceLog.setUserCode(userCode);
			interfaceLog.setLabelCode(label);
			Map<String, String> map = new HashMap<>();
			map.put("address", addrList);
			interfaceLog.setParams(JSONObject.toJSONString(map));
			interfaceLog.setStartTime(startTime);
			interfaceLog.setEndTime(new Date());
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				interfaceLog.setCharge(price);
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
		if("1".equals(encrypt) && securityKey != null && !"".equals(securityKey)) {
			return AESUtil.encrypt(result.toString(), securityKey);
		}else {
			return result.toString();
		}
	}
	
}