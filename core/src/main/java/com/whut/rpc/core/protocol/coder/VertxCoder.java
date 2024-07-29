package com.whut.rpc.core.protocol.coder;

import com.whut.rpc.core.model.RpcRequest;
import com.whut.rpc.core.model.RpcResponse;
import com.whut.rpc.core.protocol.ProtocolMessage;
import com.whut.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import com.whut.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import com.whut.rpc.core.serializer.BasicSerializer;
import com.whut.rpc.core.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

import static com.whut.rpc.core.constant.ProtocolConstant.*;

/**
 * @author whut2024
 * @since 2024-07-29
 */
public class VertxCoder implements ProtocolMessageCoder<Buffer> {

    private VertxCoder() {
    }

    private volatile static VertxCoder vertxCoder;

    public static VertxCoder getInstance() {
        if (vertxCoder == null) {
            synchronized (VertxCoder.class) {
                if (vertxCoder == null) vertxCoder = new VertxCoder();
            }
        }

        return vertxCoder;
    }


    @Override
    public Buffer encoder(ProtocolMessage<?> protocolMessage) {
        Buffer buffer = Buffer.buffer();
        if (protocolMessage == null) return buffer;

        ProtocolMessage.Header header = protocolMessage.getHeader();

        // get the serializer
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByKey(header.getSerializer());
        if (serializerEnum == null) throw new RuntimeException("no such specified serializer");
        BasicSerializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());

        // add message to header by order to read it by order
        buffer.appendByte(header.getMagic())
                .appendByte(header.getVersion())
                .appendByte(header.getSerializer())
                .appendByte(header.getType())
                .appendByte(header.getStatus())
                .appendLong(header.getRequestId());

        byte[] bodyByteArray;
        try {
            bodyByteArray = serializer.serialize(protocolMessage.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.appendInt(bodyByteArray.length).appendBytes(bodyByteArray);

        // return the buffer
        return buffer;
    }


    @Override
    public ProtocolMessage<?> decoder(Buffer buffer) {
        byte magic = buffer.getByte(0);
        if (magic != PROTOCOL_MAGIC) throw new RuntimeException("this message's magic is illegal");

        // read data from buffer by order
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // get body bytes from buffer precisely
        byte[] bodyByteArray = buffer.getBytes(MESSAGE_HEADER_LENGTH, MESSAGE_HEADER_LENGTH + header.getBodyLength());

        // get serializer
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getByKey(header.getSerializer());
        if (serializerEnum == null) throw new RuntimeException("no such specified serializer");
        BasicSerializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());

        Object bodyObject;

        // serializer protocol message type
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getByKey(header.getType());
        if (messageTypeEnum == null) throw new RuntimeException("no such message type");
        try {
            switch (messageTypeEnum) {
                case REQUEST:
                    bodyObject = serializer.deserialize(bodyByteArray, RpcRequest.class);
                    break;
                case RESPONSE:
                    bodyObject = serializer.deserialize(bodyByteArray, RpcResponse.class);
                    break;
                case HEART:
                case OTHERS:
                default:
                    throw new RuntimeException("don't support this message body type this time");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ProtocolMessage<>(bodyObject, header);
    }
}
