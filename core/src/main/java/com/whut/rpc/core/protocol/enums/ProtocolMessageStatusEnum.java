package com.whut.rpc.core.protocol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author whut2024
 * @since 2024-07-29
 */

@Getter
@AllArgsConstructor
public enum ProtocolMessageStatusEnum {


    OK("OK", 0),
    BAD_REQUEST("BAD_REQUEST", 10),
    BAD_RESPONSE("BAD_RESPONSE", 20)
    ;



    private final String text;


    private final int value;



    /**
     * match protocol status by the enum key
     */
    public static ProtocolMessageStatusEnum getByKey(int key) {
        for (ProtocolMessageStatusEnum anEnum : values()) {
            if (anEnum.value == key) return anEnum;
        }

        return null;
    }
}
