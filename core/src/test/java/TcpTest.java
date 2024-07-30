import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * use vertx's RecordParser to solve sticky package and half package
 *
 * @author whut2024
 * @since 2024-07-30
 */
@Slf4j
public class TcpTest {

    private static class Server {
        public static void main(String[] args) {
            NetServer server = Vertx.vertx().createNetServer();

            server.connectHandler(socket -> {
                RecordParser parser = RecordParser.newFixed(8);
                AtomicBoolean readHeader = new AtomicBoolean(true);

                parser.setOutput(buffer -> {
                    if (readHeader.get()) {
                        // get the body's length
                        parser.fixedSizeMode(buffer.getInt(4));
                        readHeader.set(false);

                    } else  {
                        log.warn("received a string : {}", buffer.toString());

                        parser.fixedSizeMode(8);
                        readHeader.set(true);
                    }
                });

                socket.handler(parser);
            });


            server.listen(8081, result -> {
                if (result.succeeded()) {
                    log.warn("started successfully");
                } else {
                    log.error("starting failed");
                }
            });

        }
    }


    private static class Client {
        public static void main(String[] args) {
            NetClient client = Vertx.vertx().createNetClient();

            client.connect(8081, "127.0.0.1", connect -> {
                NetSocket socket = connect.result();


                for (int i = 0; i < 100; i++) {
                    Buffer buffer = Buffer.buffer();

                    String str = "abcdefghijklmnopqrstuvwxyz";
                    byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);

                    buffer.appendInt(0);
                    buffer.appendInt(strBytes.length);
                    buffer.appendBytes(strBytes);

                    socket.write(buffer);
                }

                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            });

        }
    }

}
