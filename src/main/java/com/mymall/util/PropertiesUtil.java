package com.mymall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by lamZe on 2017/11/18.<br>
 */
public class PropertiesUtil {
    static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;
    static {
        String fileName = "mmall.properties";
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"utf-8"));
        } catch (IOException e) {
            logger.error("无法加载配置文件",e);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultProperty) {
        String value = properties.getProperty(key.trim());
        if(StringUtils.isBlank(value)) {
            return defaultProperty;
        }
        return value.trim();
    }
}
