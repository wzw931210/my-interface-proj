package cn.jinjing.inter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class ConfigUtil 
{
    private static Log log = LogFactory.getLog(ConfigUtil.class);
	private static Resource resource = new ClassPathResource("/config.properties");
    private static Resource label = new ClassPathResource("/label.properties");
    private static Resource debit = new ClassPathResource("/debit.properties");
    private static Properties props = null;
    private static Properties labelProps = null;
    private static Properties debitProps = null;
    
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

    public static String getLabelValue(String key){
        if(labelProps == null ){
            getLabelProps();
        }
        return labelProps.getProperty(key);
    }
    public static Set<String> getLabelKey(){
        if(labelProps == null ){
            getLabelProps();
        }
        return labelProps.stringPropertyNames();
    }

    private static void getLabelProps(){
        try{
            labelProps = PropertiesLoaderUtils.loadProperties(label);
        }catch (IOException e){
            e.printStackTrace();
            log.error(">>>>>>>>label.properties error!");
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
    }

    public static String getDebitValue(String key){
        if(debitProps == null ){
            getDebitProps();
        }
        return debitProps.getProperty(key);
    }
    public static Set<String> getDebitKey(){
        if(debitProps == null ){
            getDebitProps();
        }
        return debitProps.stringPropertyNames();
    }

    private static void getDebitProps(){
        try{
            debitProps = PropertiesLoaderUtils.loadProperties(debit);
        }catch (IOException e){
            e.printStackTrace();
            log.error(">>>>>>>>debit.properties error!");
            log.error(ExceptionUtil.printStackTraceToString(e));
        }
    }
    
    /**
     * 判断字符串是否为空
     * 
     * @param str
     * @return
     */
    public static boolean isNull(String str)
    {
        return (null == str || "".equals(str.trim())) ? true : false;
    }
}
