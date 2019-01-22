package cn.jinjing.plat.digital.boot;

import cn.jinjing.plat.digital.init.TokenThread;
import cn.jinjing.plat.util.ConfigUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Boot {

    public static Log log = LogFactory.getLog(Boot.class);
    private static int TOKEN_UPDATA_MIN = Integer.parseInt(ConfigUtil.getProperties("token_updata_min"));
    private static int INTERFACE_COLUMN_MIN = Integer.parseInt(ConfigUtil.getProperties("interface_column_min"));

    public static void main(String[] args)
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring.xml","dubbo-digital.xml"});
        context.start();
        log.info("电信接口模块服务已经启动...");

        //更新token线程
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new TokenThread(TOKEN_UPDATA_MIN), 0, 1, TimeUnit.MINUTES);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>update token start!");

        try {
            while(true){
                Thread.sleep(5*1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
