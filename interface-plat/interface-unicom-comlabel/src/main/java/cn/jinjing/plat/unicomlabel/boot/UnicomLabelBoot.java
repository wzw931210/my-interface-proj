package cn.jinjing.plat.unicomlabel.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static jodd.util.ThreadUtil.sleep;

public class UnicomLabelBoot {

    public static Log log = LogFactory.getLog(UnicomLabelBoot.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring.xml","dubbo-unicomlabel.xml"});
        context.start();
        log.info("联通公共接口模块服务已经启动...");

        while(true){
            sleep(60*1000*5);
        }
    }

}
