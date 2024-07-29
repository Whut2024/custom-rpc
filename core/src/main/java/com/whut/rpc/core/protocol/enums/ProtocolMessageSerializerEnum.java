package com.whut.rpc.core.protocol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * serializer enum
 * @author whut2024
 * @since 2024-07-29
 */

@Getter
@AllArgsConstructor
public enum ProtocolMessageSerializerEnum {

    KRYO(0, "KRYO"),
    HESSIAN(1, "HESSIAN"),
    JDK(2, "JDK")
    ;


    private final int key;


    private final String value;


    /**
     * get alias list
     */
    public static List<String> getValueList() {
        return Arrays.stream(values()).map(ProtocolMessageSerializerEnum::getValue).collect(Collectors.toList());
    }


    public static ProtocolMessageSerializerEnum getByKey(int key) {
        for (ProtocolMessageSerializerEnum anEnum : values()) {
            if (anEnum.key == key) return anEnum;
        }

        return null;
    }


    public static ProtocolMessageSerializerEnum getByValue(String value) {
        for (ProtocolMessageSerializerEnum anEnum : values()) {
            if(anEnum.value.equals(value)) return anEnum;
        }

        return null;
    }
}
