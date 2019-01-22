package cn.jinjing.inter.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.jinjing.inter.pojo.Result;
import cn.jinjing.inter.pojo.StatusCode;
import cn.jinjing.inter.util.AESUtil;
import cn.jinjing.inter.util.ExceptionUtil;
import cn.jinjing.inter.util.InterLimit;
import cn.jinjing.plat.api.entity.ReObject;
import cn.jinjing.plat.service.user.UserService;


@Controller
@RequestMapping("/user")
public class LoginController
{
	public static Log log = LogFactory.getLog(LoginController.class);
	
	@Autowired
	UserService loginService;
	
	// 登录
	@RequestMapping("/dologin")
	@ResponseBody
	public String logon(String userCode, String passwd, String encrypt){
		JSONObject result = new JSONObject();
		String securityKey = "";
		
		log.info(">>>>>>>>>>>>>登录qps验证: " + userCode + "--" + passwd + "--" + encrypt);
		ReObject resultRe = new ReObject();
		boolean flag = false;
		if(InterLimit.loginLimit.containsKey(userCode)) {
			if(InterLimit.getLoginPermiss(userCode)){
				flag = true;
			}else{
				resultRe.setMessage("用户每分钟只能登陆一次！");
				resultRe.setCode(StatusCode.LOGINOVERTIMES.getCode());
				flag = false;
			}
		}else {
			resultRe.setMessage("用户不存在！");
			resultRe.setCode(StatusCode.USERORPASSWD.getCode());
			flag = false;
		}
		
		if(flag) {
			log.info(">>>>>>>>>>>>>登录: " + userCode + "--" + passwd + "--" + encrypt);
			ReObject ro;
			try {
				System.out.println("1111111111111" + System.currentTimeMillis());
				ro = loginService.logon(userCode, passwd);
				System.out.println("2222222222222" + System.currentTimeMillis());
				if(ro.getData()==null) {
					ro.setData(new JSONObject());
				}
				else if(ro.getData().containsKey("securityKey")){
					securityKey = ro.getData().getString("securityKey");
					String qps = ro.getData().getString("qps");
					double qpsNum = 0;
					if(qps != null && !"".equals(qps)) {
						qpsNum = Double.parseDouble(qps);
					}
					InterLimit.createLimiter(userCode, qpsNum);
					ro.getData().remove("securityKey");
					ro.getData().remove("qps");
				}
				
				if(ro.isFlag() == false) {
					result.put("flag", Result.ERROR.getValue());
					result.put("message", ro.getMessage());
					result.put("code", ro.getCode());
				} else {
					result.put("flag", Result.SUCCESS.getValue());
					result.put("data", ro.getData());
					result.put("code", ro.getCode());
				}
				
			} catch (Exception e) {
				result.put("flag", Result.ERROR.getValue());
				result.put("message", e.getMessage());
				result.put("code", StatusCode.SYSERR1.getCode());
				log.error(ExceptionUtil.printStackTraceToString(e));
			}
		}else {
			result.put("flag", Result.ERROR.getValue());
			result.put("message", resultRe.getMessage());
			result.put("code", resultRe.getCode());
		}
		//返回加密信息
		if("1".equals(encrypt) && securityKey != null && !"".equals(securityKey)) {
			return AESUtil.encrypt(result.toString(), securityKey);
		}else {
			return result.toString();
		}
	}
	
}
