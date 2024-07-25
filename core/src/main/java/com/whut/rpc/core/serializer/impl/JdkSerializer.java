package com.whut.rpc.core.serializer.impl;

import com.whut.rpc.core.serializer.BasicSerializer;

import java.io.*;

/**
 * @author whut2024
 * @since 2024-07-23
 * <p>
 * a serializer based on JDK API
 */
public class JdkSerializer implements BasicSerializer {
    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();

        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> classType) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        try {
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            objectInputStream.close();
        }
    }
}
