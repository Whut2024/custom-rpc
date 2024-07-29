package com.whut.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * custom transform protocol
 *
 * @author whut2024
 * @since 2024-07-29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    @Data
    public final static class Header {


        /**
         * verify number
         */
        private byte magic;


        private byte version;


        private byte serializer;


        /**
         * is request, response, heart beat or others
         */
        private byte type;

        /**
         * success request failed or response failed
         */
        private byte status;


        private long requestId;


        /**
         * how many bytes are the body
         */
        private int bodyLength;
    }

    /**
     * real message
     */
    private T body;


    /**
     * some tag for this protocol
     */
    private Header header;
}
