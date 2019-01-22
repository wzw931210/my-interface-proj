package cn.jinjing.plat.shuzun.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jodd.util.ThreadUtil.sleep;

public class shuzunBoot {

    private static Log log = LogFactory.getLog(shuzunBoot.class);

    public static void main(String [] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring.xml","dubbo-shuzun.xml"});
        context.start();
        log.info("数尊接口模块服务已经启动...");

        while(true){
            sleep(10000);
        }
    }
}
