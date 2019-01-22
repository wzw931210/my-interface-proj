package cn.jinjing.plat.comlabel.boot;

import cn.jinjing.plat.comlabel.init.ComLabelTokenThread;
import cn.jinjing.plat.util.ConfigUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static jodd.util.ThreadUtil.sleep;

public class ComLabelBoot {

    public static Log log = LogFactory.getLog(ComLabelBoot.class);
    public static int TOKEN_UPDATE_MIN = Integer.parseInt(ConfigUtil.getProperties("token_updata_min"));

    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring.xml","dubbo-comlabel.xml"});
        context.start();
        log.info("电信公共接口模块服务已经启动...");

        //更新token的线程
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new ComLabelTokenThread(TOKEN_UPDATE_MIN),0,1, TimeUnit.MINUTES);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>update comLabel token start!");

        while(true){
            sleep(60*1000*5);
        }
    }

}
