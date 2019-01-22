package cn.jinjing.inter.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.jinjing.plat.service.user.UserService;

/**
 * 用户每分钟登陆次数
 *
 */
public class UpdateLimit extends Thread {
	
	public static Log log = LogFactory.getLog(UpdateLimit.class);
	
	private static double LOGIN_QPS = Double.parseDouble(ConfigUtil.getProperties("login_qps"));
	
	private UserService userService = (UserService) SpringContextUtil.getBean("userService");

	@Override
	public void run() {
		try {
			log.info("UPDATE USER LOGIN LIMIT");
			List<String> list = userService.getAllUserCode();
			if(list != null && list.size() > 0) {
				for(String userCode : list) {
					InterLimit.loginLimiter(userCode, LOGIN_QPS);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
