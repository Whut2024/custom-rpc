package com.whut.rpc.core.serializer;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spi for serializer
 *
 * @author whut2024
 * @since 2024-07-25
 */
@Slf4j
public class SpiLoader {

    /**
     * store loaded class,
     * the key is full class name  and
     * the inner map's key is an alias
     */
    private final static Map<String, Map<String, Class<?>>> LOADER_MAP = new ConcurrentHashMap<>();


    /**
     * cache map to avoid instantiating object repetitively and
     * the key is full class name
     */
    private final static Map<String, Object> CACHE_MAP = new ConcurrentHashMap<>();


    /**
     * system file dir
     */
    private final static String SYSTEM_DIR = "META-INF/rpc/system/";


    /**
     * custom file dir
     */
    private final static String CUSTOM_DIR = "META-INF/rpc/custom/";


    /**
     * the road to be scanned
     */
    private final static String[] SCAN_DIR_ARRAY = new String[]{SYSTEM_DIR, CUSTOM_DIR};


    /**
     * dynamic loading class list
     */
    private final static List<Class<?>> LOADER_CALSS_LIST = Collections.singletonList(BasicSerializer.class);


    /**
     * load the specified class's implementation's class and store it in a map
     */
    public static Map<String, Class<?>> load(Class<?> classType) {
        log.info("load the implementation for the class: {}", classType.getName());

        Map<String, Class<?>> classMap = new HashMap<>();
        for (String dir : SCAN_DIR_ARRAY) {
            List<URL> resourceList = ResourceUtil.getResources(dir + classType.getName());

            try {
                for (URL resource : resourceList) {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] splitArray = line.split("=");
                        if (splitArray.length == 2) classMap.put(splitArray[0], Class.forName(splitArray[1]));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                log.error("load specified class fail, the reason is {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        LOADER_MAP.put(classType.getName(), classMap);

        return classMap;
    }


    /**
     * load all specified class's implementation's class
     */
    public static void loadAll() {
        log.info("load all SPI");
        for (Class<?> basicClass : LOADER_CALSS_LIST) {
            load(basicClass);
        }
    }


    /**
     * get instance
     *
     * @param basicClass the basic class for some implementation
     * @param key        the alias for a class (JDK for JdkSerializer, KRYO for KryoSerializer)
     */
    public static Object get(Class<?> basicClass, String key) {

        // get basic class's implementation's class and target class
        Map<String, Class<?>> implClassMap = LOADER_MAP.get(basicClass.getName());

        if (implClassMap == null)
            throw new RuntimeException(String.format("no such basic class(%s) has been load", basicClass.getName()));

        Class<?> targetClass = implClassMap.get(key);

        if (targetClass == null)
            throw new RuntimeException(String.format("no such impl class(%s) has been registered", key));

        // get instant for cache map
        String targetClassName = targetClass.getName();
        if (!CACHE_MAP.containsKey(targetClassName)) {
            synchronized (SpiLoader.class) {
                if (!CACHE_MAP.containsKey(targetClassName)) {
                    try {
                        CACHE_MAP.put(targetClassName, targetClass.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        log.error("instantiate {} class failed", targetClassName);
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return CACHE_MAP.get(targetClassName);
    }
}
