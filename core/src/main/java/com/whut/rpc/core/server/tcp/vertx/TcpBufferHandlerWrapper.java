package com.whut.rpc.core.server.tcp.vertx;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

import static com.whut.rpc.core.constant.ProtocolConstant.*;

/**
 * before a normal handler is invoked, the input buffer has been solved to avoid sticky or half package
 *
 * @author whut2024
 * @since 2024-07-30
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {


    private final RecordParser RECORDER_PARSER;



    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler) {
        RECORDER_PARSER = initParser(bufferHandler);
    }



    private RecordParser initParser(Handler<Buffer> originalBufferHandler) {
        final RecordParser parser = RecordParser.newFixed(MESSAGE_HEADER_LENGTH);

        parser.setOutput(new Handler<Buffer>() {

            private boolean readHeader = true;

            private Buffer inputBuffer = Buffer.buffer();

            @Override
            public void handle(Buffer buffer) {
                if (readHeader) {
                    parser.fixedSizeMode(buffer.getInt(13));
                    inputBuffer.appendBuffer(buffer);
                    readHeader = false;
                } else {
                    parser.fixedSizeMode(MESSAGE_HEADER_LENGTH);
                    inputBuffer.appendBuffer(buffer);
                    readHeader = true;

                    // handler client's input
                    originalBufferHandler.handle(inputBuffer);

                    // resetting it
                    inputBuffer = Buffer.buffer();
                }
            }
        });

        return parser;
    }


    @Override
    public void handle(Buffer buffer) {
        RECORDER_PARSER.handle(buffer);
    }
}
