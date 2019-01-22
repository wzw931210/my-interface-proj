package com.jjdata.batch.util;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Joy.M on 2018/1/16 14:57
 */
public class ConfigUtils {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    private static final Properties ps = new Properties();
    private static final String NaV = "--NA--";

    public ConfigUtils() {
    }

    public static void loadingStdConfig(String[] args, String defaultConfig) throws Exception {
        loadArgs(args);
        loadClasspathConfig(defaultConfig);
        if (hasArg("-c") && hasArgValue("-c")) {
            loadFileSystemConfig(getString("-c"));
        } else {
            System.out.println("[CONFIG-WARN] Not using \"-c\" to specify properties config file");
        }

        if (hasArg("-lc") && hasArgValue("-lc")) {
            String path = getString("-lc");
            if (path.toLowerCase().endsWith(".properties")) {
                PropertyConfigurator.configure(path);
            } else if (path.toLowerCase().endsWith(".xml")) {
                DOMConfigurator.configure(path);
            } else {
                PropertyConfigurator.configure(ConfigUtils.class.getClassLoader().getResource("global.log4j.properties"));
                System.out.println("[CONFIG-WARN] No properties or xml file configured. Using default.");
            }
        }

        dump();
        if (hasArg("-t")) {
            System.out.println("[CONFIG-EXIT] TEST MODE\nWARN: BDCSC NOT START");
            System.exit(0);
        }

    }

    public static void loadArgs(String[] args) throws Exception {
        if (args != null && args.length != 0) {
            String ck = null;

            for(int i = 0; i < args.length; ++i) {
                String v = args[i];
                if (v.startsWith("-")) {
                    if (null != ck) {
                        ps.put(ck, "--NA--");
                    }

                    ck = args[i].trim();
                } else {
                    ps.put(ck, args[i]);
                    ck = null;
                }
            }

            if (null != ck) {
                ps.put(ck, "--NA--");
            }

        }
    }

    public static void loadFileSystemConfig(String fsConfig) throws Exception {
        File f = new File(fsConfig);
        if (!f.exists()) {
            throw new Exception("No Config file found in Filesystem: " + fsConfig);
        } else {
            System.out.println("[CONFIG-INFO] Loading config file from filesystem: " + fsConfig);
            ps.load(new FileInputStream(f));
        }
    }

    public static void loadClasspathConfig(String classPathConfig) throws Exception {
        InputStreamReader is = new InputStreamReader(ConfigUtils.class.getClassLoader().getResourceAsStream(classPathConfig), "UTF-8");
        if (null == is) {
            throw new Exception("No Config file found in Classpath: " + classPathConfig);
        } else {
            System.out.println("[CONFIG-INFO] Loading config file from classpath: " + classPathConfig);
            ps.load(is);
        }
    }

    public static void loadClasspathConfig(String classPathConfig, String defaultConfig) throws Exception {
        try {
            loadClasspathConfig(classPathConfig);
        } catch (Exception var3) {
            logger.info(var3.getMessage(), var3);
            loadClasspathConfig(defaultConfig);
        }

    }

    public static void dump() {
        System.out.println(String.format("%nRunning standard mode! "));
        System.out.println("******************** SYSTEM CONFIGURATION ********************");
        List<String> keys = new ArrayList();
        Iterator i$ = ps.keySet().iterator();

        Object key;
        while(i$.hasNext()) {
            key = i$.next();
            keys.add(key.toString());
        }

        Collections.sort(keys);
        i$ = keys.iterator();

        while(i$.hasNext()) {
            key = i$.next();
            String k = key.toString();
            System.out.println(String.format("%-30s: [%s]", k, getString(k)));
        }

        System.out.println("********************  CONFIGURATION DUMP  ********************\n");
    }

    public static String getString(String key) {
        if (null == key) {
            return "";
        } else {
            String v = ps.getProperty(key);
            if (null != v) {
                return v.trim();
            } else {
                System.out.println("[CONFIG-WARN] No configuration found: " + key);
                return "";
            }
        }
    }

    public static Map<String, String> queryStartWith(String startWith) {
        Map<String, String> map = new HashMap();
        Iterator i$ = ps.keySet().iterator();

        while(i$.hasNext()) {
            Object keyObj = i$.next();
            String key = keyObj.toString();
            if (key.startsWith(startWith)) {
                map.put(key, getString(key));
            }
        }

        return map;
    }

    // 我自定义的，根据是否包含字符串得到key
    public static Map<String,String> queryContains(String charSequence){
        Map<String, String> map = new HashMap();
        Iterator i$ = ps.keySet().iterator();

        while(i$.hasNext()) {
            Object keyObj = i$.next();
            String key = keyObj.toString();
            if (key.contains(charSequence)) {
                map.put(key, getString(key));
            }
        }

        return map;

    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public static long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public static float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    private static String getArg(String arg) {
        return getString(arg);
    }

    private static boolean hasArg(String arg) {
        return ps.containsKey(arg);
    }

    public static boolean hasArgValue(String arg) {
        if (!hasArg(arg)) {
            throw new RuntimeException("No Argument names: " + arg);
        } else {
            return !"--NA--".equals(getArg(arg));
        }
    }
}
