package com.whut.rpc.core.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.whut.rpc.core.serializer.BasicSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo serializer
 *
 * @author whut2024
 * @since 2024-07-25
 */
public class KryoSerializer implements BasicSerializer {

    private final static ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
       Kryo kryo = new Kryo();

       // don't register all needed class
       kryo.setRegistrationRequired(false);

       return kryo;
    });

    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);

        KRYO_THREAD_LOCAL.get().writeObject(output, object);

        output.close();

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> classType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        Input input = new Input(byteArrayInputStream);

        T result = KRYO_THREAD_LOCAL.get().readObject(input, classType);

        input.close();

        return result;
    }
}
