package com.whut.rpc.esay.serializer.impl;

import com.whut.rpc.esay.serializer.BasicSerializer;

import java.io.*;

/**
 * @author whut2024
 * @since 2024-07-23
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
    public <T> T deserialize(byte[] byteArray) throws IOException {
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
