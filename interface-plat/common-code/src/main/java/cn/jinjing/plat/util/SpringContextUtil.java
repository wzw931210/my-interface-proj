/*
 * 文 件 名:  SpringContextUtil.java
 * 版    权:  Archermind.NANJING 2015-3-6,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  lpf
 * 修改时间:  2015-3-6   上午09:53:04
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package cn.jinjing.plat.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <一句话功能简述>
 * <功能详细描述>
 */
public class SpringContextUtil implements ApplicationContextAware
{
    private static ApplicationContext applicationContext; // Spring应用上下文环境
    
    // 下面的这个方法上加了@Override注解，原因是继承ApplicationContextAware接口是必须实现的方法
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        SpringContextUtil.applicationContext = applicationContext;
    }
    
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    
    public static Object getBean(String name) throws BeansException
    {
        return applicationContext.getBean(name);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getBean(String name, Class requiredType)
            throws BeansException
    {
        return applicationContext.getBean(name, requiredType);
    }
    
    public static boolean containsBean(String name)
    {
        return applicationContext.containsBean(name);
    }
    
    public static boolean isSingleton(String name)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.isSingleton(name);
    }
    
    @SuppressWarnings("rawtypes")
	public static Class getType(String name)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.getType(name);
    }
    
    public static String[] getAliases(String name)
            throws NoSuchBeanDefinitionException
    {
        return applicationContext.getAliases(name);
    }
}
