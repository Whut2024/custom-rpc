package com.whut.rpc.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * some utils method about rpc config
 *
 * @author whut2024
 * @since 2024-07-24
 */
public class ConfigUtil {


    public static <T> T loadConfig(String environment, String prefix, Class<?> configClassType) {
        StringBuilder configFileStringBuilder = new StringBuilder("application");

        // append environment config
        if (StrUtil.isNotBlank(environment)) configFileStringBuilder.append("-").append(environment);

        configFileStringBuilder.append(".properties");

        Props props = new Props(configFileStringBuilder.toString());

        return (T) props.toBean(configClassType, prefix);
    }


    public static <T> T loadConfig(String prefix, Class<?> configClassType) {

        return loadConfig(null, prefix, configClassType);
    }

}
