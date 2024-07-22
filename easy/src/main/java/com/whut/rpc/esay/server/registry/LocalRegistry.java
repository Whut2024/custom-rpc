package com.whut.rpc.esay.server.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * local registry center
 * store service class message
 */
public class LocalRegistry {

    private final static Map<String, Class<?>> SERVICE_MAP = new ConcurrentHashMap<>();

    public static Class<?> getService(String ServiceName) {
        return SERVICE_MAP.get(ServiceName);
    }

    public static void addService(String ServiceName, Class<?> clazz) {
        SERVICE_MAP.put(ServiceName, clazz);
    }

    public void removeService(String ServiceName) {
        SERVICE_MAP.remove(ServiceName);
    }
}
