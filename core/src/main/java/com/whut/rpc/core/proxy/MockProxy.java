package com.whut.rpc.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * mock proxy process logic
 *
 * @author whut2024
 * @since 2024-07-24
 */
public class MockProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnClassType = method.getReturnType();

        return getDefaultMockResult(returnClassType);
    }

    /**
     * generate default result for different class type
     */
    private Object getDefaultMockResult(Class<?> returnClassType) {
        if (returnClassType == byte.class || returnClassType == short.class ||
                returnClassType == int.class || returnClassType == long.class ||
                returnClassType == char.class || returnClassType == float.class ||
                returnClassType == double.class) return 0;

        if (returnClassType == boolean.class)  return true;

        try {
            return returnClassType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }
}
