package com.whut.rpc.core.serializer;

import java.io.IOException;

/**
 * serializer interface
 * if you want to define yourself serializer, just implement it
 * @author Whut2024
 * @since 2024-07-23
 */
public interface BasicSerializer {

    byte[] serialize(Object object) throws IOException;


    <T> T deserialize(byte[] byteArray, Class<T> classType) throws IOException;
}
