package cn.jinjing.plat.user.init;

import java.util.List;

import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.user.dao.UserInfoMapper;
import cn.jinjing.plat.user.pojo.UserInfo;
import cn.jinjing.plat.user.util.CacheUtil;
import cn.jinjing.plat.util.ConfigUtil;
import cn.jinjing.plat.util.ExceptionUtil;
import cn.jinjing.plat.util.SpringContextUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdateUserInfo extends Thread{

	private UserInfoMapper userDao = (UserInfoMapper) SpringContextUtil.getBean("userInfoMapper");
	public static Log log = LogFactory.getLog(UpdateUserInfo.class);

	private static int USER_UPDATA_MIN = Integer.parseInt(ConfigUtil.getProperties("user_updata_min"));
	
	@Override
	public void run() {

		while(true)
		{
			Long millis;
			try {
				millis = CacheUtil.getCacheUserUpdateTime();
				if(System.currentTimeMillis() - millis > USER_UPDATA_MIN * 60 * 1000){
					//更新缓存时间
					CacheUtil.setCacheUserUpdateTime();
					//查询所有可用用户
					List<UserInfo> users = userDao.queryAllUser();
					List<CacheUserLabel> labels ;
					for(UserInfo user : users){
						//添加每个用户的标签授权信息到缓存
						labels = userDao.queryUserLabels(user.getUserCode());
						if(labels!=null && labels.size()>0)
						{
							log.info(">>>>>>>>>>>>>定时添加用户配置--" + user.getUserCode() + ":" + labels.size());
							CacheUtil.addUserLabels(user, labels);
						}
					}
				}
			}catch (Exception e){
				log.error(ExceptionUtil.printStackTraceToString(e));
			}
			try {
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
				log.error(ExceptionUtil.printStackTraceToString(e));
			}
		}
	}
	
}
