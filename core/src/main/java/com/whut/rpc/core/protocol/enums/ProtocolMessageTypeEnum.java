package com.whut.rpc.core.protocol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author whut2024
 * @since 2024-07-29
 */


@Getter
@AllArgsConstructor
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART(2),
    OTHERS(3);


    private final int key;


    /**
     * match protocol message type by the enum key
     */
    public static ProtocolMessageTypeEnum getByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : values()) {
            if (anEnum.key == key) return anEnum;
        }

        return null;
    }

}
