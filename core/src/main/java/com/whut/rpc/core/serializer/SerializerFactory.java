package com.whut.rpc.core.serializer;

import static com.whut.rpc.core.serializer.SerializerKeys.*;

/**
 * singleton serializer factory
 *
 * @author whut2024
 * @since 2024-07-25
 */
public class SerializerFactory {


    static {
        // load serializer's spi
        SpiLoader.load(BasicSerializer.class);
    }


    /**
     * default serializer
     */
    public final static BasicSerializer DEFAULT_SERIALIZER = getSerializer(JDK);


    /**
     * get specified serializer
     */
    public static BasicSerializer getSerializer(String alias) {
        return (BasicSerializer) SpiLoader.get(BasicSerializer.class, alias);
    }

}
