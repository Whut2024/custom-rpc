package com.whut.rpc.core.protocol.coder;

import com.whut.rpc.core.protocol.ProtocolMessage;

/**
 * @author whut2024
 * @since 2024-07-29
 */
public interface ProtocolMessageCoder<T> {


    T encoder(ProtocolMessage<?> protocolMessage);


    ProtocolMessage<?> decoder(T data);
}
