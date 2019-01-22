package cn.jinjing.plat.unicominner.boot;

import cn.jinjing.plat.unicominner.util.Config;
import cn.jinjing.plat.unicominner.util.FindTagConfig;
import cn.jinjing.plat.unicominner.util.UnicomRequesRestriction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.*;

public class UnicomInnerBoot  {

    public static Log log = LogFactory.getLog(UnicomInnerBoot.class);
    ScheduledExecutorService clear_key_pool = Executors.newScheduledThreadPool(5);
    public static void main(String[] args) throws Exception {
        Config.loadClasspathConfig("label-type.properties");
        Config.loadClasspathConfig("label-piece.properties");
        Config.loadClasspathConfig("customized-tags.properties");
        Config.loadClasspathConfig("app-code2name.properties");
        Config.loadClasspathConfig("unicom-config.properties");
        //初始化需要转app名称的标签
        FindTagConfig.initFAT();
        //初始化标签信息
        FindTagConfig.initLabelInfo();
        //清理请求限制key值线程
        new UnicomRequesRestriction().start();
         log.info(">>>>>>>>>>>>>>>>>>> 清理请求限制key值线程 启动!");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"spring.xml","dubbo-unicominner.xml"});
        context.start();
        log.info("联通内部接口模块服务已经启动...");

        // 为保证服务一直开着，利用输入流的阻塞来模拟
        synchronized (UnicomInnerBoot.class) {
            while (true) {
                try {
                    UnicomInnerBoot.class.wait();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
