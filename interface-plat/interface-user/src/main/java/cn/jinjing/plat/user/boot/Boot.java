package cn.jinjing.plat.user.boot;

import cn.jinjing.plat.user.init.ClearKey;
import cn.jinjing.plat.user.init.ProcessLog;
import cn.jinjing.plat.user.init.UpdateLogTable;
import cn.jinjing.plat.user.init.UpdateUserInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static jodd.util.ThreadUtil.sleep;

public class Boot {
    public static Log log = LogFactory.getLog(Boot.class);

    public static void main(String[] args)
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"spring.xml","dubbo-user.xml"});
        context.start();
        log.info("用户模块服务已经启动...");

        //清除过期key
        new ClearKey().start();
        log.info(">>>>>>>>>>>>>>>>>>> clear accessKey start!");

        //更新用户配置参数
        new UpdateUserInfo().start();
        log.info(">>>>>>>>>>>>>>>>>>>> update user info start!");

        //日志入库计费
        new ProcessLog().start();
        log.info(">>>>>>>>>>>>>>>>>>>> log process start!");

        //检查是否需要新建hBase日志表
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new UpdateLogTable(),0,1, TimeUnit.DAYS);

        while(true){
            sleep(60*1000*5);
        }
    }

}
