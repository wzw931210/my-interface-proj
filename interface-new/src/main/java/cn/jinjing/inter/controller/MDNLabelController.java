package cn.jinjing.inter.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.inter.util.AESUtil;
import cn.jinjing.inter.util.CheckParams;
import cn.jinjing.inter.util.CheckUserUtil;
import cn.jinjing.inter.util.ExceptionUtil;
import cn.jinjing.inter.util.StringUtil;
import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.api.entity.InterfaceLog;
import cn.jinjing.plat.api.entity.ReLabel;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.service.comlabel.ComLabelService;
import cn.jinjing.plat.service.digital.DigitalService;
import cn.jinjing.plat.service.shuzun.ShuZunService;
import cn.jinjing.plat.service.unicomlabel.UnicomLabelService;
import cn.jinjing.plat.service.unicominner.UnicomInnerService;
import cn.jinjing.plat.service.user.UserService;

@Controller
@RequestMapping("/mdn/label")
public class MDNLabelController {
	
	private static String MDN_TYPE = "0";

	@Autowired
	private UserService userService;
	@Autowired
	private DigitalService digitalService;
	@Autowired
	private ComLabelService comLabelService;
	@Autowired
	private ShuZunService shuZunService;
	@Autowired
	private UnicomLabelService unicomLabelService;
	@Autowired
	private UnicomInnerService unicomInnerService;
	
	public static DateFormat df = new SimpleDateFormat("yyyyMM");
	public static Log log = LogFactory.getLog(MDNLabelController.class);
	
	@RequestMapping("/{getTag}")
	@ResponseBody
	public String getBasicData(@PathVariable("getTag") String label,String userCode, String accessKey, String mdn, String month,String teleCom, String encrypt){
		
		Date startTime = new Date();
		JSONObject result = new JSONObject();
		result.put("flag", Result.SUCCESS.getValue());
		ReObject ro = null;
		double price = 0;
		String securityKey = "";
		ReLabel rl = null;
		try {
			
			//验证QPS
			ro = CheckUserUtil.checkQPS(userCode);
			if(ro.isFlag() == false) {
				result.put("flag", Result.ERROR.getValue());
				result.put("message", ro.getMessage());
				result.put("code", StatusCode.OVERSPEED.getCode());
			}
			
			//验证参数是否为空
			if(StringUtil.isEmpty(mdn) || StringUtil.isEmpty(month) || StringUtil.isEmpty(teleCom)) {
				result.put("flag", Result.ERROR.getValue());
				result.put("message", "手机号、月份、运营商字段值不能为空！");
				result.put("code", StatusCode.PARAMSERR.getCode());
			}
			
			
			//参数月份
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				ro = CheckParams.checkMonth1(month);
				if(ro.isFlag() == false) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", ro.getMessage());
					result.put("code", StatusCode.OVERMONTH.getCode());
				}
			}
			
			//获取用户缓存信息
			CacheUserLabel cacheUser = null;
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				cacheUser = userService.getUserLabelInfo(userCode,label,MDN_TYPE,teleCom);
				if(cacheUser == null) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", StatusCode.USERINFO.getValue());
					result.put("code", StatusCode.USERINFO.getCode());
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
					result.put("code", ro.getCode());
				}
			}
			
			label = StringUtil.lowerCase(label.substring(3));//去掉get
			log.info(">>>>>>>>>>>>接口名称："+label);
			//选择数据源，调接口
			if(Result.SUCCESS.getValue().equals(result.getString("flag"))) {
				// TODO 数据源选择
				String dataSource = cacheUser.getDataSource();
					
				switch (dataSource) {
				case "00" : rl = digitalService.getLabelResult(label, mdn, month,userCode);break;//电信内部
								
				case "01" : rl = comLabelService.getComLabel(month,label, mdn,userCode);break;//电信公共
								
				case "02" : rl = unicomInnerService.getUnicomInnerLabel(month,label, mdn,userCode);break;//联通内部
								
				case "03" : rl = unicomLabelService.getUnicomLabel(month,label, mdn,userCode);break;//联通公共
								
				case "04" : rl = shuZunService.getShuZunLabel(month, label, mdn,userCode);break;//数尊
								
				default : rl = new ReLabel(false);
							  rl.setMessage("没有访问权限");
							  break;
				}
				if(rl.isFlag() == false) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", rl.getMessage());
					
				}else {//调取数据接口成功
					result.put("flag", Result.SUCCESS.getValue());
					if(rl.getMessage() != null) {
						result.put("message", rl.getMessage());
					}
					if(rl.getData() != null) {
						result.put("data", rl.getData());
					}
				}
				result.put("code", rl.getCode());
			}
			
		}catch(Exception e) {
			result.put("flag", Result.ERROR.getValue());
			result.put("message", e.getMessage());
			result.put("code", StatusCode.SYSERR1.getCode());
			log.error(ExceptionUtil.printStackTraceToString(e));
		}finally {
			//记日志
			InterfaceLog interfaceLog = new InterfaceLog();
			interfaceLog.setUserCode(userCode);
			interfaceLog.setLabelCode("get"+StringUtil.upperCase(label));//首字母大写，加get
			interfaceLog.setInterCode(MDN_TYPE);
			Map<String, String> map = new HashMap<>();
			map.put("mdn", mdn);
			map.put("month", month);
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
