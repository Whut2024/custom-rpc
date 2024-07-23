package com.whut.rpc.esay.serializer;

import java.io.IOException;

/**
 * @author Whut2024
 * @since 2024-07-23
 */
public interface BasicSerializer {

    byte[] serialize(Object object) throws IOException;


    <T> T deserialize(byte[] byteArray) throws IOException;
}
