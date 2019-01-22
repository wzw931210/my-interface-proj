package cn.jinjing.plat.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

public class ConfigUtil 
{
    private static Log log = LogFactory.getLog(ConfigUtil.class);
	private static Resource resource = new ClassPathResource("/config.properties");
    private static Properties props = null;

    public static String getProperties(String key)
    {
        if (null == props)
        {
            try
            {
                props = PropertiesLoaderUtils.loadProperties(resource);
                
            }
            catch (IOException e)
            {
                log.error(ExceptionUtil.printStackTraceToString(e));
            }
        }
        return props.getProperty(key);

    }

}
