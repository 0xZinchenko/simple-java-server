package letscode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        new Server();

    }
}

class Server {
    private AsynchronousServerSocketChannel server;

    public void bootstrap() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 3000));

            Future<AsynchronousSocketChannel> future = server.accept();
            System.out.println("new client thread");

            AsynchronousSocketChannel clientChannel = future.get(30, TimeUnit.SECONDS);
            while (clientChannel != null && clientChannel.isOpen()) {
                
            }

        } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}