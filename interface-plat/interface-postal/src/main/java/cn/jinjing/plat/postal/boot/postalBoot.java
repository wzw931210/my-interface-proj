package cn.jinjing.plat.postal.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jodd.util.ThreadUtil.sleep;

public class postalBoot {

    private static Log log = LogFactory.getLog(postalBoot.class);

    public static void main(String [] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring.xml","dubbo-postal.xml"});

        context.start();
        log.info("邮政地址库接口模块服务已经启动...");

        while(true){
            sleep(10000);
        }
    }
}
