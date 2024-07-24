package com.whut.rpc.core.serializer;

import java.io.IOException;

/**
 * @author Whut2024
 * @since 2024-07-23
 *
 * serializer interface
 * if you want to define yourself serializer, just implement it
 */
public interface BasicSerializer {

    byte[] serialize(Object object) throws IOException;


    <T> T deserialize(byte[] byteArray) throws IOException;
}
