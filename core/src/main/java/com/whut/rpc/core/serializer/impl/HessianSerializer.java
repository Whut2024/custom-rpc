package com.whut.rpc.core.serializer.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.whut.rpc.core.serializer.BasicSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * hessian serializer
 *
 * @author whut2024
 * @since 2024-07-25
 */
public class HessianSerializer implements BasicSerializer {
    @Override
    public byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(byteArrayOutputStream);

        output.writeObject(object);

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> classType) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        HessianInput input = new HessianInput(byteArrayInputStream);

        return (T) input.readObject();
    }
}
