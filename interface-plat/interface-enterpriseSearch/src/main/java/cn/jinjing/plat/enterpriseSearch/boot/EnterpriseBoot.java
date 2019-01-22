package cn.jinjing.plat.enterpriseSearch.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static jodd.util.ThreadUtil.sleep;

public class EnterpriseBoot {

    private static Log log = LogFactory.getLog(EnterpriseBoot.class);

    public static void main(String [] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring.xml","dubbo-enterpriseSearch.xml"});
        context.start();
        log.info("企业搜索接口模块服务已经启动...");

        while(true){
            sleep(10000);
        }
    }
}
