package letscode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class Server {
    private final static int BUFFER_SIZE = 256;
    private AsynchronousServerSocketChannel server;
    private final HttpHandler handler;
    private final String HEADERS =
            "HTTP/1.1 200 OK\r\n" +
                    "Server: silver\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: %s\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

    Server(HttpHandler handler) {
        this.handler = handler;
    }

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 3000));

            while (true) {
                Future<AsynchronousSocketChannel> future = server.accept();
                handleClient(future);
            }
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Future<AsynchronousSocketChannel> future) throws InterruptedException, ExecutionException, TimeoutException, IOException {
        System.out.println("new client connection");

        AsynchronousSocketChannel clientChannel = future.get();

        while (clientChannel != null && clientChannel.isOpen()) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            StringBuilder builder = new StringBuilder();
            boolean keepReading = true;

            while (keepReading) {
                int readResult = clientChannel.read(buffer).get();

                keepReading = readResult == BUFFER_SIZE;
                buffer.flip();

                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer);
                builder.append(charBuffer);

                buffer.clear();
            }

            HttpRequest request = new HttpRequest(builder.toString());
            HttpResponse response = new HttpResponse();

            String body = this.handler.handle(request, response);

            String page = String.format(HEADERS, body.length()) + body;
            ByteBuffer resp = ByteBuffer.wrap(page.getBytes());
            clientChannel.write(resp);


            clientChannel.close();
        }
    }

}
